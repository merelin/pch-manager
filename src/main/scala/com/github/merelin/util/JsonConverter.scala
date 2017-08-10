package com.github.merelin.util

import java.io.Writer

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization._

object JsonConverter {
  implicit val fmt = formats(NoTypeHints)

  def toJson(s: String): JValue = parse(s)
  def fromJson(json: JValue): String = compact(render(json))

  def writeTo[A <: AnyRef](a: A)(implicit formats: Formats): String = write(a)
  def writeTo[A <: AnyRef, W <: Writer](a: A, w: W)(implicit formats: Formats): W = write(a, w)
  def readFrom[A <: AnyRef](jsonInput: JsonInput)(implicit formats: Formats, manifest: Manifest[A]): A = read(jsonInput)

  def main(args: Array[String]) = {
    val xml = scala.xml.XML.loadString(
      """
        |<theDavidBox>
        |  <request>
        |    <arg0>get_video_zoom</arg0>
        |    <arg1>get_language</arg1>
        |    <arg2>get_resolution</arg2>
        |    <arg3>get_display_mode</arg3>
        |    <arg4>get_audio_level</arg4>
        |    <module>setting</module>
        |  </request>
        |  <response>
        |    <videoZoom>actual size</videoZoom>
        |  </response>
        |  <returnValue>0</returnValue>
        |</theDavidBox>
      """.stripMargin
    )

    case class Document(theDavidBox: TheDavidBox)
    case class TheDavidBox(request: Request, response: Response, returnValue: String)

    case class Request(args: List[String], module: String)
//    case class Request(arg0: String, module: String)

//    case class Response(items: List[AnyRef])
    case class Response(videoZoom: String)

    val json = Xml.toJson(xml)
//    println(json)

    var args = List.empty[JValue]
    json.transformField {
      case (name, value) if name.matches("arg[0-9]+") =>
        args :+= value
        name -> value
      case (name, value) => name -> value
    }

    val jsonFixed = json.transformField {
      case ("request", value) => "request" -> (JObject(JField("arg", JArray(args))) merge value)
      case (name, value) => name -> value
    }.removeField { case (name, value) => name.matches("arg[0-9]+") }
    println(fromJson(jsonFixed))
//    println(Xml.toXml(jsonFixed))
//    println(fromJson(Xml.toJson(Xml.toXml(jsonFixed))))

//    val jsonAsString = fromJson(json)
//    println(jsonAsString)
//    println(writeTo[Document](Document(TheDavidBox(Request(List("get_video_zoom"), "setting"), Response("actual size"), "0"))))
//    println(readFrom[Document](StringInput(jsonAsString)))
//    println(Xml.toXml(json) == xml)
  }
}

package com.github.merelin.util

import com.github.merelin.thedavidbox._
import com.thoughtworks.xstream._
import com.thoughtworks.xstream.converters.basic.StringConverter
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter
import com.thoughtworks.xstream.converters.{Converter, MarshallingContext, UnmarshallingContext}
import com.thoughtworks.xstream.core.util.HierarchicalStreams
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper._
import com.thoughtworks.xstream.io.{HierarchicalStreamReader, HierarchicalStreamWriter}
import com.thoughtworks.xstream.io.xml.DomDriver
import com.thoughtworks.xstream.mapper.Mapper

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

case class Topping(name: String)

case class Pizza(crustSize: Int, crustType: String, toppings: List[Topping] = Nil
//                 ArrayBuffer[Topping] = ArrayBuffer[Topping]()
//                   ListBuffer[Topping] = ListBuffer[Topping]()
//                   Seq[Topping] = Seq()
//                   List[Topping] = Nil
//                   Set[Topping] = Set.empty[Topping]
//                   Map[Topping, Topping] = Map.empty[Topping, Topping]
                ) {
  def +(t: Topping): Pizza = copy(toppings = toppings :+ t)
}

object XStreamTest {
  def main(args: Array[String]): Unit = {
    var p = Pizza(14, "Thin")
    p += Topping("cheese")
    p += Topping("sausage")

    val xstream = XStreamConversions(new XStream(new DomDriver))
//    xstream.alias("topping", classOf[Topping])
//    xstream.alias("pizza", classOf[Pizza])
//
//    val xml = xstream.toXML(p)
//    println(
//      s"""
//        |${xml}
//        |
//        |${p} =${p == xstream.fromXML(xml)}= ${xstream.fromXML(xml)}
//      """.stripMargin
//    )
//
//    val tuple = (1, 2, 3, 4, 5)
//    val tupleXml = xstream.toXML(tuple)
//    println(
//      s"""
//         |${tupleXml}
//         |
//        |${tuple} =${tuple == xstream.fromXML(tupleXml)}= ${xstream.fromXML(tupleXml)}
//      """.stripMargin
//    )
//
//    XStreamConversions.test()
    class RequestConverter(implicit _mapper: Mapper) extends Converter {
      override def canConvert(clazz: Class[_]): Boolean = classOf[Request] == clazz

      override def marshal(value: Any, writer: HierarchicalStreamWriter, context: MarshallingContext): Unit = {
        val request = value.asInstanceOf[Request]
        request.args match {
          case null =>
            val name = _mapper.serializedClass(null)
            startNode(writer, name, classOf[Mapper.Null])
            writer.endNode()

          case args =>
            for (i <- (0 until args.length)) {
              startNode(writer, s"arg${i}", classOf[String])
              context.convertAnother(args(i))
              writer.endNode()
            }
        }

        request.module match {
          case null =>
            val name = _mapper.serializedClass(null)
            startNode(writer, name, classOf[Mapper.Null])
            writer.endNode()

          case module =>
            startNode(writer, "module", classOf[String])
            context.convertAnother(module)
            writer.endNode()
        }
      }

      override def unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): AnyRef = {
        var args = List.empty[String]
        var modules = List.empty[String]
        while (reader.hasMoreChildren) {
          reader.moveDown()
          reader.getNodeName match {
            case arg if arg.startsWith("arg") => args :+= context.convertAnother(args, classOf[String]).asInstanceOf[String]
            case "module" => modules :+= context.convertAnother(modules, classOf[String]).asInstanceOf[String]
          }
          reader.moveUp()
        }

        Request(args, modules.head)
      }
    }

//    class ResponseConverter(implicit _mapper: Mapper) extends ScalaCollectionConverter[Response](
//      list => {
//println(s"list: ${list}")
//        Response(list.asInstanceOf[List[AnyRef]])
//      }
//    )
    class ResponseConverter(implicit _mapper: Mapper) extends AbstractCollectionConverter(_mapper) {
      def canConvert(clazz: Class[_]) = classOf[Response] == clazz

      def marshal(value: Any, writer: HierarchicalStreamWriter, context: MarshallingContext): Unit = {
        val response = value.asInstanceOf[Response]
        for (item <- response.items) {
          item match {
            case Language(language) =>
              startNode(writer, "language", classOf[String])
              context.convertAnother(language)
              writer.endNode()
            case PlayMode(playMode) =>
              startNode(writer, "playMode", classOf[String])
              context.convertAnother(playMode)
              writer.endNode()
            case Style(style) =>
              startNode(writer, "style", classOf[String])
              context.convertAnother(style)
              writer.endNode()
            case Time(time) =>
              startNode(writer, "time", classOf[String])
              context.convertAnother(time)
              writer.endNode()
            case SetupPageLock(setupPageLock) =>
              startNode(writer, "setupPageLock", classOf[String])
              context.convertAnother(setupPageLock)
              writer.endNode()
            case VideoOutput(videoOutput) =>
              startNode(writer, "videoOutput", classOf[String])
              context.convertAnother(videoOutput)
              writer.endNode()
            case FrameRate(frameRate) =>
              startNode(writer, "frameRate", classOf[String])
              context.convertAnother(frameRate)
              writer.endNode()
            case VideoZoom(videoZoom) =>
              startNode(writer, "videoZoom", classOf[String])
              context.convertAnother(videoZoom)
              writer.endNode()
          }
        }
      }

      def unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): Response = {
        var list = List.empty[AnyRef]
        while (reader.hasMoreChildren) {
          reader.moveDown()
          val item = reader.getNodeName match {
            case "language" => Language(reader.getValue)
            case "playMode" => PlayMode(reader.getValue)
            case "style" => Style(reader.getValue)
            case "time" => Time(reader.getValue.toLong)
            case "setupPageLock" => SetupPageLock(reader.getValue.toBoolean)
            case "videoOutput" => VideoOutput(reader.getValue)
            case "frameRate" => FrameRate(reader.getValue)
            case "videoZoom" => VideoZoom(reader.getValue)
          }
          list :+= item
          reader.moveUp()
        }

        Response(list)
      }
    }

    implicit val mapper = xstream.getMapper
    xstream.alias("theDavidBox", classOf[TheDavidBox])
    xstream.alias("request", classOf[Request])
    xstream.registerConverter(new RequestConverter)
    xstream.alias("response", classOf[Response])
    xstream.registerConverter(new ResponseConverter)

    val tdb = TheDavidBox(
      request = Request(args = List("get_video_zoom"), module = "setting"),
      response = Response(List(VideoZoom(videoZoom = "actual size"))),
      returnValue = 0
    )

    val tdbToXml = xstream.toXML(tdb)
    println(tdbToXml)
    println(xstream.fromXML(tdbToXml))
    println(xstream.fromXML(tdbToXml) == tdb)
  }
}

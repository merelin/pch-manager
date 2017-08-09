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
    println(fromJson(Xml.toJson(<root attr='av1'>txt<el/></root>)))
    println(Xml.toXml(Xml.toJson(<root attr='av1'>txt<el/></root>)))
  }
}

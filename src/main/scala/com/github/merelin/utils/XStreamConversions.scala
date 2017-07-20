package com.github.merelin.utils

import java.lang.reflect.{Constructor => JConstructor}

import com.thoughtworks.xstream._
import com.thoughtworks.xstream.converters._
import com.thoughtworks.xstream.converters.collections._
import com.thoughtworks.xstream.io._
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver
import com.thoughtworks.xstream.mapper._

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.reflect.ClassTag

class SymbolConverter extends SingleValueConverter {
  def toString(value: Any) = value.asInstanceOf[Symbol].name
  def fromString(name: String) = Symbol(name)
  def canConvert(clazz: Class[_]) = classOf[Symbol] == clazz
}

class TupleConverter(implicit _mapper: Mapper) extends AbstractCollectionConverter(_mapper) {
  import XStreamConversions._

  val constructors: Seq[JConstructor[AnyRef]] = Seq(null) ++ tuplesClasses.map(
    c => c.getConstructors.head.asInstanceOf[JConstructor[AnyRef]]
  )

  def canConvert(clazz: Class[_]) = clazz.getName.startsWith("scala.Tuple")

  def marshal(value: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) = {
    val product = value.asInstanceOf[Product]
    for (item <- product.productIterator) writeItem(item, context, writer)
  }

  def unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext) = {
    var list = List.empty[AnyRef]
    while (reader.hasMoreChildren) {
      reader.moveDown()
      list :+= readItem(reader, context, list)
      reader.moveUp()
    }

    constructors(list.size).newInstance(list: _*)
  }
}

abstract class ScalaCollectionConverter[T <: Iterable[_]](fromList: List[_] => T)(implicit manifest: ClassTag[T], _mapper: Mapper) extends AbstractCollectionConverter(_mapper) {
  def canConvert(clazz: Class[_]) = manifest.runtimeClass.isAssignableFrom(clazz)

  def marshal(value: Any, writer: HierarchicalStreamWriter, context: MarshallingContext): Unit = {
    for (item <- value.asInstanceOf[Iterable[_]]) writeItem(item, context, writer)
  }

  def unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): T = {
    var list = List.empty[Any]
    while (reader.hasMoreChildren) {
      reader.moveDown()
      list :+= readItem(reader, context, list)
      reader.moveUp()
    }

    fromList(list)
  }
}

class ArrayBufferConverter(implicit _mapper: Mapper) extends ScalaCollectionConverter[ArrayBuffer[_]](list => list.to[ArrayBuffer])
class ListBufferConverter(implicit _mapper: Mapper) extends ScalaCollectionConverter[ListBuffer[_]](list => list.to[ListBuffer])
class SeqConverter(implicit _mapper: Mapper) extends ScalaCollectionConverter[Seq[_]](list => list)
class ListConverter(implicit _mapper: Mapper) extends ScalaCollectionConverter[List[_]](list => list)
class SetConverter(implicit _mapper: Mapper) extends ScalaCollectionConverter[Set[_]](list => list.toSet)
class MapConverter(implicit _mapper: Mapper) extends ScalaCollectionConverter[Map[_, _]](list => list.asInstanceOf[List[(_, _)]].toMap)

object XStreamConversions {
  def tuplesClasses = for (t <- 1 to 22) yield Class.forName(s"scala.Tuple${t}")

  private lazy val jsonConverter = XStreamConversions(new XStream(new JettisonMappedXmlDriver))
  private lazy val xmlConverter = XStreamConversions(new XStream)

  def toJson(item: AnyRef) = jsonConverter.toXML(item)
  def toXml(item: AnyRef) = xmlConverter.toXML(item)

  def register[T <: ConverterMatcher](alias: String, classes: Seq[Class[_]], converter: T)(implicit stream: XStream) = {
    for (c <- classes) stream.alias(alias, c)
    converter match {
      case svc: SingleValueConverter => stream.registerConverter(svc)
      case c: Converter => stream.registerConverter(c)
    }
  }

  def apply(stream: XStream): XStream = {
    implicit val mapper = stream.getMapper
    implicit val xstream = stream

    register[SymbolConverter]("symbol", Seq(classOf[Symbol]), new SymbolConverter)
    register[TupleConverter]("tuple", tuplesClasses, new TupleConverter)
    register[ListConverter]("list", Seq(classOf[List[_]], Nil.getClass, classOf[::[_]]), new ListConverter)
    register[SeqConverter]("seq", Seq(classOf[Seq[_]]), new SeqConverter)
    register[ArrayBufferConverter]("arrayBuffer", Seq(classOf[ArrayBuffer[_]]), new ArrayBufferConverter)
    register[ListBufferConverter]("listBuffer", Seq(classOf[ListBuffer[_]]), new ListBufferConverter)
    register[SetConverter]("set", Seq(classOf[Set[_]]), new SetConverter)
    register[MapConverter]("map", Seq(classOf[Map[_, _]]), new MapConverter)

    stream
  }

  def test() {
    implicit val stream = XStreamConversions(new XStream)
    testType(new ArrayBuffer[Int] ++= List(1, 2, 3))
    testType(new ListBuffer[Int] ++= List(1, 2, 3))
    testType(Seq() ++ List(1, 2, 3))
    testType(List() ++ List(1, 2, 3))
    testType(Set() ++ List(1, 2, 3))
    testType(Map() ++ List(1 -> 1, 2 -> 2, 3 -> 3))
  }

  def testType[T](x: T)(implicit stream: XStream, manifest: ClassTag[T]) {
    val xml = stream.toXML(x)
    println("Test for type: " + manifest.runtimeClass)
    println(xml)
    println(stream.fromXML(xml))
  }
}

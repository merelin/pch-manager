package com.github.merelin.util.xml

import com.github.merelin.thedavidbox._
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter
import com.thoughtworks.xstream.converters.{Converter, MarshallingContext, UnmarshallingContext}
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper.startNode
import com.thoughtworks.xstream.io.{HierarchicalStreamReader, HierarchicalStreamWriter}
import com.thoughtworks.xstream.mapper.Mapper

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

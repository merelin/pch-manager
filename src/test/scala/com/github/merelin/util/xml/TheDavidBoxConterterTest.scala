package com.github.merelin.util.xml

import com.github.merelin.thedavidbox._
import com.thoughtworks.xstream.XStream

object TheDavidBoxConterterTest {
  implicit val xstream = XStreamConverter(new XStream)
  implicit val mapper = xstream.getMapper

  def main(args: Array[String]): Unit = {
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

    require(xstream.fromXML(xstream.toXML(tdb)) == tdb)
  }
}

package com.github.merelin.util.xml

import scala.xml.{Elem, Node, XML}

object XmlTest {
  val xml = XML.loadString("""<?xml version="1.0"?><root><el1></el1><el2></el2><el3></el3></root>""")

  def main(args: Array[String]): Unit = {
    def printNode(node: Node): Unit = node match {
      case <root>{contents @ _*}</root> =>
        println("<root>")
        contents.foreach { printNode _ }
        println("</root>")

      case el: Elem if el.label.startsWith("el") => println(el)
    }

    printNode(xml)
  }
}

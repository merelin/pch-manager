package com.github.merelin.utils

import com.thoughtworks.xstream._
import com.thoughtworks.xstream.io.xml.DomDriver

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
    xstream.alias("topping", classOf[Topping])
    xstream.alias("pizza", classOf[Pizza])

    val xml = xstream.toXML(p)
    println(
      s"""
        |${xml}
        |
        |${p} =${p == xstream.fromXML(xml)}= ${xstream.fromXML(xml)}
      """.stripMargin
    )

    val tuple = (1, 2, 3, 4, 5)
    val tupleXml = xstream.toXML(tuple)
    println(
      s"""
         |${tupleXml}
         |
        |${tuple} =${tuple == xstream.fromXML(tupleXml)}= ${xstream.fromXML(tupleXml)}
      """.stripMargin
    )

    XStreamConversions.test()
  }
}

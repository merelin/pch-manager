name := "pch-manager"

version := "1.0"

scalaVersion := "2.12.2"

resolvers ++= Seq("default" at "http://nexus.global.trafigura.com:8081/nexus/content/groups/mirror/")

libraryDependencies ++= Seq(
  "org.scala-lang.modules" % "scala-xml_2.12" % "1.0.6",
  "org.jmdns" % "jmdns" % "3.5.1",
  "org.eclipse.jetty" % "jetty-server" % "9.4.2.v20170220",
  "org.eclipse.jetty" % "jetty-util" % "9.4.2.v20170220",
  "org.codehaus.jettison" % "jettison" % "1.3.7",
  "com.thoughtworks.xstream" % "xstream" % "1.4.7"
)

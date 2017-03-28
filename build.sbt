name := "pch-manager"

version := "1.0"

scalaVersion := "2.12.1"

resolvers ++= Seq("default" at "http://nexus.global.trafigura.com:8081/nexus/content/groups/mirror/")

libraryDependencies ++= Seq(
  "org.jmdns" % "jmdns" % "3.5.1",
  "org.eclipse.jetty" % "jetty-server" % "9.4.2.v20170220",
  "org.eclipse.jetty" % "jetty-util" % "9.4.2.v20170220"
)

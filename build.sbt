name := "crawler"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "edu.uci.ics" % "crawler4j" % "4.2",
  "org.apache.httpcomponents"% "httpclient" % "4.5.2",
  "com.ui4j" % "ui4j-all" % "2.1.0" ,
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
  "org.jfxtras" % "openjfx-monocle" % "1.8.0_20",
  "org.slf4j" % "slf4j-log4j12" % "1.7.21"
)

 unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))

// unmanagedJars in Compile += {
//   val ps = new sys.SystemProperties
//   val jh = ps("java.home")
//   Attributed.blank(file(jh) / "lib/ext/jfxrt.jar")
//   }
javaOptions in run += "-Dui4j.headless=true"

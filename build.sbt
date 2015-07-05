name := "YAC"

version := "1.0"

scalaVersion := "2.11.2"

javacOptions ++= Seq("-encoding", "UTF-8")

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "mysql" % "mysql-connector-java" % "5.1.31"
)

libraryDependencies += "com.typesafe.slick" % "slick_2.11" % "3.0.0"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.3.5" withSources()

libraryDependencies += "log4j" % "log4j" % "1.2.16"

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.2"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "org.apache.commons" % "commons-collections4" % "4.0"

libraryDependencies += "org.json" % "json" % "20140107"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.6.0" withSources()

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.2"

libraryDependencies += "com.typesafe.slick" % "slick-codegen_2.11" % "3.0.0"

libraryDependencies += "commons-codec" % "commons-codec" % "1.9"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.11" withSources()

libraryDependencies += "com.typesafe.akka" % "akka-remote_2.11" % "2.3.11" withSources()

libraryDependencies += "com.zaxxer" % "HikariCP-java6" % "2.3.8"

libraryDependencies += "com.ning" % "async-http-client" % "1.9.29" withSources()

libraryDependencies += "com.google.protobuf" % "protobuf-java" % "2.6.1"

libraryDependencies += "net.lingala.zip4j" % "zip4j" % "1.3.2"

libraryDependencies += "io.spray" % "spray-can_2.11" % "1.3.3"

libraryDependencies += "io.spray" % "spray-util_2.11" % "1.3.3"

libraryDependencies += "io.spray" % "spray-routing_2.11" % "1.3.3"

libraryDependencies += "io.spray" % "spray-caching_2.11" % "1.3.3"

libraryDependencies += "io.spray" % "spray-client_2.11" % "1.3.3"

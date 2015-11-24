name := "temperature-control"

version := "1.0"

lazy val `temperature-control` = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq( javaJdbc , cache , javaWs)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
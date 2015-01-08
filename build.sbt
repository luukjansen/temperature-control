name := "temperature-control"

version := "1.0"

lazy val `temperature-control` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( javaJdbc , javaEbean , cache , javaWs,
  javaEbean
  //javaJpa,
  //"org.hibernate" % "hibernate-entitymanager" % "4.3.7.Final" // replace by your jpa implementation
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  
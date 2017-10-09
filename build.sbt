
name := "scala-docusign"

version := "0.1"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(

  "com.sun.jersey" % "jersey-core" % "1.13-b01",
  "com.docusign" % "docusign-esign-java" % "2.3.0"

)
name := "valid-case-class"

version := "0.1"

scalaVersion := "2.12.6"

scalacOptions += "-Ypartial-unification"

libraryDependencies += "com.softwaremill.common" %% "tagging" % "2.1.0"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.2.0"
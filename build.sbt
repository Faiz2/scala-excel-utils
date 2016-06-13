

name := "odenzo-excel-utils"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

//packageDescription := "Internall Helpers and Utilities"

resolvers ++= Seq(
                   "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
                   "SonaType OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
                   "Scala Tools Releases" at "http://scala-tools.org/repo-releases/",
                   "Dr Dozer graphiz-s" at "http://dl.bintray.com/content/drdozer/maven"
                 )



// Even though XML needs are small, I am not so found of Scala XML and its deprecated anyway.
// Think about moving to ScalaXB http://scalaxb.org

// Common Dependancies used across most projects
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.5" withSources()



libraryDependencies ++=
  Seq(
       "com.typesafe" % "config" % "1.3.0" withSources() withJavadoc(), //  https://github.com/typesafehub/config
       "net.ceedubs" %% "ficus" % "1.1.2" withSources() withJavadoc(),
       "ch.qos.logback" % "logback-classic" % "1.1.7" withSources() withJavadoc(),
       "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0" withSources() withJavadoc(),
       "org.json4s" %% "json4s-native" % "3.3.0" withSources() withJavadoc(), // https://github.com/json4s/json4s/
       "org.json4s" %% "json4s-scalaz" % "3.3.0" withSources(), // Json4s Scalaz support
       "org.scalaz" %% "scalaz-core" % "7.2.3" withSources() // For Either really

     )


libraryDependencies ++=
  Seq(
       "org.scalacheck" %% "scalacheck" % "1.13.1" % "test" withSources() withJavadoc(),
       "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test" withSources() withJavadoc()
     )


libraryDependencies ++=
  Seq(
       "org.apache.poi" % "poi" % "3.14" withSources(),
       "org.apache.poi" % "poi-ooxml" % "3.14" withSources()
     )



javaOptions in Test += "-Dconfig.file=conf/logger-test.xml"

scalacOptions ++= Seq("-unchecked", "-deprecation")

mainClass in(Compile, run) := Some("com.odenzo.archcatalogs.functional.BestProgram")

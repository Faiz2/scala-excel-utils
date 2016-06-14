

name := "scala-excel-utils"

version := "V0.1"

scalaVersion := "2.11.8"

packageDescription := "Internal Helpers and Utilities"


resolvers ++=
  Seq(
       Resolver.jcenterRepo,
       Resolver.bintrayRepo("odenzo", "maven")
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




//////////// Bintray Publishing  --- to Move to Seperate .sbt file

lazy val commonSettings = Seq(
                               version in ThisBuild := "<YOUR PLUGIN VERSION HERE>",
                               organization in ThisBuild := "odenzo"
                             )





/* In the credentials.properties file which I will try and store in .ivy
realm=Bintray API Realm
host=api.bintray.com
user=odenzo
password=BINTRAY_API_KEY

 */
//credentials += Credentials(Path.userHome / ".ivy2" / "bintraycredentials.properties")
publishTo := Some("Bintray API Realm"
                    at "https://api.bintray.com/content/odenzo/maven/scala-excel-utils/V0.1"
                 )
//
//lazy val root = (project in file(".")).
//  settings(commonSettings ++ bintrayPublishSettings: _*).
//  settings(
//    sbtPlugin := true,
//    name := "<YOUR PLUGIN HERE>",
//    description := "<YOUR DESCRIPTION HERE>",
//
//    publishMavenStyle := false,
//    repository in bintray := "sbt-plugins",
//    bintrayOrganization in bintray := None
//  )


/*
 * Note that this will seem to reject a version ending in SNAPSHOT
 */
sbtPlugin := false
bintrayOrganization := None
bintrayRepository := "maven"
publishMavenStyle := false
bintrayReleaseOnPublish in ThisBuild := false
licenses +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
bintrayPackageLabels := Seq("scala", "excel", "POI")

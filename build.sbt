import com.typesafe.sbt.SbtNativePackager.autoImport._


lazy val commonSettings =
  Seq(
    organization := "com.odenzo",
    version := "0.0.1",
    scalaVersion := "2.11.8"
  )



scalacOptions ++= Seq("-unchecked", "-deprecation", "-explaintypes",
                      //"-verbose",
                      "-encoding", "utf8",
                      "-feature",
                      "-Xlint",
                      "-Ywarn-dead-code",
                      "-target:jvm-1.8",
                      "-language:existentials",
                      "-language:implicitConversions",
                      "-language:higherKinds",
                      "-language:existentials",
                      "-language:postfixOps"
)



resolvers ++=
  Seq(
    Resolver.jcenterRepo,
    Resolver.bintrayRepo("odenzo", "maven")
  )

val standardlibs = // I use these same set in just about every project
  Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.7" withSources(),
    "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0" withSources(),
    "org.scalaz" %% "scalaz-core" % "7.2.3" withSources(), // For Either really
    "org.scala-lang.modules" %% "scala-xml" % "1.0.5" withSources(),

    "org.scalacheck" %% "scalacheck" % "1.13.1" % "test" withSources() withJavadoc(),
    "org.scalatest" %% "scalatest" % "2.2.6" % "test" withSources() withJavadoc()
  )

javaOptions in Test += "-Dconfig.file=conf/logger-test.xml"


lazy val root = (project in file("."))
                .settings(commonSettings: _*)
                .settings(
                  name := "scala-excel-utils",
                  packageDescription := "Internal Helpers and Utilities",
                  libraryDependencies ++= standardlibs,
                  libraryDependencies ++= Seq(

                    "org.apache.poi" % "poi" % "3.14" withSources() withJavadoc(),
                    "org.apache.poi" % "poi-ooxml-schemas" % "3.14", // Has no javadocs or source
                    "org.apache.poi" % "poi-ooxml" % "3.14"
                  )
                )



//////////// Bintray Publishing  --- to Move to Seperate .sbt file


/* In the credentials.properties file which I will try and store in .ivy
realm=Bintray API Realm
host=api.bintray.com
user=odenzo
password=BINTRAY_API_KEY

 */

// Well well, do we want ot put version in here automatically? I think so.
// Not that after a sbt publish need a sbt release
publishTo := Some("Bintray API Realm"
                    at "https://api.bintray.com/content/odenzo/maven/scala-excel-utils/0.0.1"
)


/*
 * Note that this will seem to reject a version ending in SNAPSHOT
 */
sbtPlugin := false
bintrayOrganization := None
bintrayRepository := "maven"
publishMavenStyle := true
bintrayReleaseOnPublish in ThisBuild := true
licenses +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
bintrayPackageLabels := Seq("scala", "excel", "POI")
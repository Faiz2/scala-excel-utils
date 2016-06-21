import com.typesafe.sbt.SbtNativePackager.autoImport._


lazy val commonSettings =
  Seq(
    organization := "com.odenzo",
    version := "0.0.2-SNAPSHOT",
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
    "org.scalaz" %% "scalaz-core" % "7.2.3" withSources(), // For Either, OptionT really, cats instead?
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



/*--------------- Bintray Publishing  --- to Move to Seperate .sbt file --------------------------*/

// Note SNAPSHOTs won't go to BinTray
publishTo := Some("Bintray API Realm"
                    at "https://api.bintray.com/content/odenzo/maven/scala-excel-utils/" + version
)

sbtPlugin := false
bintrayOrganization := None
bintrayRepository := "maven"
publishMavenStyle := true
bintrayReleaseOnPublish in ThisBuild := true
licenses +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
bintrayPackageLabels := Seq("scala", "excel", "POI")


/*----- Scalariform FOrmatting ---------*/

/* -------------- Scalariform --------------------------*/
// And the battle with IntelliJ -- whose formattting I like better for now.
// So not useing thig.
//
//import scalariform.formatter.preferences._
//import com.typesafe.sbt.SbtScalariform
//import com.typesafe.sbt.SbtScalariform.ScalariformKeys
//
//SbtScalariform.scalariformSettings
//
//ScalariformKeys.preferences := ScalariformKeys.preferences.value
//                               .setPreference(DoubleIndentClassDeclaration, true)
//                               .setPreference(IndentWithTabs, false)
//                               .setPreference(IndentSpaces, 2)
//                               .setPreference(RewriteArrowSymbols, true)
//                               .setPreference(SpaceBeforeColon, true)
//                               .setPreference(SpaceInsideBrackets, false)
//                               .setPreference(SpaceInsideParentheses, false)
//                               .setPreference(SpacesWithinPatternBinders, true)
//                               .setPreference(AlignArguments, true)
//                               .setPreference(AlignParameters, true)
//                               .setPreference(AlignSingleLineCaseStatements, true)
//                               .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 80)
//                               .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
//                               .setPreference(SpacesAroundMultiImports, false)
//                               .setPreference(CompactControlReadability, true)
//                               .setPreference(CompactStringConcatenation, false)


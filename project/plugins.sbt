
///    http://eed3si9n.com/30sbt_plugins_in_15min/#33

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.10")


// Generic Native Packaging -- Used for Docker; Packaging only, no code changes
// https://github.com/sbt/sbt-native-packager
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M2")

// Was using this and Scalariform, need to sort out final solution
// http://www.scalastyle.org/sbt.html
//addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")


addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")


// https://github.com/softprops/bintray-sbt
addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")

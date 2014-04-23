name := "scala-koans-online"

version := "1.0-SNAPSHOT"

//resolvers += Resolver.url("webjars", url("https://oss.sonatype.org/content/repositories/snapshots"))	(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0",
  "org.webjars" %% "webjars-play" % "2.2.2",
  "org.webjars" % "jquery" % "2.1.0-2",
  "org.webjars" % "ace" % "01.08.2014",
  "org.webjars" % "jasny-bootstrap" % "3.0.1-p7-1",
  "org.webjars" % "angularjs" % "1.3.0-beta.2",
  "org.webjars" % "angular-ui-bootstrap" % "0.10.0-1"
)

play.Project.playScalaSettings
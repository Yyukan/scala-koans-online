name := "scala-koans-online"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0",
  "org.webjars" %% "webjars-play" % "2.2.1",
  "org.webjars" % "jquery" % "2.0.3-1",
  "org.webjars" % "bootstrap" % "3.0.3",
  "org.webjars" % "ace" % "07.31.2013"
)

play.Project.playScalaSettings
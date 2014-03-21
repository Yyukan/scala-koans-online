name := "scala-koans-online"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0",
  "org.webjars" %% "webjars-play" % "2.2.1",
  "org.webjars" % "jquery" % "2.1.0-2",
  //"org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "ace" % "07.31.2013",
  "org.webjars" % "typeaheadjs" % "0.10.2",
  "org.webjars" % "jasny-bootstrap" % "3.0.1-p7-1",
  //"org.webjars" % "bootswatch-flatly" % "3.1.1"
  //"org.webjars" % "bootswatch-simplex" % "3.1.1"
  //"org.webjars" % "bootswatch-cosmo" % "3.1.1"
  "org.webjars" % "bootswatch-united" % "3.1.1"
)

play.Project.playScalaSettings
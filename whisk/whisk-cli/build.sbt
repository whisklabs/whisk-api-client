import AssemblyKeys._

scalaVersion := "2.9.2"

libraryDependencies += "org.rogach" %% "scallop" % "0.5.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.8" % "test"

//libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.3"


libraryDependencies ++= Seq(
	"com.novocode" % "junit-interface" % "0.9-RC2" % "test->default",
	"info.cukes" % "cucumber-junit" % "1.0.9" % "test",
	"info.cukes" % "cucumber-scala" % "1.0.9" % "test"
	)


assemblySettings

//assembleArtifact in packageBin := false

mainClass in assembly := Some("whisk.WhiskCli")


scalaVersion in ThisBuild := "2.9.1"


resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
	"com.novocode" % "junit-interface" % "0.9-RC2" % "test->default",
	"info.cukes" % "cucumber-junit" % "1.0.9" % "test",
	"info.cukes" % "cucumber-scala" % "1.0.9" % "test"
	)
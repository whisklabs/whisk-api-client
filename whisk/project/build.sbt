scalaVersion in ThisBuild := "2.9.2"


resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "org.eclipse.mylyn.github" % "org.eclipse.egit.github.core" % "2.1.0"

libraryDependencies += "org.rogach" %% "scallop" % "0.5.1"

libraryDependencies ++= Seq(
	"com.novocode" % "junit-interface" % "0.9-RC2" % "test->default",
	"info.cukes" % "cucumber-junit" % "1.0.9" % "test",
	"info.cukes" % "cucumber-scala" % "1.0.9" % "test"
	)

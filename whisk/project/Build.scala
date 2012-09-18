import sbt._
import Keys._


object HelloBuild extends Build {
	lazy val whisk = Project(id = "whisk", base = file("."))  aggregate(whiskcli, whiskapiproxy)
    lazy val whiskapiproxy  = Project(id = "whiskapiproxy", base = file("whiskapiproxy"))
	lazy val whiskcli  = Project(id = "whisk-cli", base = file("whisk-cli"))  dependsOn(whiskapiproxy)
}
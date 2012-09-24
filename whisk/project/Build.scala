import sbt._
import Keys._



object HelloBuild extends Build {

    lazy val majorVersion = "0.2"
    lazy val whiskCliSettings   =    Defaults.defaultSettings ++ Seq(
        organization := "whisk",
        version := "%s.0-SNAPSHOT" format majorVersion,
        scalaVersion := "2.9.2",
        name := "whisk-cli",
        scalacOptions ++= Seq("-unchecked", "-deprecation"),
        javacOptions ++= Seq("-target", "1.6", "-source", "1.6"),
        manifestSetting)

	lazy val whisk = Project(id = "whisk", base = file("."))  aggregate(whiskcli, whiskapiproxy)
    lazy val whiskapiproxy  = Project(id = "whiskapiproxy", base = file("whiskapiproxy"))
	lazy val whiskcli  = Project(id = "whisk-cli", base = file("whisk-cli"), settings = whiskCliSettings) dependsOn(whiskapiproxy)


    lazy val manifestSetting = packageOptions <+= (name, version, organization) map {
        (title, version, vendor) =>
            Package.ManifestAttributes(
                "Created-By" -> "Simple Build Tool",
                "Built-By" -> "whisk",
                "Build-Jdk" -> System.getProperty("java.version"),
                "Specification-Title" -> title,
                "Specification-Version" -> version,
                "Specification-Vendor" -> vendor,
                "Implementation-Title" -> title,
                "Implementation-Version" -> version,
                "Implementation-Vendor-Id" -> vendor,
                "Implementation-Vendor" -> vendor
            )
    }
}






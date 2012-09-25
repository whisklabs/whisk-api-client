import org.apache.ivy.util.FileUtil
import sbt._
import Keys._
import sbtassembly.Plugin.AssemblyKeys._


object HelloBuild extends Build {

    lazy val majorVersion = "0.1"
    lazy val whiskCliSettings   =    Defaults.defaultSettings ++ Seq(
        organization := "whisk",
        version := "%s.0" format majorVersion,
        scalaVersion := "2.9.2",
        name := "whisk-cli",
        scalacOptions ++= Seq("-unchecked", "-deprecation"),
        javacOptions ++= Seq("-target", "1.6", "-source", "1.6"),
        manifestSetting) ++ Seq(
                test in assembly := {},
                jarName in assembly := "whisk-cli-%s.0.jar" format majorVersion,
                mainClass in assembly := Some("whisk.WhiskCli"))

	lazy val whisk = Project(id = "whisk", base = file("."))  aggregate(whiskcli, whiskapiproxy)
    lazy val whiskapiproxy  = Project(id = "whisk-apiproxy", base = file("whisk-apiproxy"))
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






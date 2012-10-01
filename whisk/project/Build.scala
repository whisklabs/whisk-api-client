import org.rogach.scallop.ScallopConf
import sbtassembly.Plugin.AssemblyKeys._
import sbt._
import sbt.Keys._
import scala.Some


object HelloBuild extends Build {

    lazy val majorVersion= "0.1"

    val buildSettings = Seq(
        organization := "co.uk.whisk",
        version := majorVersion)


    lazy val whiskCliSettings = Defaults.defaultSettings ++ buildSettings ++  Format.settings ++ Seq(
        name := "whisk-cli",
        scalacOptions ++= Seq("-unchecked", "-deprecation"),
        javacOptions ++= Seq("-target", "1.6", "-source", "1.6"),
        manifestSetting) ++ Seq(
        test in assembly := {},
        jarName in assembly := "whisk-cli-%s.jar" format majorVersion,
        mainClass in assembly := Some("whisk.WhiskCli"))

    lazy val whisk = {
        Project(id = "whisk", base = file("."), settings = Defaults.defaultSettings ++ Format.settings) aggregate(whiskcli, whiskapiproxy)
    }
    lazy val whiskapiproxy = Project(id = "whisk-apiproxy", base = file("whisk-apiproxy"), settings = Defaults.defaultSettings ++ Dist.settings ++ Format.settings)
    lazy val whiskcli = Project(id = "whisk-cli", base = file("whisk-cli"), settings = whiskCliSettings) dependsOn (whiskapiproxy)


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


    object Dist {

        import org.eclipse.egit.github.core.client.GitHubClient
        import org.eclipse.egit.github.core.service.{RepositoryService, DownloadService}
        import org.eclipse.egit.github.core.{DownloadResource, Download, Repository}
        import java.io.File


        val distDirectory = SettingKey[File]("dist-dir")

        val dist = InputKey[Unit]("dist", "Create a zipped distribution of everything.")

        lazy val settings: Seq[Setting[_]] = Seq(
            distDirectory <<= baseDirectory / "../dist",
            dist <<= inputTask {
                (argTask: TaskKey[Seq[String]]) => {
                      (argTask, distDirectory, packageBin in Compile, assembly in whiskcli) map {
                        case (args: Seq[String], dir, proxy: File, cli: File) =>
                            object Conf extends ScallopConf(args) {
                                val gitHubUserIdArg = opt[String]("user")
                                val gitHubPassArg = opt[String]("pass")
                                val gitHubRepositoryArg = opt[String]("repo")
                            }

                            val gitHubUserId = Conf.gitHubUserIdArg.get.get
                            val gitHubPass = Conf.gitHubPassArg.get.get
                            val gitHubRepository = Conf.gitHubRepositoryArg.get.get

                            val cliTargetFile: File = dir / cli.getName
                            val proxyTargetFile: File = dir / proxy.getName
                            IO.copyFile(cli, cliTargetFile)
                            IO.copyFile(proxy, proxyTargetFile)

                            uploadToGithubDownloads(gitHubUserId, gitHubPass, gitHubRepository, cliTargetFile, "CLI for whisk API")
                            uploadToGithubDownloads(gitHubUserId, gitHubPass, gitHubRepository, proxyTargetFile, "Proxy for whisk API")
                    }
                }
            })


        private def uploadToGithubDownloads(userId: String, pass: String, repository: String, file: File, desc: String) = {
            val client = new GitHubClient()
            client.setCredentials(userId, pass)

            val repoSer: RepositoryService = new RepositoryService(client)
            val repo: Repository = repoSer.getRepository(userId, repository)
            val service: DownloadService = new DownloadService(client)

            val download: Download = new Download()
            download.setName(file.getName)
            download.setSize(file.length().toInt)
            download.setContentType("application/java-archive")
            download.setDescription(desc)

            var d: DownloadResource = service.createDownload(repo, download, file)
            println("Upload file to " + d.getPath)
        }
    }

}


object Format {

    import com.typesafe.sbtscalariform.ScalariformPlugin
    import ScalariformPlugin._

    lazy val settings = scalariformSettings ++ Seq(
        ScalariformKeys.preferences := formattingPreferences
    )

    lazy val formattingPreferences = {
        import scalariform.formatter.preferences._
        FormattingPreferences().
            setPreference(AlignParameters, true).
            setPreference(AlignSingleLineCaseStatements, true).
            setPreference(CompactControlReadability, true).
            setPreference(CompactStringConcatenation, true).
            setPreference(DoubleIndentClassDeclaration, true).
            setPreference(FormatXml, true).
            setPreference(IndentLocalDefs, true).
            setPreference(IndentPackageBlocks, true).
            setPreference(IndentSpaces, 4).
            setPreference(MultilineScaladocCommentsStartOnFirstLine, true).
            setPreference(PreserveSpaceBeforeArguments, false).
            setPreference(PreserveDanglingCloseParenthesis, false).
            setPreference(RewriteArrowSymbols, false).
            setPreference(SpaceBeforeColon, false).
            setPreference(SpaceInsideBrackets, false).
            setPreference(SpacesWithinPatternBinders, true)
    }
}







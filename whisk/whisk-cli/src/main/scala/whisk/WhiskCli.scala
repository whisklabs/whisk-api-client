package whisk

import apiproxy.{ NullLogger, HttpClient, ApiProxy }
import org.rogach.scallop.{ Subcommand, ScallopConf }
import protocol.identity.AddRecipeToShortlistRequest
import protocol.identity.AuthenticationCredentials
import protocol.identity.CreateSessionRequest
import protocol.identity.CredentialSource
import protocol.identity.LoginRequest
import protocol.recipes._
import protocol.recipes.Recipe
import protocol.recipes.RecipeQueryRequest
import protocol.shoppinglist.{ AddToShoppingListRequest, ShoppingListOptionsRequest }
import org.rogach.scallop.exceptions.ScallopException
import java.io.PrintStream
import scala.Some

object WhiskCli {
    private val out = Console.out

    def main(args: Array[String]): Unit = {
        if (args.length == 0) {
            showHelp
            return
        }

        try {
            process(args)
        }
        catch {
            case e: ScallopException => {
                println(e.getMessage)
                showHelp
            }
            case e: Exception => e.printStackTrace()
        }
    }

    def showHelp {
        println(
            """usages
    : whisk-cli.jar [--v] recipes [--search chocolate] [--site bbc] all
    : whisk-cli.jar [--v] addtofavourites full_url
    : whisk-cli.jar [--v] shoppinglistoptions [--store store] full_url
    : whisk-cli.jar [--v] addtoshoppinglist [--shoppingListName test] waitrose full_url
    : whisk-cli.jar [--v] check recipe_url """)
    }

    def process(args: Array[String]) = {
        object Conf extends ScallopConf(args) {

            val verbose = opt[Boolean]("v")

            val recipes = new Subcommand("recipes") {
                val searchText = opt[String]("search")
                val site = opt[String]("site")
                val list = trailArg[String](required = true)
            }

            val addtofavourites = new Subcommand("addtofavourites") {
                val url = trailArg[String](required = true)
            }

            val shoppinglistoptions = new Subcommand("shoppinglistoptions") {
                val store = opt[String]("store")
                val url = trailArg[String](required = true)
            }

            val addtoshoppinglist = new Subcommand("addtoshoppinglist") {
                val shoppingListName = opt[String]("shoppingListName")
                val store = trailArg[String](required = true)
                val url = trailArg[String](required = true)
            }

            val login = new Subcommand("login") {
                val email = trailArg[String](required = true)
                val pass = trailArg[String](required = true)
            }

            val check = new Subcommand("check") {
                val url = trailArg[String](required = true)
            }
        }

        val sessionId = obtainSessionId()

        val client = new HttpClient(if (Conf.verbose.get.getOrElse(false)) ConsoleLogger else NullLogger)

        Conf.subcommand match {
            case Some(Conf.recipes) => {

                var map: Map[String, Seq[String]] = Map(
                    ("list", Seq(Conf.recipes.list())))

                val isTopRecipes: Boolean = Conf.recipes.list().contains("top")
                if (isTopRecipes) {
                    map ++ Map(("start", Seq("0")),
                        ("count", Seq("10")))
                }

                if (Conf.recipes.site.isSupplied)
                    map += (("site", Seq(Conf.recipes.site())))

                if (Conf.recipes.searchText.isSupplied)
                    map += (("searchText", Seq(Conf.recipes.searchText())))

                val prod = () => {
                    new ApiProxy(client).recipesQuery(RecipeQueryRequest(sessionId = sessionId, params = map)) match {
                        case Some(r) => if (r.data.isDefined) r.data.get.recipes else Seq.empty
                        case None    => Seq.empty
                    }
                }

                val header = (out: PrintStream) => {
                    out.println("-" * 180)
                    out.println(
                        if (!isTopRecipes)
                            "A selection of recipes from Whisk"
                        else
                            "Whisks top recipes")
                    out.println("-" * 180)
                }
                val columnHeaderPrinter = (out: PrintStream) => {
                    out.println("%-5s %-40s %-15s %-15s %-45s %-45s".format("ID", "Recipe Title", "Author", "Site", "Tesco price (supermarket/total/pre person) ", "Waitrose price (supermarket/total/pre person)"))
                    out.println("-" * 180)
                }
                InteractiveConsole.process(prod, header, (out: PrintStream, id: Int, r: Recipe) => RecipeFormatterExt.formatItem(out, (id, r)), columnHeaderPrinter)
            }

            case Some(Conf.login) => {
                val response = new ApiProxy(client).createSession(CreateSessionRequest())
                val newSessionId = response.map(r => r.header.sessionId) match {
                    case Some(x) => WhiskPermanentStorage.saveSessionId(x); x
                    case None    => ""
                }

                val r = new ApiProxy(client)
                    .loginQuery(LoginRequest(newSessionId, AuthenticationCredentials(username = Conf.login.email.get.get, source = CredentialSource("BasicAuth"), token = "")))
                LoginFormatter.formatItem(out, r.get)
            }

            case Some(Conf.shoppinglistoptions) => {
                val r = new ApiProxy(client)
                    .shoppingListOptionsQuery(
                        ShoppingListOptionsRequest(sessionId, Conf.shoppinglistoptions.url.get.get, Some(1), shoppingListName = None, store = Conf.shoppinglistoptions.store.get))
                ShoppingListOptions.formatItem(out, r.get)
            }

            case Some(Conf.addtoshoppinglist) => {
                val store: Option[String] = Conf.addtoshoppinglist.store.get
                val url: String = Conf.addtoshoppinglist.url.get.get
                val r = new ApiProxy(client)
                    .addToShoppingListQuery(AddToShoppingListRequest(sessionId, url, Some(1), store, shoppingListName = Conf.addtoshoppinglist.shoppingListName.get))
                AddToShoppingListFormatter.formatItem(out, r.get)
            }

            case Some(Conf.addtofavourites) => {
                val r = new ApiProxy(client)
                    .AddRecipeToShortlistRequestQuery(AddRecipeToShortlistRequest(sessionId, Conf.addtofavourites.url.get.get))
                RecipeResponseFormatter.formatItem(out, r.get)
            }

            case Some(Conf.check) => {
                val r = new ApiProxy(client).recipeCheck(RecipeCheckRequest(Some(sessionId), Conf.check.url.get.get, true))
                out.println("%-40s %-15s %-15s %-45s %-45s".format("Recipe Title", "Author", "Site", "Tesco price (supermarket/total/pre person)", "Waitrose price (supermarket/total/pre person)"))
                out.println("-" * 200)
                RecipeFormatter.formatItem(out, r.get.recipe.get)
            }
            case None => {}
        }
    }

    def obtainSessionId(): String = {
        WhiskPermanentStorage.loadSessionId() match {
            case Some(s) => s
            case None => {
                val response = new ApiProxy(new HttpClient(NullLogger)).createSession(CreateSessionRequest())
                response.map(r => r.header.sessionId) match {
                    case Some(x) => WhiskPermanentStorage.saveSessionId(x); x
                    case None    => ""
                }

            }
        }
    }
}


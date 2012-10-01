package whisk

import apiproxy.{HttpClient, ApiProxy}
import org.rogach.scallop.{Subcommand, ScallopConf}
import protocol.identity._
import protocol.identity.AuthenticationCredentials
import protocol.identity.CreateSessionRequest
import protocol.identity.LoginRequest
import protocol.recipes.{Recipe, RecipeQueryResponse, RecipeQueryRequest}
import protocol.recipes.{RecipeCheckRequest, RecipeQueryResponse, RecipeQueryRequest}
import protocol.shoppinglist.{AddToShoppingListRequest, ShoppingListOptionsRequest}
import scala.Some
import org.rogach.scallop.exceptions.ScallopException
import java.io.PrintStream


object WhiskCli {
    private val out = Console.out

    def main(args: Array[String]): Unit = {
        if (args.length == 0) {
            showHelp
            return
        }

        try {
            process(args)
        } catch {
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
    : whisk-cli.jar recipes [--search chocolate] [--site bbc] all
    : whisk-cli.jar addtofavourites full_url
    : whisk-cli.jar shoppinglistoptions [--store store] full_url
    : whisk-cli.jar addtoshoppinglist [--shoppingListName test] waitrose full_url
    : whisk-cli.jar check recipe_url """)
    }

    def process(args: Array[String]) = {
        object Conf extends ScallopConf(args) {
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
                val url  = trailArg[String](required = true)
            }
        }

        val sessionId = obtainSessionId()

        Conf.subcommand match {
            case Some(Conf.recipes) => {

                var map: Map[String, Seq[String]] = Map(
                    ("list", Seq(Conf.recipes.list()))
                )

                val isTopRecipes: Boolean = Conf.recipes.list().contains("top")
                if(isTopRecipes){
                    map ++ Map(("start", Seq("0")),
                               ("count", Seq("10")))
                }

                if (Conf.recipes.site.isSupplied)
                    map += (("site", Seq(Conf.recipes.site())))

                if (Conf.recipes.searchText.isSupplied)
                    map += (("searchText", Seq(Conf.recipes.searchText())))

                val prod = () => {
                    new ApiProxy(HttpClient).recipesQuery(RecipeQueryRequest(sessionId = sessionId, params = map)) match {
                        case Some(r) => r.data.get.recipes
                        case None => Seq.empty
                    }
                }

                val header = (out:PrintStream)=> {
                    out.println("-" * 120)
                    out.println(
                        if(!isTopRecipes)
                            "  A selection of recipes from Whisk"
                        else
                            "  Whisks top recipes")
                    out.println("-" * 120)
                }
                val columnHeaderPrinter = (out:PrintStream) =>{
                    out.println("%-5s %-20s %-15s %-15s %-30s %-30s".format("ID", "Recipe Title","Author", "Site", "Tesco price", "Waitrose price"))
                    out.println("-" * 120)
                }
                InteractiveConsole.process(prod, header, (out:PrintStream, id:Int, r:Recipe) => RecipeFormatterExt.formatItem(out,(id, r)), columnHeaderPrinter)
            }

            case Some(Conf.login) => {
                val response = new ApiProxy(HttpClient).createSession(CreateSessionRequest())
                val newSessionId = response.map(r => r.header.sessionId) match {
                    case Some(x) => WhiskPermanentStorage.saveSessionId(x); x
                    case None => ""
                }

                val r = new ApiProxy(HttpClient)
                    .loginQuery(LoginRequest(newSessionId, AuthenticationCredentials(username = Conf.login.email.get.get, source = CredentialSource("BasicAuth"), token = "")))
                LoginFormatter.formatItem(out, r.get)
            }

            case Some(Conf.shoppinglistoptions) => {
                val r = new ApiProxy(HttpClient)
                    .shoppingListOptionsQuery(
                    ShoppingListOptionsRequest(sessionId, Conf.shoppinglistoptions.url.get.get, Some(1), store = Conf.shoppinglistoptions.store.get))
                ShoppingListOptions.formatItem(out, r.get)
            }

            case Some(Conf.addtoshoppinglist) => {
                val store: Option[String] = Conf.addtoshoppinglist.store.get
                val url: String = Conf.addtoshoppinglist.url.get.get
                val r = new ApiProxy(HttpClient)
                    .addToShoppingListQuery(AddToShoppingListRequest(sessionId, url, Some(1), store, shoppingListName = Conf.addtoshoppinglist.shoppingListName.get))
                AddToShoppingListFormatter.formatItem(out, r.get)
            }

            case Some(Conf.addtofavourites) => {
                val r = new ApiProxy(HttpClient)
                    .AddRecipeToShortlistRequestQuery(AddRecipeToShortlistRequest(sessionId, Conf.addtofavourites.url.get.get))
                RecipeResponseFormatter.formatItem(out, r.get)
            }


            case  Some(Conf.check) => {
                val  r = new ApiProxy(HttpClient).recipeCheck(RecipeCheckRequest(sessionId, Conf.check.url.get.get, true))
                out.println("%-20s %-15s %-15s %-30s %-30s".format("Recipe Title","Author", "Site", "Tesco price", "Waitrose price"))
                out.println("-" * 120)
                RecipeFormatter.formatItem(out, r.get.recipe.get)
            }
            case None => {}
        }
    }


    def obtainSessionId(): String = {
        WhiskPermanentStorage.loadSessionId() match {
            case Some(s) => s
            case None => {
                val response = new ApiProxy(HttpClient).createSession(CreateSessionRequest())
                response.map(r => r.header.sessionId) match {
                    case Some(x) => WhiskPermanentStorage.saveSessionId(x); x
                    case None => ""
                }

            }
        }
    }
}




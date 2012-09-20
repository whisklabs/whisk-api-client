package whisk

import apiproxy.{HttpClient, ApiProxy}
import org.rogach.scallop.{Subcommand, ScallopConf}
import protocol.identity._
import protocol.identity.AuthenticationCredentials
import protocol.identity.CreateSessionRequest
import protocol.identity.LoginRequest
import protocol.recipes.{RecipeQueryResponse, RecipeQueryRequest}
import protocol.shoppinglist.{AddToShoppingListRequest, ShoppingListOptionsRequest}
import scala.Some
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import net.liftweb.json.DefaultFormats
import org.rogach.scallop.exceptions.OptionParseException


object WhiskCli {
    private val out = Console.out

    def main(args: Array[String]) :Unit = {
        try{
            process(args)
        }catch
        {
            case e: OptionParseException =>
                {
                    println(e.getMessage)
                    println(
                        """usages
    : whisk-cli.jar recipes [--search chocolate] [--site bbc] all
    : whisk-cli.jar addtofavourites full_url
    : whisk-cli.jar shoppinglistoptions full_url
    : whisk-cli.jar addtoshoppinglist full_url""")
                }
            case e:Exception=> e.printStackTrace()
        }
    }

    def process(args: Array[String]) ={

        object Conf extends ScallopConf(args) {
            val recipes = new Subcommand("recipes") {
                val searchText = opt[String]("search")
                val site = opt[String]("site")

                val favourites = trailArg[String]( name = "favourites", required = false)
                val list = trailArg[String](required = true)
                mutuallyExclusive(favourites, searchText)
            }

            val addtofavourites = new Subcommand("addtofavourites") {
                val url = trailArg[String](required = true)
            }

            val shoppinglistoptions = new Subcommand("shoppinglistoptions") {
                val url = trailArg[String](required = true)
            }

            val addtoshoppinglist = new Subcommand("addtoshoppinglist") {
                val url = trailArg[String](required = true)
            }

            val login = new Subcommand("login") {
                val email = trailArg[String](required = true)
                val pass = trailArg[String](required = true)
            }
        }

        val sessionId = obtainSessionId()

        Conf.subcommand match {
            case Some(Conf.recipes) => {

                var map: Map[String, Seq[String]] = Map(
                    ("list",  Seq(Conf.recipes.list())),
                    ("start", Seq("0")),
                    ("count", Seq("10"))
                )

                if(Conf.recipes.favourites.isSupplied)
                {
                    throw new NotImplementedException
                }


                if(Conf.recipes.site.isSupplied)
                    map +=  (("site", Seq(Conf.recipes.site())))

                if(Conf.recipes.searchText.isSupplied)
                    map += (("searchText", Seq(Conf.recipes.searchText())))

                val r: Option[RecipeQueryResponse] = new ApiProxy(HttpClient).recipesQuery(RecipeQueryRequest(sessionId= sessionId, params = map))
                RecipeQueryResponseFormatter.formatItem(out, r)
            }

            case Some(Conf.login) => {
                val response = new ApiProxy(HttpClient).createSession(CreateSessionRequest())
                val newSessionId = response.map(r => r.header.sessionId) match {
                    case Some(x) => WhiskPermanentStorage.saveSessionId(x); x
                    case None => ""
                }

                val r  = new ApiProxy(HttpClient)
                    .loginQuery(LoginRequest(newSessionId, AuthenticationCredentials(username = Conf.login.email.get.get, source = CredentialSource("BasicAuth"), token= "")))
                LoginFormatter.formatItem(out, r.get)
            }

            case Some(Conf.shoppinglistoptions) =>{
                val  r = new ApiProxy(HttpClient)
                     .shoppingListOptionsQuery(ShoppingListOptionsRequest(sessionId, Conf.shoppinglistoptions.url.get.get, Some(1), None))
                ShoppingListOptions.formatItem(out, r.get)
            }

            case Some(Conf.addtoshoppinglist) =>{
                val  r = new ApiProxy(HttpClient)
                    .addToShoppingListQuery(AddToShoppingListRequest(sessionId, Conf.shoppinglistoptions.url.get.get, Some(1), None))
                AddToShoppingListFormatter.formatItem(out, r.get)
            }

            case Some(Conf.addtofavourites) =>{
                val  r = new ApiProxy(HttpClient)
                    .AddRecipeToShortlistRequestQuery(AddRecipeToShortlistRequest(sessionId, Conf.addtofavourites.url.get.get))
                RecipeResponseFormatter.formatItem(out, r.get)
            }

            case  None => {}
        }
    }


    def obtainSessionId(): String = {
        WhiskPermanentStorage.loadSessionId() match {
            case Some(s) => s
            case None  =>
                {
                    val response = new ApiProxy(HttpClient).createSession(CreateSessionRequest())
                    response.map(r=> r.header.sessionId) match {
                        case Some(x) => WhiskPermanentStorage.saveSessionId(x); x
                        case None  => ""
                    }

                }
        }
    }
}


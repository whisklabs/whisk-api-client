package whisk

import apiproxy.{HttpClient, HttpHandler, ApiProxy}
import org.rogach.scallop.{Subcommand, ScallopConf}
import protocol.recipes.RecipeQueryRequest


object WhiskCli{

////  whisk recipes topchefs
////  whisk recipes --site channel4 all
//  var seq: Seq[String] = Seq("recipes", "--site", "itv", "all")


    def main(args: Array[String]) = {
        try{
            object Conf extends ScallopConf(args.toSeq) {
                val recipes = new Subcommand("recipes") {
                    val site = opt[String]("site")
                    val list = trailArg[String](required = true)
                }
            }

            val list: String = Conf.recipes.list()
            var map: Map[String, Seq[String]] = Map(
                ("list",  Seq(list)),
                ("start", Seq("0")),
                ("count", Seq("10"))
            )

            if(Conf.recipes.site.isSupplied)
                map +=  (("site", Seq(Conf.recipes.site())))

            val r  = new ApiProxy(HttpClient).recipesQuery(RecipeQueryRequest(sessionId = "", params= map))
            r.get.data.get.recipes
                .map(RecipeFormatter.formatItem)
                .foreach(x => Console.println(x))

        } catch {
            case e: Exception => {
                e.printStackTrace()
            }
        }
    }
}


package whisk

import apiproxy.{HttpClient, HttpHandler, ApiProxy}
import org.rogach.scallop.{Subcommand, ScallopConf}
import protocol.recipes.RecipeQueryRequest


object WhiskCli extends App {

//  whisk recipes topchefs
//  whisk recipes all -site channel4
  var seq: Seq[String] = Seq("recipes", "--site", "itv", "all")
  //var seq: Seq[String] = Seq("recipes", "topchefs")

    object Conf extends ScallopConf(seq) {
        val recipes = new Subcommand("recipes") {
            val site = opt[String]("site")
            val list = trailArg[String](required = true)
        }
    }

    var map: Map[String, Seq[String]] = Map(
        ("list", Seq(Conf.recipes.list.get.getOrElse(""))),
        ("start", Seq("0")),
        ("count", Seq("1"))
    )
    val option = Conf.recipes.site.get
        option match {
            case Some(x) =>  {
                map +=  (("site", Seq(x)))
            }
            case _ => {}
      }


    try{
        val r  = new ApiProxy(HttpClient).recipesQuery(RecipeQueryRequest(sessionId = "", params= map))
        r.get.data.get.recipes
            .map(RecipeFormatter.formatItem)
            .foreach(x => Console.println(x))

    } catch {
        case e: Exception => {
            Console.println(e.toString())
        }
    }
}


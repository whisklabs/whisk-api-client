package whisk.protocol.client


import whisk.protocol.recipes.RecipeQueryRequest
import main.scala.whisk.protocol.client.{ApiProxy, RecipeFormatter}
import joptsimple.OptionParser


object Main extends App {

    val parser = new OptionParser()
    val listOp = parser.accepts("list").withRequiredArg().ofType(classOf[String])
    val chefOp = parser.accepts("chef").withRequiredArg().ofType(classOf[String])
    val startOp = parser.accepts("start").withRequiredArg().ofType(classOf[Int])
    val countOp = parser.accepts("count").withRequiredArg().ofType(classOf[Int])
    val siteOp = parser.accepts("site").withRequiredArg().ofType(classOf[String])

    try {
        val optionSet = parser.parse(args: _*)
        if (!optionSet.hasOptions){
            Console.println("Usage: whisk [-list list] [-chef chef] [-start 0] [-count 1] [-site site]")
            System.exit(-1);
        }

        val map = List("list", "start", "count", "site", "chef")
            .filter( v => optionSet.has(v) && optionSet.hasArgument(v))
            .map(v => (v, Seq(String.valueOf(optionSet.valueOf(v))))).toMap

        val r = ApiProxy.recipesQuery(RecipeQueryRequest(sessionId = "", params = map))

        r.get.data.get.recipes
            .map(RecipeFormatter.formatItem)
            .foreach(x => Console.println(x))

    } catch {
        case e: Exception => {
            Console.println(e.toString())
        }
    }

}







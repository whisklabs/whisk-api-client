package whisk

import org.scalatest.FunSuite
import org.rogach.scallop.{Subcommand, ScallopConf}

class ArgsParserTest extends  FunSuite {


    test("whisk recipes --site channel4 all") {

        val args  = Seq("recipes", "--site", "channel4", "all")
        object Conf extends ScallopConf(args) {
            val recipes = new Subcommand("recipes") {
                val searchText = opt[String]("search")
                val site = opt[String]("site")

                val favourites = trailArg[String]( name = "favourites", required = false)
                val list = trailArg[String](required = true)
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

        assert(Conf.recipes.site().equals("channel4"))
    }

    test("whisk recipes search chicken all (which does a search for chicken recipes)") {

        val args  = Seq("recipes", "--search", "chicken", "--site", "channel4", "all")
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

        assert(Conf.recipes.searchText().equals("chicken"))
        assert(Conf.recipes.site().equals("channel4"))
    }


    test("whisk recipes favourites all") {

        val args  = Seq("recipes", "favourites", "all")
        object Conf extends ScallopConf(args) {
            val recipes = new Subcommand("recipes") {
                val searchText = opt[String]("search")
                val site = opt[String]("site")

                val favourites = trailArg[String]( name = "favourites", required = false)
                val list = trailArg[String](required = true)
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

        assert(Conf.recipes.favourites.isSupplied)
        assert(Conf.recipes.list().equals("all"))
    }




  test("whisk addtofavourites http://www.jamieoliver.com:81/recipes/meat-recipes/flying-steak-swich (which adds the recipe to the users favourites)"){
        val args  = Seq("addtofavourites", "http://www.jamieoliver.com:81/recipes/meat-recipes/flying-steak-swich")

      object Conf extends ScallopConf(args) {
          val recipes = new Subcommand("recipes") {
              val searchText = opt[String]("search")
              val site = opt[String]("site")

              val favourites = trailArg[String]( name = "favourites", required = false)
              val list = trailArg[String](required = true)
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

      assert(Conf.subcommand match {
            case Some(Conf.addtofavourites) => true
            case Some(Conf.recipes) => false
            case _ => false;
        })

        assert(Conf.recipes.list.isSupplied)
        assert(Conf.addtofavourites.url.isSupplied)
  }

    test("whisk shoppinglistoptions http://www.jamieoliver.com:81/recipes/meat-recipes/flying-steak-swich (which gives you the options for the provided recipe)"){
        val args  = Seq("shoppinglistoptions", "http://www.jamieoliver.com:81/recipes/meat-recipes/flying-steak-swich")

        object Conf extends ScallopConf(args) {
            val recipes = new Subcommand("recipes") {
                val searchText = opt[String]("search")
                val site = opt[String]("site")

                val favourites = trailArg[String]( name = "favourites", required = false)
                val list = trailArg[String](required = true)
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

        assert(Conf.subcommand match {
            case Some(Conf.addtofavourites) => false
            case Some(Conf.recipes) => false
            case Some(Conf.shoppinglistoptions) => true
            case _ => false;
        })

        assert(Conf.recipes.list.isSupplied)
        assert(Conf.shoppinglistoptions.url.isSupplied)
    }

    test("whisk addtoshoppinglist http://www.jamieoliver.com:81/recipes/meat-recipes/flying-steak-swich"){
        val args  = Seq("addtoshoppinglist", "http://www.jamieoliver.com:81/recipes/meat-recipes/flying-steak-swich")

        object Conf extends ScallopConf(args) {
            val recipes = new Subcommand("recipes") {
                val searchText = opt[String]("search")
                val site = opt[String]("site")

                val favourites = trailArg[String]( name = "favourites", required = false)
                val list = trailArg[String](required = true)
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

        assert(Conf.subcommand match {
            case Some(Conf.addtoshoppinglist) => true
            case Some(Conf.addtofavourites) => false
            case Some(Conf.recipes) => false
            case Some(Conf.shoppinglistoptions) => false
            case _ => false;
        })

        assert(Conf.recipes.list.isSupplied)
        assert(Conf.shoppinglistoptions.url.isSupplied)
    }

    test("whisk login test.test@gmail.com aaaaaa (logs in)"){
        val args  = Seq("login", "test.test@gmail.com", "pass")

        object Conf extends ScallopConf(args) {
            val recipes = new Subcommand("recipes") {
                val searchText = opt[String]("search")
                val site = opt[String]("site")

                val favourites = trailArg[String]( name = "favourites", required = false)
                val list = trailArg[String](required = true)
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

        assert(Conf.subcommand match {
            case Some(Conf.login) => true
            case Some(Conf.addtoshoppinglist) => false
            case Some(Conf.addtofavourites) => false
            case Some(Conf.recipes) => false
            case Some(Conf.shoppinglistoptions) => false
            case _ => false;
        })

        assert(Conf.login.pass().equals("pass"))
    }
}

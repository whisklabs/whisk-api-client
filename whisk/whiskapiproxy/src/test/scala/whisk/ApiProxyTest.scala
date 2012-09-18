package whisk

import apiproxy.{HttpHandler, ApiProxy}
import cucumber.runtime.{ScalaDsl, EN}
import junit.framework.Assert._
import protocol.recipes.{RecipeQueryResponse, RecipeQueryRequest}


class ApiProxyTest extends ScalaDsl with EN {

//    @When("^Run with recipes -site \"([^\"]*)\" all$")
//    public void Run_with_recipes_site_all(String arg1) throws Throwable {
//        // Express the Regexp above with the code you wish you had
//        throw new PendingException();
//    }
//
//    When("""^Run with recipes -site "([^"]*)" all$"""){ (arg0:String) =>
//    //// Express the Regexp above with the code you wish you had
//        throw new PendingException()
//    }
//    @Then("^it should return the following site: \"([^\"]*)\"$")
//    public void it_should_return_the_following_site(String arg1) throws Throwable {
//        // Express the Regexp above with the code you wish you had
//        throw new PendingException();
//    }
//
//    Then("""^it should return the following site: "([^"]*)"$"""){ (arg0:String) =>
//    //// Express the Regexp above with the code you wish you had
//        throw new PendingException()
//    }

    var query: Option[RecipeQueryResponse] = None
    val proxy  = new ApiProxy(FakeHttpHandler)
    When("""Run with recipes -site (\s+) all""") { (arg:String)  =>
        var map: Map[String, Seq[String]] = Map(
            ("list", Seq("all")),
            ("site", Seq(arg)))

      query = proxy.recipesQuery(RecipeQueryRequest(sessionId = "", params = map))

    }
    Then ("""^it should return the following site : (\s+)$"""){ (site:String) =>
        query match {
            case r:RecipeQueryResponse => r.data.get.recipes.forall(c=> c.site.equals(site))
            case _ => assert(false)
        }
    }
}


object FakeHttpHandler extends HttpHandler{
    def handleGet(url: String) :String = {
        return """{"header":{"status":{"code":0,"desc":"Ok"},"sessionId":"undefined"},"data":{"recipes":[{"id":"50194dbd923b73af73e43ae4","url":"http://www.itv.com/food/recipes/spaghetti-carbonara","title":"Spaghetti carbonara","ingredients":[{"text":"100 g salted butter","group":"Default"},{"text":"3 free range egg yolks","group":"Default"},{"text":"3 handfuls of pancetta, dried","group":"Default"},{"text":"500 g spaghetti","group":"Default"},{"text":"Handful of parmesan cheese, freshly grated","group":"Default"},{"text":"Salt and pepper","group":"Default"}],"instructions":[{"text":"Cook the pasta in salted boiling water until al dente.","group":"Default"},{"text":"In a saucepan, slowly shallow fry the pancetta in the butter until very crispy.","group":"Default"},{"text":"Drain the pasta and add to the cooked pancetta. Mix the eggs yolks with the parmesan and add immediately. Season with salt and pepper and stir very well until you have a creamy texture.","group":"Default"},{"text":"Sprinkle over more parmesan cheese if desired and serve immediately.","group":"Default"}],"author":{"name":"Gino D'Acampo"},"site":{"name":"itv","rootUrl":"http://www.itv.com"},"images":[{"width":624,"height":351,"url":"http://www.itv.com/food/sites/default/files/imagecache/16-9_624x351/24.8.11 640x360.jpg"}],"videos":[],"serves":2,"tags":[],"lastCheckedAt":"Sep 13, 2012 1:02 PM","isFavourite":false,"suggestedPrice":{}}],"statistics":{"sites":[{"name":"itv","count":2501}],"tags":[{"name":"TopRecipes","count":1}]},"guid":"df211e09-d3ac-498b-903d-1629296cecf9"}}""";
    }
}


package whisk

import apiproxy.{HttpClient, HttpHandler, ApiProxy}
import cucumber.runtime.{ScalaDsl, EN}
import junit.framework.Assert._
import protocol.recipes.{RecipeQueryResponse, RecipeQueryRequest}


class ApiProxyTest extends ScalaDsl with EN {
    var query: Option[RecipeQueryResponse] = None
    val proxy  = new ApiProxy(FakeHttpHandler)
    When("""^Run with recipes -site "([^"]*)" all$""") { (arg:String)  =>
        var map: Map[String, Seq[String]] = Map(
            ("list", Seq("all")),
            ("site", Seq(arg)))

      query = proxy.recipesQuery(RecipeQueryRequest(sessionId = "", params = map))
    }


    Then ("""^it should return the following site: "([^"]*)"$"""){ (site:String) =>
        query match {
            case Some(r: RecipeQueryResponse) => {
                assert(r.data.get.recipes.forall(c=> c.site.name.equals(site)), "not itv found")
            }
            case None => assert(false, "failed getting response")
        }
    }
}


object FakeHttpHandler extends HttpHandler{
    def handleGet(url: String) :String = {
        return """{"header":{"status":{"code":0,"desc":"Ok"},"sessionId":"undefined"},"data":{"recipes":[{"id":"50194dbd923b73af73e43ae4","url":"http://www.itv.com/food/recipes/spaghetti-carbonara","title":"Spaghetti carbonara","ingredients":[{"text":"100 g salted butter","group":"Default"},{"text":"3 free range egg yolks","group":"Default"},{"text":"3 handfuls of pancetta, dried","group":"Default"},{"text":"500 g spaghetti","group":"Default"},{"text":"Handful of parmesan cheese, freshly grated","group":"Default"},{"text":"Salt and pepper","group":"Default"}],"instructions":[{"text":"Cook the pasta in salted boiling water until al dente.","group":"Default"},{"text":"In a saucepan, slowly shallow fry the pancetta in the butter until very crispy.","group":"Default"},{"text":"Drain the pasta and add to the cooked pancetta. Mix the eggs yolks with the parmesan and add immediately. Season with salt and pepper and stir very well until you have a creamy texture.","group":"Default"},{"text":"Sprinkle over more parmesan cheese if desired and serve immediately.","group":"Default"}],"author":{"name":"Gino D'Acampo"},"site":{"name":"itv","rootUrl":"http://www.itv.com"},"images":[{"width":624,"height":351,"url":"http://www.itv.com/food/sites/default/files/imagecache/16-9_624x351/24.8.11 640x360.jpg"}],"videos":[],"serves":2,"tags":[],"lastCheckedAt":"Sep 13, 2012 1:02 PM","isFavourite":false,"suggestedPrice":{}}],"statistics":{"sites":[{"name":"itv","count":2501}],"tags":[{"name":"TopRecipes","count":1}]},"guid":"df211e09-d3ac-498b-903d-1629296cecf9"}}""";
    }
}


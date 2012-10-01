package whisk

import cucumber.runtime.{ ScalaDsl, EN }
import apiproxy.{ HttpClient, ApiProxy }
import protocol.identity.{ AddRecipeToShortlistRequest, CreateSessionRequest }
import protocol.recipes.RecipeQueryRequest
import org.junit.Assert._
import scala.Some

class FavouritesStepDefs extends ScalaDsl with EN {

    val proxy = new ApiProxy(HttpClient)

    var sessionId: Option[String] = None

    var params: Map[String, Seq[String]] = Map.empty

    When("""^I Create  a  new session$""") { () =>
        sessionId = Some(proxy.createSession(CreateSessionRequest()).get.header.sessionId)
    }

    And("""^the site name "([^"]*)"$""") { (url: String) =>
        params += (("site", Seq(url)))
    }

    And("""^then add to favourites the recipe with Url "([^"]*)"$""") { (url: String) =>
        proxy.AddRecipeToShortlistRequestQuery(AddRecipeToShortlistRequest(sessionId.get, url))
    }

    Then("""^my favourites should include the recipe with Url "([^"]*)"$""") { (url: String) =>
        params += (("list", Seq("shortlist")))
        val result = proxy.recipesQuery(RecipeQueryRequest(sessionId.get, params))
        assertTrue(result.isDefined)
        val urls = result.get.data.get.recipes.map(r => r.url)

        assertTrue("expected %s  and given %s \n".format(url, urls),
            result.get.data.get.recipes.forall(r => r.url.equalsIgnoreCase(url)))

    }
}

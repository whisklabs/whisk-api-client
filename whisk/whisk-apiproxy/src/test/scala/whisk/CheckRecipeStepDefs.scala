package whisk

import cucumber.runtime.{ScalaDsl, EN}
import apiproxy.{HttpClient, ApiProxy}
import protocol.identity.{AddRecipeToShortlistRequest, CreateSessionRequest}
import protocol.recipes.{RecipeResponse, RecipeCheckRequest, RecipeQueryRequest}
import org.junit.Assert._
import scala.Some

class CheckRecipeStepDefs extends ScalaDsl with EN {

    val proxy = new ApiProxy(HttpClient)

    var sessionId:Option[String] = None

    var params :Map[String,Seq[String]] =  Map.empty
    var res : Option[RecipeResponse]  = None

    When("""^I  create a new session$"""){ () =>
        sessionId = Some(proxy.createSession(CreateSessionRequest()).get.header.sessionId)
    }

    And("""^I  ask it to check for  recipe url recipe "([^"]*)"$"""){ (url:String) =>
        res = proxy.recipeCheck(RecipeCheckRequest(sessionId.get, url, true))
    }
    Then("""^it  should return the recipe with Url "([^"]*)"$"""){ (url:String) =>
        assertTrue("check failed", res.get.recipe.isDefined)
        assertTrue("recipe url missmatch %s %s".format(res.get.recipe.get.url, url), res.get.recipe.get.url.equalsIgnoreCase(url))
    }
}

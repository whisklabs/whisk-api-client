package whisk

import apiproxy.{HttpClient, ApiProxy}
import cucumber.runtime.{ScalaDsl, EN}
import protocol.identity.CreateSessionRequest
import protocol.recipes.{Recipe, RecipeQueryRequest}
import scala.Some
import org.junit.Assert._

class QueryTopChefsStepDefs extends ScalaDsl with EN {

    val proxy = new ApiProxy(HttpClient)

    var sessionId:Option[String] = None

    var params :Map[String,Seq[String]] =  Map.empty


    When("""^I create a new session$""") {
        sessionId = Some(proxy.createSession(CreateSessionRequest()).get.header.sessionId)
    }

    And("""^I ask it to search for "([^"]*)"$"""){ (list:String) =>
        params += (("list", Seq(list)))
    }


    Then("""^it should return the following chefs:$"""){ (rawchefs:String) =>
        val result = proxy.recipesQuery(RecipeQueryRequest(sessionId.get, params))
        assertTrue(result.isDefined)
        val expected = rawchefs.split("\n").map(s => s.trim)
        val given = result.get.data.get.recipes.map(r => r.author.get.name)
        assertTrue("expected %s given %s\n".format(expected.toSeq, given.toSeq),
            given.exists(chef => expected.exists(c => c.equalsIgnoreCase(chef))))
    }
}


class QuerySearchForRecipeStepDefs extends ScalaDsl with EN {

    val proxy = new ApiProxy(HttpClient)

    var sessionId:Option[String] = None

    var params :Map[String,Seq[String]] =  Map.empty


    When("""^I create a new   session$""") {
        sessionId = Some(proxy.createSession(CreateSessionRequest()).get.header.sessionId)
    }


    And("""the site "([^"]*)"$""")  { (url:String) =>
        params += (("site", Seq(url)))
    }

    And("""^I ask it to search for the text "([^"]*)"$"""){ (arg0:String) =>
        params += (("searchText", Seq(arg0)))
    }

    Then("""^it should return the recipe with Url "([^"]*)"$"""){ (url:String) =>
        val result = proxy.recipesQuery(RecipeQueryRequest(sessionId.get, params))
        assertTrue(result.isDefined)
        val urls = result.get.data.get.recipes.map(r => r.url)
        assertTrue("expected %s  and given %s \n".format(url, urls),
            result.get.data.get.recipes.exists( r=> r.url.equalsIgnoreCase(url)) )

    }
}










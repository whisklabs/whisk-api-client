package whisk


import apiproxy.{HttpClient, ApiProxy}
import cucumber.runtime.{ScalaDsl, EN}
import junit.framework.Assert._
import protocol.identity.CreateSessionRequest
import protocol.shoppinglist.AddToShoppingListRequest
import protocol.shoppinglist.AddToShoppingListResponse
import scala.Some

/**
 * Created by IntelliJ IDEA.
 * User: gus
 * Date: 20.09.12 20:08
 * To change this template use File | Settings | File Templates.
 */
class AddToShoppingListTest  extends ScalaDsl with EN {
    var result: Option[AddToShoppingListResponse] = None
    val proxy  = new ApiProxy(HttpClient)


    When("""^Add to shopping list with  name: "([^"]*)" store: "([^"]*)" and recipe url "([^"]*)"$"""){ (name:String, store:String, url:String) =>
        val response = new ApiProxy(HttpClient).createSession(CreateSessionRequest())
        result = proxy.addToShoppingListQuery(AddToShoppingListRequest(sessionId = response.get.header.sessionId, servings = Some(1), recipeUrl = url, store = Some(store), shoppingListName = Some(name)))
    }
    Then("""^it should return the shopping list with  name "([^"]*)" and store "([^"]*)"$"""){ (name:String, store:String) =>
        assert(result.isDefined, "request is not processed")

        assertEquals(result.get.shoppingList.store.name, store)
        assertEquals(result.get.shoppingList.name.get, name)

    }
}



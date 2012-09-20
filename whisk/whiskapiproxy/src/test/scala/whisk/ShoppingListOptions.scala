package whisk

import apiproxy.{HttpClient, ApiProxy}
import cucumber.runtime.{ScalaDsl, EN}
import junit.framework.Assert._
import protocol.identity.CreateSessionRequest
import protocol.identity.CreateSessionRequest
import protocol.shoppinglist._
import protocol.shoppinglist.ShoppingListOptionsRequest
import protocol.shoppinglist.ShoppingListOptionsResponse
import scala.Some

/**
 * Created by IntelliJ IDEA.
 * User: gus
 * Date: 20.09.12 20:08
 * To change this template use File | Settings | File Templates.
 */
class ShoppingListOptions extends ScalaDsl with EN {
    var result: Option[ShoppingListOptionsResponse] = None
    val proxy  = new ApiProxy(HttpClient)


    When("""^Request shopping list options with store: "([^"]*)" and recipe url: "([^"]*)"$"""){  (store:String, url:String) =>
             val response = new ApiProxy(HttpClient).createSession(CreateSessionRequest())

             val mutation = ShoppingListEntryOptionMutation("4ff5488230046ad82c20ef7b", Some("4ff506bc029f706f1c231603"), None)
             result = proxy.shoppingListOptionsQuery(ShoppingListOptionsRequest(response.get.header.sessionId, url, Some(1), Some(store), Seq(mutation) ))
         }

    Then("""^it should return the shopping list with   store "([^"]*)"$"""){ (store:String) =>
             assert(result.isDefined, "request is not processed")

             assertEquals(result.get.store.name, store)
         }
}

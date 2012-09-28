package whisk

import apiproxy.{HttpClient, ApiProxy}
import cucumber.runtime.{ScalaDsl, EN}
import protocol.identity.CreateSessionRequest
import protocol.shoppinglist._
import protocol.shoppinglist.ShoppingListOptionsRequest
import protocol.shoppinglist.ShoppingListOptionsResponse
import org.junit.Assert._
import scala.Some

class GetShoppinglistoptionsAndAddToShoppinglistStepDefs extends ScalaDsl with EN {

    val proxy = new ApiProxy(HttpClient)

    var sessionId: Option[String] = None
    var params: Map[String, Seq[String]] = Map.empty
    var toChange: Seq[ShoppingListEntryOptionMutation] = Seq.empty
    var res: Option[ShoppingListOptionsResponse] = None
    var recipeUrl: Option[String] = None

    When( """^I create a  new session$""") {
        () =>
            sessionId = Some(proxy.createSession(CreateSessionRequest()).get.header.sessionId)
    }

    And( """^I ask it to get the shopping list options for "([^"]*)"$""") {
        (url: String) =>
            recipeUrl = Some(url)
            res = proxy.shoppingListOptionsQuery(ShoppingListOptionsRequest(sessionId.get, url, None, None))
    }

    And( """^I change the option for "([^"]*)" to "([^"]*)"$""") {
        (ingredient: String, optionNew: String) =>
            val entry = res.get.entries.find(e => e.recipeIngredient.equalsIgnoreCase(ingredient))
            val storeItemId = entry.get.entryOptions.flatMap(v => v.storeItems).find(x => x.name.equalsIgnoreCase(optionNew))
            toChange = Seq(ShoppingListEntryOptionMutation(entry.get.entryOptions.head.id, Some(storeItemId.get.storeItemId), Some(ShoppingListStoreItemDecision.Add)))
            proxy.shoppingListOptionsPost(ShoppingListOptionsRequest(sessionId.get, recipeUrl.get, None, None, toChange))
    }

    And( """^I add the recipe to a shopping list at "([^"]*)"$""") {
        (store: String) =>
            proxy.addToShoppingListQuery(AddToShoppingListRequest(sessionId.get, recipeUrl.get, None, Some(store), toChange))
    }

    Then( """^shopping list should include "([^"]*)"$""") {
        (storeItemName: String) =>
            val r = proxy.shoppingListQuery(GetShoppingListRequest(sessionId.get, None))
            val storeItems = r.get.shoppingList.get.storeItems.map(c => c.name)
            assertTrue("expected %s  given %s".format(storeItemName, storeItems), storeItems.exists(c => c.equalsIgnoreCase(storeItemName)))
    }
}


class GetShoppinglistoptionsWaitroseAndTescoStepDefs extends ScalaDsl with EN {
    val proxy = new ApiProxy(HttpClient)

    var sessionId: Option[String] = None
    var params: Map[String, Seq[String]] = Map.empty
    var toChange: Seq[ShoppingListEntryOptionMutation] = Seq.empty
    var res: Option[ShoppingListOptionsResponse] = None
    var recipeUrl: Option[String] = None

    When( """^I create a new    session$""") {
        () =>
            sessionId = Some(proxy.createSession(CreateSessionRequest()).get.header.sessionId)
    }



    When( """^I ask it to get the shopping list options for   "([^"]*)"$""") {   (url: String) =>
        recipeUrl = Some(url)
        res = proxy.shoppingListOptionsQuery(ShoppingListOptionsRequest(sessionId.get, url, None, None))
    }


    Then( """^the supermarkets "([^"]*)" and "([^"]*)" should be returned$""") {
        (market1: String, market2: String) =>

        val expected = Seq(market1, market2)
        val resultStores: Seq[String] = res.get.availableStores.map(x=> x.name)
        assertTrue("expected %s  given %s".format(expected, resultStores), !expected.intersect(resultStores).isEmpty)
    }
}



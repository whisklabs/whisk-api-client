package whisk.apiproxy

import whisk.protocol.recipes.RecipeQueryRequest
import whisk.protocol.shoppinglist.{GetShoppingListRequest, ShoppingListOptionsRequest}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import scala.collection.JavaConverters._;

object UrlBuilder {
    def getShoppingListRequestUrl(r: GetShoppingListRequest): String ={
        var m = Map(("sessionId", r.sessionId))

        if(r.shoppingListName.isDefined)
            m += (("shoppingListName", r.shoppingListName.get))

        val parameters  = m.map({  case (k, v) =>  new BasicNameValuePair(k, v) }).toSeq
        return buildGetUrl("shoppingList/show", URLEncodedUtils.format(parameters.asJava, "utf-8"))
    }

    def getAddRecipeToShortlistUrl(): String ={
        buildUrl("shortlist/recipes/add")
    }

    def getShoppingListOptionsUrl(): String ={
        buildUrl("shoppingList/options")
    }

    def getAddToShoppingListUrl(): String ={
        buildUrl("shoppingList/add")
    }


    def getRecipesQueryUrl(r: RecipeQueryRequest): String = {
        val parameters = (
                Map(("sessionId", r.sessionId)) ++
                r.params.map { case (k, s) => (k, s.head) }
            )
            .map({
                case (k, v) => new BasicNameValuePair(k, v)
            }).toSeq

        return buildGetUrl("recipes/query", URLEncodedUtils.format(parameters.asJava, "utf-8"))
    }


    def getShoppingListOptionsUrl(r: ShoppingListOptionsRequest): String = {
        var m = Map(("sessionId", r.sessionId),
            ("recipeUrl", r.recipeUrl))

        if(r.store.isDefined)
            m += (("store", r.store.get))

        val parameters  = m.map({  case (k, v) =>  new BasicNameValuePair(k, v) }).toSeq

        return buildGetUrl("shoppingList/options",  URLEncodedUtils.format(parameters.asJava, "utf-8"))
    }


    def getLoginUrl() : String ={
        buildUrl("auth/login")
    }

    def getCreateSessionUrl() : String ={
        buildUrl("sessions/create")
    }

    private def buildGetUrl(queryType: String, params: String) = {
        "http://test-apiadmin.whisk.co.uk/api/" + queryType + "?" + params;
    }

    private def buildUrl(queryType: String) = {
        "http://test-apiadmin.whisk.co.uk/api/" + queryType
    }
}

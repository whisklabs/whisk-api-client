package whisk.apiproxy

import whisk.protocol.recipes.RecipeQueryRequest
import whisk.protocol.shoppinglist.ShoppingListOptionsRequest

object UrlBuilder {
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
        val parameters  = (
                Map(("sessionId", r.sessionId)) ++
                r.params.map { case (k, s) => (k, s.head) }
            )
            .map({
                case (k, v) => "%s=%s".format(k, v)
            })

        val getArgs = parameters.reduceLeft({ (acc, s) => acc + "&" + s })
        return buildGetUrl("recipes/query", getArgs)
    }


    def getShoppingListOptionsUrl(r: ShoppingListOptionsRequest): String = {
        val parameters  = (
            Map(("sessionId", r.sessionId),
                ("store", r.store.get),
                ("recipeUrl", r.recipeUrl)
            ))
            .map({
            case (k, v) => "%s=%s".format(k, v)
        })

        val getArgs = parameters.reduceLeft({ (acc, s) => acc + "&" + s })
        return buildGetUrl("shoppingList/options", getArgs)
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

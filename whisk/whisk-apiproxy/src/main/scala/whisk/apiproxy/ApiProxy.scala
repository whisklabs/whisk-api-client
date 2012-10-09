package whisk.apiproxy

import net.liftweb.json.{ parseOpt, pretty, render, DefaultFormats }
import net.liftweb.json.Extraction._
import whisk.protocol.recipes._
import java.io.IOException
import whisk.protocol.shoppinglist._
import whisk.protocol.recipes.RecipeResponse
import whisk.protocol.shoppinglist.AddToShoppingListRequest
import whisk.protocol.recipes.RecipeQueryRequest
import whisk.protocol.shoppinglist.ShoppingListOptionsRequest
import whisk.protocol.shoppinglist.ShoppingListOptionsResponse
import whisk.protocol.identity.LoginRequest
import whisk.protocol.identity.LoginResponse
import whisk.protocol.identity.CreateSessionResponse
import whisk.protocol.identity.AddRecipeToShortlistRequest
import whisk.protocol.identity.CreateSessionRequest
import scala.Some
import whisk.protocol.recipes.RecipeQueryResponse
import whisk.protocol.shoppinglist.AddToShoppingListResponse
import whisk.protocol.common.BasicWhiskResponse
import net.liftweb.json.ext.EnumSerializer

class ApiProxy(httpHandler: HttpHandler) {

    private implicit val formats = DefaultFormats + new CustomShoppingListStoreItemSerializer + new EnumSerializer(whisk.protocol.shoppinglist.ShoppingListStoreItemDecision)

    def recipeCheck(r: RecipeCheckRequest): Option[RecipeResponse] = {
        val url: String = UrlBuilder.getRecipeCheckUrl(r)
        val response: String = httpHandler.handleGet(url)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[RecipeResponse] match {
                    case Some(m) => Some(m)
                    case None    => throw new IOException(jv.extractOpt[BasicWhiskResponse].toString)
                }
            }
            case _ => None
        }
    }

    def AddRecipeToShortlistRequestQuery(r: AddRecipeToShortlistRequest): Option[RecipeResponse] = {
        val url: String = UrlBuilder.getAddRecipeToShortlistUrl()
        val response: String = httpHandler.handlePost(url, Map(
            ("recipeUrl", r.recipeUrl),
            ("sessionId", r.sessionId)))
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[RecipeResponse]
            }
            case _ => None
        }
    }

    def addToShoppingListQuery(r: AddToShoppingListRequest): Option[AddToShoppingListResponse] = {
        val url: String = UrlBuilder.getAddToShoppingListUrl()
        val jsonContent = pretty(render(decompose(r)))
        val response: String = httpHandler.handlePost(url, jsonContent)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[AddToShoppingListResponse]
            }
            case _ => None
        }
    }

    def shoppingListOptionsQuery(r: ShoppingListOptionsRequest): Option[ShoppingListOptionsResponse] = {
        val url: String = UrlBuilder.getShoppingListOptionsUrl(r)
        val response: String = httpHandler.handleGet(url)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[ShoppingListOptionsResponse] match {
                    case Some(m) => Some(m)
                    case None    => throw new IOException(jv.extractOpt[BasicWhiskResponse].toString)
                }
            }
            case _ => None
        }
    }

    def shoppingListOptionsPost(r: ShoppingListOptionsRequest): Option[ShoppingListOptionsResponse] = {
        val url: String = UrlBuilder.getShoppingListOptionsUrl()
        val jsonContent = pretty(render(decompose(r)))
        val response: String = httpHandler.handlePost(url, jsonContent)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[ShoppingListOptionsResponse]
            }
            case _ => None
        }
    }

    def shoppingListQuery(r: GetShoppingListRequest): Option[GetShoppingListResponse] = {
        val url: String = UrlBuilder.getShoppingListRequestUrl(r)
        val response: String = httpHandler.handleGet(url)

        parseOpt(response) match {
            case Some(jv) => {
                Some(jv.extract[GetShoppingListResponse])
            }
            case _ => None
        }
    }

    def recipesQuery(r: RecipeQueryRequest): Option[RecipeQueryResponse] = {
        val url: String = UrlBuilder.getRecipesQueryUrl(r)
        val response: String = httpHandler.handleGet(url)
        parseOpt(response) match {
            case Some(jv) => {
                Some(jv.extract[RecipeQueryResponse])
            }
            case _ => None
        }
    }

    def createSession(r: CreateSessionRequest): Option[CreateSessionResponse] = {
        val url: String = UrlBuilder.getCreateSessionUrl()
        val jsonContent = pretty(render(decompose(r)))
        val response: String = httpHandler.handlePost(url, jsonContent)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[CreateSessionResponse]
            }
            case _ => None
        }
    }

    def loginQuery(r: LoginRequest): Option[LoginResponse] = {
        val url: String = UrlBuilder.getLoginUrl()
        val jsonContent = pretty(render(decompose(r)))
        val response: String = httpHandler.handlePost(url, jsonContent)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[LoginResponse]
            }
            case _ => None
        }
    }
}


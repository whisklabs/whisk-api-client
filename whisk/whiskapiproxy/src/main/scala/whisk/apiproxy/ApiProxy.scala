package whisk.apiproxy

import net.liftweb.json.{parseOpt, pretty, render, DefaultFormats}
import net.liftweb.json.Extraction._
import whisk.protocol.recipes._
import scala.Some
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.{HttpPost, HttpGet}
import org.apache.http.util.EntityUtils
import java.io.IOException
import whisk.protocol.identity._
import org.apache.http.entity.{ContentType, StringEntity}
import whisk.protocol.shoppinglist.{AddToShoppingListResponse, AddToShoppingListRequest, ShoppingListOptionsResponse, ShoppingListOptionsRequest}
import whisk.protocol.shoppinglist.AddToShoppingListRequest
import whisk.protocol.shoppinglist.ShoppingListOptionsRequest
import whisk.protocol.shoppinglist.ShoppingListOptionsResponse
import whisk.protocol.identity.LoginRequest
import whisk.protocol.identity.LoginResponse
import whisk.protocol.identity.CreateSessionResponse
import whisk.protocol.identity.CreateSessionRequest
import scala.Some
import whisk.protocol.shoppinglist.AddToShoppingListResponse
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


class ApiProxy(httpHandler: HttpHandler) {

    private implicit val formats = DefaultFormats


    def AddRecipeToShortlistRequestQuery(r: AddRecipeToShortlistRequest) : Option[RecipeResponse] ={
        val url: String = UrlBuilder.getAddRecipeToShortlistUrl()
        val jsonContent = pretty(render(decompose(r)))
        val response: String = httpHandler.handlePost(url, jsonContent)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[RecipeResponse]
            }
            case _ => None
        }
    }



    def addToShoppingListQuery(r: AddToShoppingListRequest) : Option[AddToShoppingListResponse] ={
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

    def shoppingListOptionsQuery(r: ShoppingListOptionsRequest) : Option [ShoppingListOptionsResponse] ={
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


    def recipesQuery(r: RecipeQueryRequest): Option[RecipeQueryResponse] = {
        val url: String = UrlBuilder.getRecipesQueryUrl(r)
        val response: String = httpHandler.handleGet(url)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[RecipeQueryResponse]
            }
            case _ => None
        }
    }


    def createSession(r: CreateSessionRequest) :Option[CreateSessionResponse] ={
        val url: String = UrlBuilder.getCreateSessionUrl()
        val jsonContent = pretty(render(decompose(r)))
        val response :String  = httpHandler.handlePost(url, jsonContent)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[CreateSessionResponse]
            }
            case _ => None
        }
    }

    def loginQuery(r: LoginRequest) : Option[LoginResponse] ={
        val url: String = UrlBuilder.getLoginUrl()
        val jsonContent = pretty(render(decompose(r)))
        val response :String  = httpHandler.handlePost(url, jsonContent)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[LoginResponse]
            }
            case _ => None
        }
    }
}


trait HttpHandler
{
    def handleGet(url:String) : String
    def handlePost(url:String, postContent:String) :String
}

object HttpClient extends HttpHandler
{
    var logger = ( x:String ) => {  }
//    var logger = ( x:String ) => {  println(x) }

    def handleGet(url :String): String = {
        val httpClient = new DefaultHttpClient()
        try {
            logger(url)
            val request = new HttpGet(url)
            val response = httpClient.execute(request)
            if (response.getStatusLine.getStatusCode == 200) {
                val result: String = EntityUtils.toString(response.getEntity, "UTF-8")
                logger(result)
                return new String(result)
            }
            throw new IOException(response.toString)
        } finally {
            httpClient.getConnectionManager().shutdown()
        }
    }

    def handlePost(url: String, postContent: String):String ={
        val httpClient = new DefaultHttpClient()
        try {
            logger(url)
            val request = new HttpPost(url)
            request.setEntity(new StringEntity(postContent, ContentType.APPLICATION_FORM_URLENCODED))
            logger(postContent)
            val response = httpClient.execute(request)
            if (response.getStatusLine.getStatusCode == 200) {
                val result: String = EntityUtils.toString(response.getEntity, "UTF-8")
                logger(result)
                return new String(result)
            }
            throw new IOException(response.toString)
        } finally {
            httpClient.getConnectionManager().shutdown()
        }
    }
}


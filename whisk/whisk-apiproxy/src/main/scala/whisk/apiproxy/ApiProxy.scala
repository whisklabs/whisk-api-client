package whisk.apiproxy

import net.liftweb.json.{parseOpt, pretty, render, DefaultFormats}
import net.liftweb.json.Extraction._
import whisk.protocol.recipes._
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.{HttpPost, HttpGet}
import org.apache.http.util.EntityUtils
import java.io.{File, IOException}
import whisk.protocol.shoppinglist._
import whisk.protocol.recipes.RecipeQueryRequest
import whisk.protocol.identity.LoginRequest
import whisk.protocol.identity.LoginResponse
import whisk.protocol.identity.CreateSessionResponse
import whisk.protocol.identity.AddRecipeToShortlistRequest
import whisk.protocol.identity.CreateSessionRequest
import scala.Some
import whisk.protocol.recipes.RecipeQueryResponse
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair
import org.apache.http.entity.{ContentType, StringEntity}
import scala.collection.JavaConverters._
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
;

class ApiProxy(httpHandler: HttpHandler) {



    private implicit val formats = DefaultFormats + new CustomShoppingListStoreItemSerializer


    def AddRecipeToShortlistRequestQuery(r: AddRecipeToShortlistRequest) : Option[RecipeResponse] ={
        val url: String = UrlBuilder.getAddRecipeToShortlistUrl()
        val response: String = httpHandler.handlePost(url, Map(
            ("recipeUrl", r.recipeUrl),
            ("sessionId", r.sessionId)
        ))
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
        val url: String = UrlBuilder.getShoppingListOptionsUrl(r)
        val response: String = httpHandler.handleGet(url)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[ShoppingListOptionsResponse] match {
                    case Some(m) => Some(m)
                    case None => throw new IOException(jv.extractOpt[BasicWhiskResponse].toString)
                }
            }
            case _ => None
        }
    }

    def shoppingListOptionsPost(r: ShoppingListOptionsRequest) :Option[ShoppingListOptionsResponse] ={
        val url: String = UrlBuilder.getShoppingListOptionsUrl()
        val jsonContent = pretty(render(decompose(r)))
        val response :String  = httpHandler.handlePost(url, jsonContent)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[ShoppingListOptionsResponse]
            }
            case _ => None
        }
    }

    def shoppingListQuery(r: GetShoppingListRequest) : Option[GetShoppingListResponse] ={
        val url: String = UrlBuilder.getShoppingListRequestUrl(r)
        val response: String = httpHandler.handleGet(url)

        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[GetShoppingListResponse]
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
    def handlePost(url:String,  params:Map[String,String]) :String
    def handlePost(url:String,  jsonContent:String) :String
}

object HttpClient extends HttpHandler
{
    //var logger = ( x:String ) => {  }
    var logger = ( x:String ) => {  println(x) }

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

    def handlePost(url: String, params:Map[String,String]):String ={
        val httpClient = new DefaultHttpClient()
        try {
            logger(url)
            val request = new HttpPost(url)
            val postParams =  params.map(p => new BasicNameValuePair(p._1, p._2))
            request.setEntity(new UrlEncodedFormEntity(postParams.asJava))
            postParams.foreach(x => logger(x.toString))
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

    def handlePost(url: String, jsonContent:String):String ={
        val httpClient = new DefaultHttpClient()
        try {
            logger(url)
            val request = new HttpPost(url)
            request.setEntity(new StringEntity(jsonContent, ContentType.APPLICATION_JSON))
            logger(jsonContent)
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


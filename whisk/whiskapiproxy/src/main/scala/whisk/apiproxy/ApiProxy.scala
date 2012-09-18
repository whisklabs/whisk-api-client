package whisk.apiproxy

import net.liftweb.json.{parseOpt, DefaultFormats}
import whisk.protocol.recipes.{RecipeQueryResponse, RecipeQueryRequest}
import scala.Some
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import java.io.IOException



class ApiProxy(httpHandler: HttpHandler) {
    private implicit val formats = DefaultFormats

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
}


trait HttpHandler
{
    def handleGet(url:String) : String
}

object HttpClient  extends HttpHandler
{
    def handleGet(url :String): String = {
        val httpClient = new DefaultHttpClient()
        try {
            val request = new HttpGet(url)
            val response = httpClient.execute(request)
            if (response.getStatusLine.getStatusCode == 200) {
                val result: String = EntityUtils.toString(response.getEntity, "UTF-8")
                return new String(result)
            }
            throw new IOException(response.toString)
        } finally {
            httpClient.getConnectionManager().shutdown()
        }
    }
}


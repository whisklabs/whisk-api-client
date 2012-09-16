package main.scala.whisk.protocol.client

import net.liftweb.json.{parseOpt, DefaultFormats}
import whisk.protocol.recipes.{RecipeQueryResponse, RecipeQueryRequest}
import scala.Some
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import java.io.IOException

object ApiProxy {
    private implicit val formats = DefaultFormats

    def recipesQuery(r: RecipeQueryRequest): Option[RecipeQueryResponse] = {
        val urlArgs = r.params.map {
            case (k, s) => (k, s.head)
        }
        val response: String = handleGet("recipes/query", urlArgs)
        parseOpt(response) match {
            case Some(jv) => {
                jv.extractOpt[RecipeQueryResponse]
            }
            case _ => None
        }
    }

    private def buildGetUrl(queryType: String, params: String) = {
        "http://test-apiadmin.whisk.co.uk/api/" + queryType + "?" + params;
    }


    private def handleGet(queryType: String, query: Map[String, String]): String = {
        val httpClient = new DefaultHttpClient()
        try {
            val getArgs = query.map({
                case (k, v) => "%s=%s".format(k, v)
            }).reduceLeft({
                (acc, s) => acc + "&" + s
            })
            val url = buildGetUrl(queryType, getArgs)
            Console.println(url)
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

package whisk.apiproxy

import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.{ HttpPost, HttpGet }
import org.apache.http.util.EntityUtils
import java.io.IOException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair
import org.apache.http.entity.{ ContentType, StringEntity }
import scala.collection.JavaConverters._

object HttpClient extends HttpClient(NullLogger) {

}
class HttpClient(private val logger: Logger = NullLogger) extends HttpHandler {

    def handleGet(url: String): String = {
        val httpClient = new DefaultHttpClient()
        try {
            val request = new HttpGet(url)
            LogFormatter.formatRequest(logger, request)
            val response = httpClient.execute(request)
            LogFormatter.formatResponseHeader(logger, response)

            if (response.getStatusLine.getStatusCode == 200) {
                val result: String = EntityUtils.toString(response.getEntity, "UTF-8")
                LogFormatter.formatResponseContent(logger, result)
                return new String(result)
            }
            throw new IOException(response.toString)
        }
        finally {
            httpClient.getConnectionManager().shutdown()
        }
    }

    def handlePost(url: String, params: Map[String, String]): String = {
        val httpClient = new DefaultHttpClient()
        try {
            val request = new HttpPost(url)
            val postParams = params.map(p => new BasicNameValuePair(p._1, p._2))
            request.setEntity(new UrlEncodedFormEntity(postParams.asJava))
            LogFormatter.formatRequest(logger, request, params)
            val response = httpClient.execute(request)
            LogFormatter.formatResponseHeader(logger, response)

            if (response.getStatusLine.getStatusCode == 200) {
                val result: String = EntityUtils.toString(response.getEntity, "UTF-8")
                LogFormatter.formatResponseContent(logger, result)
                return new String(result)
            }
            throw new IOException(response.toString)
        }
        finally {
            httpClient.getConnectionManager().shutdown()
        }
    }

    def handlePost(url: String, jsonContent: String): String = {
        val httpClient = new DefaultHttpClient()
        try {
            val request = new HttpPost(url)
            request.setEntity(new StringEntity(jsonContent, ContentType.APPLICATION_JSON))
            LogFormatter.formatRequest(logger, request, jsonContent)
            val response = httpClient.execute(request)
            LogFormatter.formatResponseHeader(logger, response)

            if (response.getStatusLine.getStatusCode == 200) {
                val result: String = EntityUtils.toString(response.getEntity, "UTF-8")
                LogFormatter.formatResponseContent(logger, result)
                return new String(result)
            }
            throw new IOException(response.toString)
        }
        finally {
            httpClient.getConnectionManager().shutdown()
        }
    }
}


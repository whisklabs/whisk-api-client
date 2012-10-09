package whisk.apiproxy

import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.HttpResponse

object LogFormatter {
    def formatRequest(log: Logger, req: HttpRequestBase) = {
        log.info("-" * 120)
        log.info("Request type   : "+req.getMethod)
        log.info("Request URL    : "+req.getURI)
    }

    def formatRequest(log: Logger, req: HttpRequestBase, params: Map[String, String]) = {
        log.info("-" * 120)
        log.info("Request type   : "+req.getMethod)
        log.info("Request URL    : "+req.getURI)
        log.info("Request Params : ")
        params.map(c => "%s = %s".format(c._1, c._2)).foreach(log.info)
    }

    def formatRequest(log: Logger, req: HttpRequestBase, body: String) = {
        log.info("-" * 120)
        log.info("Request type   : "+req.getMethod)
        log.info("Request URL    : "+req.getURI)
        log.info("Request Body   : "+body)
    }

    def formatResponseHeader(log: Logger, res: HttpResponse) = {
        log.info("-" * 120)
        log.info("Response Status : "+res.getStatusLine)
        log.info("Response Header : "+res.getAllHeaders.flatMap(h => h.getElements)
            .map(e => e.getName)
            .reduceLeft((ac: String, c) => ac+", "+c))
    }

    def formatResponseContent(log: Logger, data: String) = {
        log.info("-" * 120)
        log.info("Response Body   : "+data)
    }
}

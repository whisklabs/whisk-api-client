package whisk.apiproxy

/** Created by IntelliJ IDEA.
 *  Date: 09.10.12 11:34
 *  To change this template use File | Settings | File Templates.
 */
trait HttpHandler {
    def handleGet(url: String): String
    def handlePost(url: String, params: Map[String, String]): String
    def handlePost(url: String, jsonContent: String): String
}

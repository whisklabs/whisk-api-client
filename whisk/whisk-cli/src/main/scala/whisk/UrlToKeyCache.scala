package whisk

/** Created by IntelliJ IDEA.
 *  Date: 11.10.12 19:04
 *  To change this template use File | Settings | File Templates.
 */
object UrlToKeyCache extends UrlToKeyCache(WhiskPermanentStorage.loadUrls, WhiskPermanentStorage.saveUrls) {
}

class UrlToKeyCache(private val loader: () => Map[String, String], private val saver: (Map[String, String]) => Unit) {

    import Utils._

    private var isInitialized: Boolean = false
    private var data: Map[String, String] = Map.empty

    private def loadIfNeed(): Unit = {
        if (!isInitialized) {
            val urls: Map[String, String] = loader()
            data = urls
            isInitialized = true
        }
    }

    def getAndSetKeyByUrl(url: String): String = {
        loadIfNeed()
        data.find(k => k._2.equals(url)) match {
            case Some(m) => m._1
            case None => {
                val urlKey: String = mangle(url)
                data += ((urlKey, url))
                urlKey
            }
        }
    }

    def getUrlByKey(key: String): Option[String] = {
        loadIfNeed()
        data.get(key)
    }

    def flush() = {
        saver(data)
    }
}

object Utils {
    def mangle(url: String): String = {
        Integer.toHexString(url.hashCode).toUpperCase
    }
}


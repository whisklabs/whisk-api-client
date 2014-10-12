package whisk

import scala._

import java.util.prefs.Preferences

object WhiskPermanentStorage extends WhiskPermanentStorage {

}

class WhiskPermanentStorage {
    val preferences: Preferences = Preferences.userNodeForPackage(classOf[WhiskPermanentStorage])
    val sessionKey: String = "SessionId"
    val urlsCacheKey: String = "UrlsCache"

    def loadSessionId(): Option[String] = {

        val r = preferences.get(sessionKey, null);
        return if (r != null) Some(r) else None
    }

    def saveSessionId(sessionID: String) = {
        preferences.put(sessionKey, sessionID)
        preferences.flush();
    }

    def loadUrls(): Map[String, String] = {
        val node: Preferences = preferences.node(urlsCacheKey)
        node.keys().map(k => (k, node.get(k, null))).toMap.filterNot(m => m._2 == null)
    }

    def saveUrls(cache: Map[String, String]) = {
        preferences.remove(urlsCacheKey)
        val node = preferences.node(urlsCacheKey)
        cache.foreach(m => node.put(m._1, m._2))
        preferences.flush()
    }

    def cleanSessionId() {
        preferences.remove(sessionKey)
        preferences.flush()
    }
}

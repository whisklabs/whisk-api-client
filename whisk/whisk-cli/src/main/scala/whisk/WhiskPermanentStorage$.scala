package whisk

import scala._

import java.util.prefs.Preferences


object WhiskPermanentStorage extends  WhiskPermanentStorage
{

}

class WhiskPermanentStorage  {
    val preferences: Preferences = Preferences.userNodeForPackage(classOf[WhiskPermanentStorage])
    val key: String = "SessionId"

    def loadSessionId(): Option[String] = {

        val r = preferences.get(key, null);
        return  if(r != null)  Some(r) else  None
    }

    def saveSessionId(sessionID: String) = {
        preferences.put(key, sessionID)
        preferences.flush();
    }

    def clean(){
        preferences.remove(key)
        preferences.flush()
    }
}

package whisk

import apiproxy.Logger

object ConsoleLogger extends Logger {
    def info(msg: String) {
        println(msg)
    }
}

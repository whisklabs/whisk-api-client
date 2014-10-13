package whisk.apiproxy

/** Created by IntelliJ IDEA.
 *  Date: 09.10.12 11:34
 *  To change this template use File | Settings | File Templates.
 */
object NullLogger extends Logger {
    def info(msg: String) = {}
}

package whisk

import java.io.PrintStream

/** Created by IntelliJ IDEA.
 *  User: vlmiroshnikov
 *  Date: 28.09.12 1:16
 *  To change this template use File | Settings | File Templates.
 */
object InteractiveConsole {
    val out = Console.out

    def process[T](prod: () => Seq[T], header: (PrintStream) => Unit, itemPrinter: (PrintStream, Int, T) => Unit, columnHeaderPrinter: (PrintStream) => Unit) = {
        val seq: Seq[T] = prod()
        val coll: Seq[Seq[(Int, T)]] = seq.zipWithIndex.map(p => p.swap).grouped(10).toSeq

        var position = 0

        if (coll.size > 1) {
            var ch = 'q'
            out.println("\n(N)ext, (P)rev, (F)irst, (L)ast, (Q)uit")
            header(out)
            print10(coll, position, itemPrinter, columnHeaderPrinter)
            do {
                try {
                    ch = Console.readChar()
                }
                catch {
                    case e: IndexOutOfBoundsException => {}
                }
                ch.toUpper match {
                    case 'N' => {
                        position = printNextFrame(position, p => coll.size > p && p >= 0, _ + 1,
                            (frame) => print10(coll, frame, itemPrinter, columnHeaderPrinter))
                    }
                    case 'P' => {
                        position = printNextFrame(position, p => coll.size > p && p >= 0, _ - 1,
                            (frame) => print10(coll, frame, itemPrinter, columnHeaderPrinter))
                    }
                    case 'L' => {
                        position = coll.size - 1
                        print10(coll, position, itemPrinter, columnHeaderPrinter)
                    }
                    case 'F' => {
                        position = 0
                        print10(coll, position, itemPrinter, columnHeaderPrinter)
                    }
                    case _ => {}
                }
            } while (ch != 'q')
        }
        else {
            header(out)
            print10(coll, position, itemPrinter, columnHeaderPrinter)
        }
    }

    private def printNextFrame(current: Int, cond: (Int) => Boolean, f: (Int) => Int, printer: (Int) => Unit): Int = {
        val nextVal = f(current)
        if (cond(nextVal)) {
            printer(nextVal)
            return nextVal
        }
        return current
    }

    private def print10[T](coll: Seq[Seq[(Int, T)]], frame: Int, f: (PrintStream, Int, T) => Unit, printHeader: (PrintStream) => Unit) = {
        if (coll.size > frame && frame >= 0) {
            out.println("Part %s  of %s \n".format(frame + 1, coll.size))
            printHeader(out)
            coll(frame).foreach(m => f(out, m._1, m._2))
        }

    }

}

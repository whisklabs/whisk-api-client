package whisk

import java.io.{PrintStream}

trait Formatter[T] {
  def formatItem(out: PrintStream, data: T)
}

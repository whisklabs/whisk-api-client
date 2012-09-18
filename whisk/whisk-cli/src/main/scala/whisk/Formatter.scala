package whisk

trait Formatter[T] {
  def formatItem(data: T): String
}

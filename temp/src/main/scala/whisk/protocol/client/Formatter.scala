package main.scala.whisk.protocol.client

trait Formatter[T] {
    def formatItem(data: T): String
}

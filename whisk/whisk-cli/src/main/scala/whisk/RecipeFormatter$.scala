package whisk

import protocol.identity.LoginResponse
import protocol.identity.LoginResponse
import protocol.recipes.Recipe
import protocol.recipes.RecipeAuthor
import protocol.recipes.RecipeQueryResponse
import protocol.recipes.RecipeResponse
import protocol.shoppinglist.AddToShoppingListResponse
import protocol.shoppinglist.ShoppingList
import protocol.shoppinglist.ShoppingListOptionsResponse
import protocol.shoppinglist.ShoppingListTitle
import protocol.shoppinglist.{ ShoppingListTitle, ShoppingList, AddToShoppingListResponse, ShoppingListOptionsResponse }
import protocol.recipes._
import scala.Some
import java.io.PrintStream
import scala.Some

object LoginFormatter extends Formatter[LoginResponse] {
    def formatItem(out: PrintStream, data: LoginResponse) = {
        out.println(data.header.status)
    }
}

object ShoppingListOptions extends Formatter[ShoppingListOptionsResponse] {
    def formatItem(out: PrintStream, data: ShoppingListOptionsResponse) = {
        val list: Seq[ShoppingListTitle] = data.shoppingLists
        list.foreach(x => out.println("%-20s %-20s\n".format(x.name.getOrElse(""), x.store)))
    }
}

object AddToShoppingListFormatter extends Formatter[AddToShoppingListResponse] {
    def formatItem(out: PrintStream, data: AddToShoppingListResponse) = {
        val list: ShoppingList = data.shoppingList
        list.recipes.foreach(x => out.println("%-20s %-20s %-20s\n".format(x.name, x.servings, x.url)))
    }
}

object RecipeResponseFormatter extends Formatter[RecipeResponse] {
    def formatItem(out: PrintStream, data: RecipeResponse) = {
        data.recipe match {
            case Some(r) => RecipeFormatter.formatItem(out, r)
            case None    => out.println("empty data")
        }
    }
}

object RecipeQueryResponseFormatter extends Formatter[Option[RecipeQueryResponse]] {
    def formatItem(out: PrintStream, data: Option[RecipeQueryResponse]) = {
        data match {
            case Some(m) => {
                m.data.get.recipes.map(c => RecipeFormatter.formatItem(out, c))
            }
            case None => out.println("empty data")
        }
    }
}

object RecipeFormatter extends Formatter[Recipe] {
    def formatItem(out: PrintStream, d: Recipe) = {
        out.printf("%-20s %-15s %-15s %-30s %-30s\n",
            d.title.take(20),
            d.author.getOrElse(RecipeAuthor("")).name.take(15),
            d.site.name.take(15),
            d.suggestedPrice.find(p => p.store == "Tesco") match {
                case Some(p) => "%1$.2f / %2$.2f / %3$.2f".format(p.priceSet.supermarketCost, p.priceSet.cost, p.priceSet.costPerServing)
                case None    => ""
            },

            d.suggestedPrice.find(p => p.store == "Waitrose") match {
                case Some(p) => "%1$.2f / %2$.2f / %3$.2f".format(p.priceSet.supermarketCost, p.priceSet.cost, p.priceSet.costPerServing)
                case None    => ""
            })
    }

}

object RecipeFormatterExt extends Formatter[(Int, Recipe)] {
    def formatItem(out: PrintStream, d: (Int, Recipe)) = {
        out.printf("%-5s %-20s %-15s %-15s %-30s %-30s\n",
            d._1.toString,
            d._2.title.take(20),
            d._2.author.getOrElse(RecipeAuthor("")).name.take(15),
            d._2.site.name.take(15),
            d._2.suggestedPrice.find(p => p.store == "Tesco") match {
                case Some(p) => "%1$.2f / %2$.2f / %3$.2f".format(p.priceSet.supermarketCost, p.priceSet.cost, p.priceSet.costPerServing)
                case None    => ""
            },

            d._2.suggestedPrice.find(p => p.store == "Waitrose") match {
                case Some(p) => "%1$.2f / %2$.2f / %3$.2f".format(p.priceSet.supermarketCost, p.priceSet.cost, p.priceSet.costPerServing)
                case None    => ""
            })
    }
}

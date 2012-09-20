package whisk

import protocol.identity.LoginResponse
import protocol.shoppinglist.{ShoppingListTitle, ShoppingList, AddToShoppingListResponse, ShoppingListOptionsResponse}
import protocol.recipes.{RecipeQueryResponse, RecipeResponse, RecipeAuthor, Recipe}
import scala.Some
import java.io.PrintStream

object LoginFormatter extends Formatter[LoginResponse]
{
    def formatItem(out: PrintStream, data: LoginResponse)  = {
        out.println(data.header.status)
    }
}

object ShoppingListOptions extends Formatter[ShoppingListOptionsResponse]
{
    def formatItem(out: PrintStream, data: ShoppingListOptionsResponse) = {
        val list: Seq[ShoppingListTitle] = data.shoppingLists
        list.foreach(x => out.println("%20s %20s".format(x.name.getOrElse(""), x.store)))
    }
}

object AddToShoppingListFormatter extends Formatter[AddToShoppingListResponse]
{
    def formatItem(out: PrintStream,data: AddToShoppingListResponse) = {
        val list: ShoppingList = data.shoppingList
        list.recipes.foreach(x => out.println("%20s %20s %20s".format(x.name, x.servings, x.url)))
    }
}

object RecipeResponseFormatter extends Formatter[RecipeResponse]
{
    def formatItem(out: PrintStream, data: RecipeResponse) = {
        data.recipe match {
            case Some(r) => RecipeFormatter.formatItem(out, r)
            case None  =>  out.println("empty data")
        }
    }
}

object RecipeQueryResponseFormatter extends Formatter[Option[RecipeQueryResponse]] {
    def formatItem(out: PrintStream, data: Option[RecipeQueryResponse]) = {
        data  match {
            case Some(m) =>{
                m.data.get.recipes.map(c => RecipeFormatter.formatItem(out , c))
            }
            case None  =>  out.println("empty data")
        }
    }
}


object RecipeFormatter extends Formatter[Recipe] {
  def formatItem(out: PrintStream, d: Recipe) = {
    out.format("%20s%10s%15s%15s%30s%30s",
      d.title.take(20),
      d.ingredients.length.toString,
      d.author.getOrElse(RecipeAuthor("")).name.take(15),
      d.site.name.take(15),
      d.suggestedPrice.find(isTesco) match {
        case Some((_, z)) => String.format("%1$.2f / %2$.2f / %3$.2f", (z.supermarketCost, z.cost, z.costPerServing))
        case None => ""
      },

      d.suggestedPrice.find(isWaitrose) match {
        case Some((_, z)) => String.format("%1$.2f / %2$.2f / %3$.2f", (z.supermarketCost, z.cost, z.costPerServing))
        case None => ""
      })
  }

  private def isTesco(p: (String, _)) = p match {
    case ("tesco", _) => true
    case _ => false;
  }

  private def isWaitrose(p: (String, _)) = p match {
    case ("waitrose", _) => true
    case _ => false;
  }
}

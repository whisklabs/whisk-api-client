package main.scala.whisk.protocol.client

import whisk.protocol.recipes.{RecipeAuthor, Recipe}
import scala.Some
import scala.Some

object RecipeFormatter extends Formatter[Recipe] {
    def formatItem(d: Recipe): String = {
        String.format("%20s%10s%15s%15s%30s%30s",
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

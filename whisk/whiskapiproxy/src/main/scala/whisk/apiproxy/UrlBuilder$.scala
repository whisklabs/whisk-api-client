package whisk.apiproxy

import whisk.protocol.recipes.RecipeQueryRequest

object UrlBuilder {
  def getRecipesQueryUrl(r: RecipeQueryRequest): String = {
    val getArgs = r.params.map {
      case (k, s) => (k, s.head)
    }
      .map({
      case (k, v) => "%s=%s".format(k, v)
    })
      .reduceLeft({
      (acc, s) => acc + "&" + s
    })

    return buildGetUrl("recipes/query", getArgs)
  }

  private def buildGetUrl(queryType: String, params: String) = {
    "http://test-apiadmin.whisk.co.uk/api/" + queryType + "?" + params;
  }
}

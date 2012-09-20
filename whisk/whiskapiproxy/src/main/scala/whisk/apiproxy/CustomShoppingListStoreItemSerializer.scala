package whisk.apiproxy

import net.liftweb.json.{TypeInfo, Formats, Serializer}
import net.liftweb.json.Extraction.decompose
import whisk.protocol.shoppinglist.ShoppingListStoreItem

class CustomShoppingListStoreItemSerializer extends Serializer[ShoppingListStoreItem] {
    private val IntervalClass = classOf[ShoppingListStoreItem]

    def deserialize(implicit format: Formats) = {
        case (TypeInfo(IntervalClass, _), json) => {
            val s = json.extract[ShoppingListStoreItemSerializable]
            ShoppingListStoreItem(s.storeItemId, s.name, s.amount, "", s.storeDepartment, s.priceSet, s.imageUrl, s.breadcrumb, s.unitPrice)
        }
    }

    def serialize(implicit format: Formats) = {
        case s: ShoppingListStoreItem => {
            val m = ShoppingListStoreItemSerializable(s.storeItemId, s.name, s.amount, s.storeDepartment, s.priceSet, s.imageUrl, s.breadcrumb, s.unitPrice)
            decompose(m)
        }
    }
}

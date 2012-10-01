package whisk.apiproxy

import whisk.protocol.store.StoreDepartment
import whisk.protocol.shoppinglist.StoreItemPriceSet

case class ShoppingListStoreItemSerializable(storeItemId: String,
                                             name: String,
                                             amount: Double,
                                             storeDepartment: StoreDepartment,
                                             priceSet: StoreItemPriceSet,
                                             imageUrl: String,
                                             breadcrumb: Option[String],
                                             unitPrice: Option[String])

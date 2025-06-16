package com.example.shared.networking.network.packing.model.packingList

data class PackingModelItem(
    val id: String,
    val customerId: String,
    val orderId: String,
    val packingListDate: String,
    val customerAddressCompany1: String,
    val customerAddressCompany2: String,
    val customerAddressStreet: String,
    val customerAddressZip: String,
    val customerAddressCity: String,
    val customerAddressCountryCode: String,
    val customerAddressContactPerson: String,
    val deliveryAddressCompany1: String,
    val deliveryAddressCompany2: String,
    val deliveryAddressStreet: String,
    val deliveryAddressZip: String,
    val deliveryAddressCity: String,
    val deliveryAddressCountryCode: String,
    val deliveryAddressContactPerson: String,
    val deliveryAddressUseCustomerAddress: Boolean,
    val invoiceAddressCompany1: String,
    val invoiceAddressCompany2: String,
    val invoiceAddressStreet: String,
    val invoiceAddressZip: String,
    val invoiceAddressCity: String,
    val invoiceAddressCountryCode: String,
    val invoiceAddressContactPerson: String,
    val invoiceAddressGln: String,
    val invoiceAddressUseCustomerAddress: Boolean,
    val customerAddressGln: String,
    val deliveryAddressGln: String,
    val salesmanName: String,
    val deliveryDate: String,
    val targetDeliveryDate: String,
    val targetDeliveryDateTypeCode: String,
    val targetDeliveryDateTypeText: String,
    val customerAddressSupplement: String,
    val deliveryAddressSupplement: String,
    val invoiceAddressSupplement: String,
    val packingListReplacementId: String,
    val packingListCreatedBy: String,
    val packingListChangedBy: String,
    val packingListCreatedTimestamp: String,
    val packingListChangedTimestamp: String,
    val status: String,
    val replacedBy: String,
    val replacedFor: String,
    val shippingContainers: List<ShippingContainer>,
    val nveRemarks: String,
    val emailRecipients: List<EmailRecipient>,
    val isHornBachCustomer: Boolean,
    val isLabelPrintable: Boolean,
    val assignedUserId: String,
    val assignedUserName: String,
    val packingListGroupCode: String?,
    val packingListGroupName: String?,
    val priority: Int,
    val connectedPackingLists: List<ConnectedPackingList>,
    val totalWeight: Double?
)

data class ShippingContainer(
    val shippingContainerPackingListItems: List<ShippingContainerPackingListItem>,
    val id: String?,
    val sscc: String?,
    val shippingContainerTypeCode: Int?,
    val width: Float?,
    val height: Float?,
    val depth: Float?,
    val weight: Float?,
    val manualReferenceNumber: String?,
    val index: Int?,
    val netWeight: Float?,
    val grossWeight: Float?
)

data class ShippingContainerPackingListItem(
    val shippingContainerId: String,
    val packingListId: String,
    val packingListItemPosition: String,
    val packedAmount: Int,
    val paI_Lgid: String
)

data class EmailRecipient(
    val emailRecipientText: String,
    val emailAddress: String,
    val isDefault: Boolean,
    val displayString: String
)

data class ConnectedPackingList(
    val id: String,
    val items: List<ConnectedPackingListItem>
)

data class ConnectedPackingListItem(
    val packingListId: String,
    val position: String,
    val orderItemLgid: String,
    val orderItemOrderId: String,
    val orderItemPosition: String,
    val typeCode: String,
    val articleId: String,
    val description: String,
    val unitCode: String,
    val amount: Int,
    val packedAmount: Int,
    val packedStatus: String,
    val gtin: String,
    val iposition: Int,
    val lgid: String,
    val shippingContainers: List<ShippingContainerPackingListItem>
)




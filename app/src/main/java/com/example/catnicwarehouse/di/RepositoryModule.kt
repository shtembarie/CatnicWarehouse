package com.example.catnicwarehouse.di

import com.example.catnicwarehouse.CorrectingStock.AmountFragment.data.repository.GetArticleUnitRepositoryImpl
import com.example.catnicwarehouse.CorrectingStock.AmountFragment.domain.repository.GetArticleUnitRepository
import com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.data.repository.CorrectInventoryRepositoryImpl
import com.example.catnicwarehouse.CorrectingStock.MatchFoundArticle.domain.repository.CorrectInventoryRepository
import com.example.catnicwarehouse.CorrectingStock.articles.data.repository.GetWarehouseStockYardsArticleRepositoryImpl
import com.example.catnicwarehouse.CorrectingStock.articles.domain.repository.GetWarehouseStockYardsArticleRepository
import com.example.catnicwarehouse.CorrectingStock.stockyards.data.repository.GetWarehouseStockYardRepositoryImpl
import com.example.catnicwarehouse.dashboard.data.repository.DashboardRepositoryImpl
import com.example.catnicwarehouse.dashboard.domain.repository.DashboardRepository
import com.example.catnicwarehouse.CorrectingStock.stockyards.domain.repository.GetWarehouseStockYardRepository
import com.example.catnicwarehouse.Inventory.AddArticle.data.InventoryAddArticleRepositoryImpl
import com.example.catnicwarehouse.Inventory.AddArticle.domain.repository.InventoryAddArticleRepository
import com.example.catnicwarehouse.Inventory.stockyards.data.repository.GetInventoryByIdRepositoryImpl
import com.example.catnicwarehouse.inventoryNew.articles.data.repository.InventoryItemRepositoryImpl
import com.example.catnicwarehouse.Inventory.matchFoundStockYard.data.repository.GetInventoryKPIRepositoryImpl
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.data.repository.MatchFoundInventoryRepositoryImpl
import com.example.catnicwarehouse.Inventory.stockyards.domain.repository.GetInventoryByIdRepository
import com.example.catnicwarehouse.inventoryNew.articles.domain.repository.InventoryItemRepository
import com.example.catnicwarehouse.Inventory.matchFoundStockYard.domain.repository.GetInventoryKPIRepository
import com.example.catnicwarehouse.Inventory.stockyardMatchFound.domain.repository.MatchFoundInventoryRepository
import com.example.catnicwarehouse.defectiveItems.amountFragment.data.repository.UpdateAmountRepositoryImpl
import com.example.catnicwarehouse.defectiveItems.amountFragment.domain.repository.UpdateAmountRepository
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.data.repository.GetDefectiveArticleByIdRepositoryImpl
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.data.repository.PostDefectiveItemsRepositoryImpl
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.repository.GetDefectiveArticleByIdRepository
import com.example.catnicwarehouse.defectiveItems.matchFoundFragment.domain.repository.PostDefectiveItemsRepository
import com.example.catnicwarehouse.defectiveItems.stockyards.data.repository.GetDefectiveArticleRepositoryImpl
import com.example.catnicwarehouse.defectiveItems.stockyards.domain.repository.GetDefectiveArticleRepository
import com.example.catnicwarehouse.incoming.amountItem.data.repository.AmountItemRepositoryImpl
import com.example.catnicwarehouse.incoming.amountItem.domain.repository.AmountItemRepository
import com.example.catnicwarehouse.incoming.articles.data.repository.ArticleRepositoryImpl
import com.example.catnicwarehouse.incoming.articles.domain.repository.ArticleRepository
import com.example.catnicwarehouse.incoming.comment.data.repository.CommentRepositoryImpl
import com.example.catnicwarehouse.incoming.comment.domain.repository.CommentRepository
import com.example.catnicwarehouse.incoming.deliveryType.data.repository.DeliveryTypeRepositoryImpl
import com.example.catnicwarehouse.incoming.deliveryType.domain.repository.DeliveryTypeRepository
import com.example.catnicwarehouse.incoming.deliveries.data.repository.DeliveryRepositoryImpl
import com.example.catnicwarehouse.incoming.deliveries.domain.repository.DeliveryRepository
import com.example.catnicwarehouse.incoming.deliveryDetail.data.repository.DeliveryDetailsRepositoryImpl
import com.example.catnicwarehouse.incoming.deliveryDetail.domain.repository.DeliveryDetailsRepository
import com.example.catnicwarehouse.incoming.inventoryItems.data.domain.repository.InventoryRepository
import com.example.catnicwarehouse.incoming.inventoryItems.data.data.InventoryRepositoryImpl
import com.example.catnicwarehouse.incoming.matchFound.data.repository.MatchFoundRepositoryImpl
import com.example.catnicwarehouse.incoming.matchFound.domain.repository.MatchFoundRepository
import com.example.catnicwarehouse.incoming.unloadingStockyards.data.repository.UnloadingStockyardsRepositoryImpl
import com.example.catnicwarehouse.incoming.unloadingStockyards.domain.repository.UnloadingStockyardsRepository
import com.example.catnicwarehouse.incoming.suppliers.data.repository.SearchVendorsRepositoryImpl
import com.example.catnicwarehouse.incoming.suppliers.domain.repository.SearchVendorsRepository
import com.example.catnicwarehouse.inventoryNew.comment.data.repository.InventoryCommentRepositoryImpl
import com.example.catnicwarehouse.inventoryNew.comment.domain.repository.InventoryCommentRepository
import com.example.catnicwarehouse.inventoryNew.matchFound.data.repository.InventoryMatchFoundRepositoryImpl
import com.example.catnicwarehouse.inventoryNew.matchFound.domain.repository.InventoryMatchFoundRepository
import com.example.catnicwarehouse.inventoryNew.stockyards.data.respository.GetCurrentInventoryRepositoryImpl
import com.example.catnicwarehouse.inventoryNew.stockyards.domain.repository.GetCurrentInventoryRepository
import com.example.catnicwarehouse.login.data.repository.LoginRepositoryImpl
import com.example.catnicwarehouse.login.domain.repository.LoginRepository
import com.example.catnicwarehouse.movement.articles.data.repository.StockyardArticlesRepositoryImpl
import com.example.catnicwarehouse.movement.articles.domain.repository.StockyardArticlesRepository
import com.example.catnicwarehouse.movement.movementList.data.repository.MovementListRepositoryImpl
import com.example.catnicwarehouse.movement.movementList.domain.repository.MovementListRepository
import com.example.catnicwarehouse.movement.stockyards.data.repository.StockyardsRepositoryImpl
import com.example.catnicwarehouse.movement.stockyards.domain.repository.StockyardsRepository
import com.example.catnicwarehouse.movement.summary.data.repository.MovementSummaryRepositoryImpl
import com.example.catnicwarehouse.movement.summary.domain.repository.MovementSummaryRepository
import com.example.catnicwarehouse.packing.addPackingItems.data.repository.AddPackingItemsRepositoryImpl
import com.example.catnicwarehouse.packing.addPackingItems.domain.repository.AddPackingItemsRepository
import com.example.catnicwarehouse.packing.finalisePackingList.data.repository.FinalisePackingListRepositoryImpl
import com.example.catnicwarehouse.packing.finalisePackingList.domain.repository.FinalisePackingListRepository
import com.example.catnicwarehouse.packing.matchFound.data.repository.PackingArticlesRepositoryImpl
import com.example.catnicwarehouse.packing.matchFound.domain.repository.PackingArticlesRepository
import com.example.catnicwarehouse.packing.packingItem.domain.repository.PackingItemsRepository
import com.example.catnicwarehouse.packing.packingList.data.repository.PackingListRepositoryImpl
import com.example.catnicwarehouse.packing.packingList.domain.repository.PackingListRepository
import com.example.catnicwarehouse.packing.shippingContainer.data.repository.ShippingContainerRepositoryImpl
import com.example.catnicwarehouse.packing.shippingContainer.domain.repository.ShippingContainerRepository
import com.example.catnicwarehouse.scan.data.repository.ManualInputRepositoryImpl
import com.example.catnicwarehouse.scan.domain.repository.ManualInputRepository
import com.example.shared.di.LoginAppApiService
import com.example.shared.di.WarehouseAppApiService
import com.example.shared.local.dataStore.DataStoreManager
import com.example.shared.networking.services.login.LoginApiService
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.catnicwarehouse.packing.packingItem.data.repository.PackingItemsRepositoryImpl as PackingItemsRepositoryImpl

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Singleton
    @Provides
    fun providesLoginRepository(
        @LoginAppApiService apiService: LoginApiService, dataStoreManager: DataStoreManager
    ): LoginRepository {
        return LoginRepositoryImpl(
            loginApiService = apiService, dataStoreManager = dataStoreManager
        )
    }

    @Singleton
    @Provides
    fun providesDeliveryRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): DeliveryRepository {
        return DeliveryRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesSearchVendorsRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): SearchVendorsRepository {
        return SearchVendorsRepositoryImpl(apiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesDeliveryTypeRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): DeliveryTypeRepository {
        return DeliveryTypeRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesUnloadingStockyardsRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): UnloadingStockyardsRepository {
        return UnloadingStockyardsRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesManualInputRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): ManualInputRepository {
        return ManualInputRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesMatchFoundRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): MatchFoundRepository {
        return MatchFoundRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }


    @Singleton
    @Provides
    fun providesCommentRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): CommentRepository {
        return CommentRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesDeliveryDetailsRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): DeliveryDetailsRepository {
        return DeliveryDetailsRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesAmountItemRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): AmountItemRepository {
        return AmountItemRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesArticleRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): ArticleRepository {
        return ArticleRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesDashboardRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): DashboardRepository {
        return DashboardRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun inventoryResponseAllItems(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): InventoryRepository {
        return InventoryRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun inventoryAddArticle(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): InventoryAddArticleRepository {
        return InventoryAddArticleRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun getInventoryByIdResponse(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): GetInventoryByIdRepository {
        return GetInventoryByIdRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun getInventoryKPIResponse(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): GetInventoryKPIRepository {
        return GetInventoryKPIRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun getInventoryItemsResponse(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): InventoryItemRepository {
        return InventoryItemRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesStockyardRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): StockyardsRepository {
        return StockyardsRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesStockyardInventoryRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): StockyardArticlesRepository {
        return StockyardArticlesRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesPackingArticlesRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): PackingArticlesRepository {
        return PackingArticlesRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesMatchFoundInventoryRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): MatchFoundInventoryRepository {
        return MatchFoundInventoryRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesMovementsMatchFoundRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): com.example.catnicwarehouse.movement.matchFound.domain.repository.MatchFoundRepository {
        return com.example.catnicwarehouse.movement.matchFound.data.repository.MatchFoundRepositoryImpl(
            warehouseApiServices = warehouseApiServices
        )
    }

    @Singleton
    @Provides
    fun providesMovementSummaryRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): MovementSummaryRepository {
        return MovementSummaryRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesMovementListRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): MovementListRepository {
        return MovementListRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesPackingListRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): PackingListRepository {
        return PackingListRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesPackingItemsRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): PackingItemsRepository {
        return PackingItemsRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }


    @Singleton
    @Provides
    fun providesFinalisePackingRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): FinalisePackingListRepository {
        return FinalisePackingListRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesWarehouseStockYardList(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): GetWarehouseStockYardRepository {
        return GetWarehouseStockYardRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }
    @Singleton
    @Provides
    fun providesWarehouseStockYardsArticleList(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): GetWarehouseStockYardsArticleRepository {
        return GetWarehouseStockYardsArticleRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }
    @Singleton
    @Provides
    fun providesCorrectedRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): CorrectInventoryRepository {
        return CorrectInventoryRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }
    @Singleton
    @Provides
    fun provideArticleUnits(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): GetArticleUnitRepository {
        return GetArticleUnitRepositoryImpl (warehouseApiServices = warehouseApiServices)
    }
    @Singleton
    @Provides
    fun provideDefectiveArticlesUnit(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): GetDefectiveArticleRepository {
        return GetDefectiveArticleRepositoryImpl (warehouseApiServices = warehouseApiServices)
    }
    @Singleton
    @Provides
    fun provideDefectiveArticlesByIdUnit(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): GetDefectiveArticleByIdRepository {
        return GetDefectiveArticleByIdRepositoryImpl (warehouseApiServices = warehouseApiServices)
    }
    @Singleton
    @Provides
    fun provideUpdatetAmount(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): UpdateAmountRepository {
        return UpdateAmountRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesPostingArticleRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): PostDefectiveItemsRepository {
        return PostDefectiveItemsRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesAddPackingItemsRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): AddPackingItemsRepository {
        return AddPackingItemsRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }


    @Singleton
    @Provides
    fun providesShippingContainersRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): ShippingContainerRepository {
        return ShippingContainerRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }


    @Singleton
    @Provides
    fun providesCurrentInventoryRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): GetCurrentInventoryRepository {
        return GetCurrentInventoryRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesInventoryCommentRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): InventoryCommentRepository {
        return InventoryCommentRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }

    @Singleton
    @Provides
    fun providesInventoryMatchFoundRepository(@WarehouseAppApiService warehouseApiServices: WarehouseApiServices): InventoryMatchFoundRepository {
        return InventoryMatchFoundRepositoryImpl(warehouseApiServices = warehouseApiServices)
    }



}

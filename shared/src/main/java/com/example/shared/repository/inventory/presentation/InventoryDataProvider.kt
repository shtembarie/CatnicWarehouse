package com.example.shared.repository.inventory.presentation

/**
 * Created by Enoklit on 30.09.2024.
 */
interface InventoryDataProvider {
    fun saveActualUnitCode(actualUnitCode: String?): String?
    fun saveTargetUnitCode(targetUnitCode: String?): String?
    fun saveUpdatedActualUnitCode(itemId: Int?, unitCode: String?): String?
    fun saveItemAmount(amount: Int?): Int?
    fun saveUpdatedItemAmount(itemId: Int?, amount: Int?): Int?
    fun saveClickedArticleComment(comment: String?): String?
    fun saveUpdatedComment(itemId: Int?, comment: String?): String?

}
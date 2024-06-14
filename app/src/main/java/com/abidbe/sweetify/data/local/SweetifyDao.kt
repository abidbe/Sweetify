package com.abidbe.sweetify.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface SweetifyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDrink(drink: Drink)

    @Delete
    suspend fun deleteDrink(drink: Drink)

    @Query("SELECT * FROM drink WHERE userId = :userId AND SUBSTR(purchaseDate, 1, 10) = :purchaseDate")
    fun getDailyHistory(userId: String, purchaseDate: String): Flow<List<Drink>>

    @Query("SELECT SUM(sugarAmountBased) FROM drink WHERE userId = :userId AND SUBSTR(purchaseDate, 1, 10) = :purchaseDate")
    fun getTotalSugarAmount(userId: String, purchaseDate: String): Flow<Double?>

    @Query("SELECT SUBSTR(purchaseDate, 1, 10) AS date, SUM(sugarAmountBased) AS totalSugarAmount FROM drink WHERE userId = :userId AND SUBSTR(purchaseDate, 1, 10) BETWEEN :startDate AND :endDate GROUP BY date ORDER BY date")
    fun getDailySugarAmounts(userId: String, startDate: String, endDate: String): Flow<List<WeeklySugarAmount>>
}
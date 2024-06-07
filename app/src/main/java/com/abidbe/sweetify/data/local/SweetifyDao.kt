package com.abidbe.sweetify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface SweetifyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDrink(drink: Drink)

    @Query("SELECT * FROM drink WHERE userId = :userId")
    suspend fun getDrinksByUserId(userId: Int): List<Drink>


    @Query("SELECT * FROM drink WHERE userId = :userId AND SUBSTR(purchaseDate, 1, 10) = :purchaseDate")
    fun getDailyHistory(userId: Int, purchaseDate: String): Flow<List<Drink>>

    // Query to get the userId
    @Query("SELECT uid FROM user LIMIT 1") // Assuming 'uid' is the column name for userId
    fun getUserId(): Flow<Int?> // Assuming userId is of type Int

    // Query to sum sugarAmountBased for a given userId and purchaseDate
    @Query("SELECT SUM(sugarAmountBased) FROM drink WHERE userId = :userId AND SUBSTR(purchaseDate, 1, 10) = :purchaseDate")
    fun getTotalSugarAmount(userId: Int, purchaseDate: String): Flow<Double?>

}
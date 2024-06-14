package com.abidbe.sweetify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "drink")
data class Drink(
    @PrimaryKey(autoGenerate = true)
    val drinkId: Int? = null,
    val userId: String? = null,
    val name: String,
    val grade: String,
    val sugarAmountBased: Double,
    val purchaseDate: String
)


data class WeeklySugarAmount(
    val date: String,
    val totalSugarAmount: Double
)


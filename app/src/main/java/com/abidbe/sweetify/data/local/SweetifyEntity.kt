package com.abidbe.sweetify.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    val username: String
)

@Entity(tableName = "drink")
data class Drink(
    @PrimaryKey(autoGenerate = true) val drinkId: Int? = null,
    val userId: Int? = null,
    val name: String,
    val grade: String,
    val sugarAmountBased: Double,
    val purchaseDate: String
)

data class UserWithDrink(
    @Embedded val user: User,
    @Relation(
        parentColumn = "uid",
        entityColumn = "userId"
    )
    val drink: List<Drink>
)


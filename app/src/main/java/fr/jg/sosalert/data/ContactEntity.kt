package fr.jg.sosalert.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val phoneNumber: String
)
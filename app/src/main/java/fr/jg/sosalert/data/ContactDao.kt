package fr.jg.sosalert.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact ORDER BY name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contact WHERE name LIKE '%' || :searchQuery || '%' OR phoneNumber LIKE '%' || :searchQuery ||  '%' ORDER BY LOWER(name) ASC")
    fun getFilteredContacts(searchQuery: String): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)
}

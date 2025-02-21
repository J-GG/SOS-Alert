package fr.jg.sosalert.data

import fr.jg.sosalert.ui.view.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getAllContacts(): Flow<List<Contact>>

    fun getFilteredContacts(searchQuery: String): Flow<List<Contact>>

    suspend fun insertContact(contact: Contact)

    suspend fun deleteContact(contact: Contact)
}
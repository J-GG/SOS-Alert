package fr.jg.sosalert.data

import fr.jg.sosalert.ui.view.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ContactRepositoryImpl(private val contactDao: ContactDao) :
    ContactRepository {
    override fun getAllContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts()
            .map { contacts -> contacts.map { Contact(it) } }
    }

    override fun getFilteredContacts(searchQuery: String): Flow<List<Contact>> {
        return contactDao.getFilteredContacts(searchQuery)
            .map { contacts -> contacts.map { Contact(it) } }
    }

    override suspend fun insertContact(contact: Contact) {
        contactDao.insertContact(ContactEntity(contact.id, contact.name, contact.phoneNumber))
    }

    override suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(ContactEntity(contact.id, contact.name, contact.phoneNumber))
    }
}
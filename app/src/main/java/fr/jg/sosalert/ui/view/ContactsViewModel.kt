package fr.jg.sosalert.ui.view

import android.telephony.PhoneNumberUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.jg.sosalert.R
import fr.jg.sosalert.data.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactsViewModel(private val contactRepository: ContactRepository) : ViewModel() {

    private var contactSearch by mutableStateOf("")

    val allContacts: StateFlow<List<Contact>?> = contactRepository.getAllContacts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = null
        )

    private val _filteredContacts = MutableStateFlow<List<Contact>>(emptyList())
    val filteredContacts: StateFlow<List<Contact>> = _filteredContacts.asStateFlow()

    private val _contactToAdd = MutableStateFlow(AddContact(Field(""), Field("")))
    val contactToAdd: StateFlow<AddContact> = _contactToAdd.asStateFlow()

    init {
        updateFilteredContacts("")
    }

    fun updateContactSearch(search: String) {
        contactSearch = search
        updateFilteredContacts(search)
    }

    private fun updateFilteredContacts(searchQuery: String) {
        viewModelScope.launch {
            contactRepository.getFilteredContacts(searchQuery)
                .distinctUntilChanged()
                .collect { contacts ->
                    _filteredContacts.value = contacts
                }

        }
    }

    fun createContact() {
        viewModelScope.launch {
            contactRepository.insertContact(
                Contact(
                    0,
                    contactToAdd.value.name.value,
                    contactToAdd.value.phoneNumber.value
                )
            )
            _contactToAdd.update {
                AddContact(Field(""), Field(""))
            }
            updateFilteredContacts(contactSearch)
        }
    }

    fun importContact(name: String, phoneNumber: String) {
        viewModelScope.launch {
            contactRepository.insertContact(
                Contact(0, name, phoneNumber)
            )
            updateFilteredContacts(contactSearch)
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            contactRepository.deleteContact(contact)
            updateFilteredContacts(contactSearch)
        }
    }

    private fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(
                true,
                R.string.contacts_create_dialog_name_error_blank,
            )

            else -> ValidationResult(false, null)
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): ValidationResult {
        return when {
            phoneNumber.isBlank() -> ValidationResult(
                true,
                R.string.contacts_create_dialog_phone_number_error_blank,
            )

            !PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber) -> ValidationResult(
                true,
                R.string.contacts_create_dialog_phone_number_error_format,
            )

            else -> ValidationResult(false, null)
        }
    }

    fun updateNameContactToAdd(name: String) {
        _contactToAdd.update { currentContact ->
            val validation = validateName(name)
            currentContact.copy(
                name = Field(name, validation.error, validation.errorMessage),
                isValid = !validation.error && !validatePhoneNumber(currentContact.phoneNumber.value).error
            )
        }
    }

    fun updatePhoneNumberContactToAdd(phoneNumber: String) {
        _contactToAdd.update { currentContact ->
            val validation = validatePhoneNumber(phoneNumber)
            currentContact.copy(
                phoneNumber = Field(phoneNumber, validation.error, validation.errorMessage),
                isValid = !validation.error && !validateName(currentContact.name.value).error
            )
        }
    }
}

data class AddContact(
    val name: Field<String>,
    val phoneNumber: Field<String>,
    val isValid: Boolean = false
)

data class Field<T>(
    val value: T,
    val error: Boolean = false,
    val errorMessage: Int? = null,
)

data class ValidationResult(
    val error: Boolean,
    val errorMessage: Int?,
)
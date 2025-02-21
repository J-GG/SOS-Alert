package fr.jg.sosalert.ui.view

import androidx.compose.runtime.Immutable
import fr.jg.sosalert.data.ContactEntity
import java.io.Serializable

@Immutable
data class Contact(
    val id: Int,
    val name: String,
    val phoneNumber: String
) : Serializable {
    constructor(contactEntity: ContactEntity) : this(
        id = contactEntity.id,
        name = contactEntity.name,
        phoneNumber = contactEntity.phoneNumber
    )
}
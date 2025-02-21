package fr.jg.sosalert.ui.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import fr.jg.sosalert.R
import fr.jg.sosalert.ui.AppViewModelProvider
import fr.jg.sosalert.ui.navigation.NavigationDestination

object ContactsDestination : NavigationDestination {
    override val route = "contacts"
    override val topBarTitle = R.string.navigation_contacts_top_bar_title
    override val bottomBarTitle = R.string.navigation_contacts_bottom_bar_title
    override val bottomBarIcon = Icons.Default.AccountBox
}

@Composable
fun Contacts(
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val filteredContacts by viewModel.filteredContacts.collectAsState()
    val allContacts by viewModel.allContacts.collectAsState()
    var showCreateContactDialog by rememberSaveable { mutableStateOf(false) }

    val contactPickerLauncher = rememberContactPickerLauncher(context) { name, phoneNumber ->
        viewModel.importContact(name, phoneNumber)
    }

    Scaffold(
        floatingActionButton = {
            MultiFloatingActionButton(
                fabIcon = Icons.Default.Add,
                items = listOf(
                    FabItem(
                        icon = Icons.Default.Create,
                        label = stringResource(R.string.contacts_fab_create_account),
                        onFabItemClicked = { showCreateContactDialog = true }
                    ),
                    FabItem(
                        icon = Icons.Default.Add,
                        label = stringResource(R.string.contacts_fab_import_account),
                        onFabItemClicked = {
                            val intent = Intent(
                                Intent.ACTION_PICK,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            )
                            contactPickerLauncher.launch(intent)
                        }
                    ),
                )
            )
        }
    ) { innerPadding ->
        if (allContacts != null) {
            if (allContacts!!.isEmpty()) {
                EmptyContactList()
            } else {
                ContactListAndSearchBar(
                    onUpdateContactSearch = { viewModel.updateContactSearch(it) },
                    onDeleteContact = { viewModel.deleteContact(it) },
                    contacts = filteredContacts
                )
            }
        }
    }

    if (showCreateContactDialog) {
        CreateContactDialog(
            contactToAdd = viewModel.contactToAdd.collectAsState().value,
            onNameChange = viewModel::updateNameContactToAdd,
            onPhoneNumberChange = viewModel::updatePhoneNumberContactToAdd,
            onCreateConfirm = {
                showCreateContactDialog = false
                viewModel.createContact()
            },
            onCreateCancel = { showCreateContactDialog = false }
        )
    }
}

@Composable
private fun ContactListAndSearchBar(
    onUpdateContactSearch: (String) -> Unit,
    onDeleteContact: (Contact) -> Unit,
    contacts: List<Contact>
) {
    Column {
        ContactSearchBar(
            onTextChange = onUpdateContactSearch,
        )
        Spacer(modifier = Modifier.padding(4.dp))
        ContactList(contacts = contacts, onDeleteContact = onDeleteContact)
    }
}

@Composable
private fun EmptyContactList(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty_contact))
    val scrollState = rememberScrollState()
    val iconId = "inlineIcon"
    val text = buildAnnotatedString {
        append(stringResource(R.string.contacts_tap_to_add_beginning))
        appendInlineContent(
            iconId,
            "[" + stringResource(R.string.contacts_search_placeholder) + "]"
        )
        append(stringResource(R.string.contacts_tap_to_add_end))
    }

    val inlineContent = mapOf(
        iconId to InlineTextContent(
            placeholder = Placeholder(
                width = 20.sp,
                height = 20.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.contacts_search_placeholder),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { 1f },
            modifier = Modifier
                .size(200.dp)
        )
        Text(
            text = stringResource(R.string.contacts_empty),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier.height(24.dp))
        Text(
            text = text,
            inlineContent = inlineContent,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier.height(100.dp))
    }
}

@Composable
fun ContactSearchBar(onTextChange: (String) -> Unit) {
    var contactSearch by rememberSaveable { mutableStateOf("") }

    OutlinedTextField(
        value = contactSearch,
        onValueChange = {
            contactSearch = it
            onTextChange(contactSearch)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 0.dp),
        placeholder = { Text(stringResource(R.string.contacts_search_placeholder)) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.contacts_search_clear)
            )
        },
        trailingIcon = {
            if (contactSearch.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        contactSearch = ""
                        onTextChange(contactSearch)
                    }
                )
            }
        }
    )
}

@Composable
fun rememberContactPickerLauncher(
    context: Context,
    onContactPicked: (String, String) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleContactSelection(context, uri, onContactPicked)
            }
        }
    }
}

private fun handleContactSelection(
    context: Context,
    contactUri: Uri,
    onContactPicked: (String, String) -> Unit
) {
    val cursor = context.contentResolver.query(
        contactUri,
        arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        ),
        null, null, null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            val name = it.getString(nameIndex)
            val number = it.getString(numberIndex)

            onContactPicked(name, number)
        }
    }
}

@Composable
private fun CreateContactDialog(
    contactToAdd: AddContact,
    onNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onCreateConfirm: () -> Unit,
    onCreateCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var creationInProgress by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCreateCancel,
        title = { Text(stringResource(R.string.contacts_create_dialog_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = contactToAdd.name.value,
                    isError = contactToAdd.name.error,
                    onValueChange = {
                        onNameChange(it)
                    },
                    label = { Text(stringResource(R.string.contacts_create_dialog_name_label)) },
                    supportingText = {
                        contactToAdd.name.errorMessage?.let {
                            Text(
                                stringResource(it),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
                OutlinedTextField(
                    value = contactToAdd.phoneNumber.value,
                    isError = contactToAdd.phoneNumber.error,
                    onValueChange = {
                        onPhoneNumberChange(it)
                    },
                    label = { Text(stringResource(R.string.contacts_create_dialog_phone_number_label)) },
                    supportingText = {
                        contactToAdd.phoneNumber.errorMessage?.let {
                            Text(
                                stringResource(it),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    ),
                )
            }
        },
        modifier = modifier,
        confirmButton = {
            TextButton(enabled = contactToAdd.isValid, onClick = {
                if (!creationInProgress) {
                    creationInProgress = true
                    onCreateConfirm()
                }
            }) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onCreateCancel) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Composable
private fun ContactList(
    contacts: List<Contact>,
    onDeleteContact: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    var contactToDelete: Contact? by rememberSaveable { mutableStateOf(null) }

    LazyColumn(
        modifier = modifier,
    ) {
        items(contacts) { contact ->
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RectangleShape,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)
                    ) {
                        Text(text = contact.name, fontWeight = FontWeight.Bold)
                        Text(
                            text = contact.phoneNumber,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { contactToDelete = contact }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.contacts_delete_icon_description),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
            }
        }
    }

    if (contactToDelete != null) {
        DeleteContactDialog(
            contact = contactToDelete!!,
            onDeleteConfirm = { contact ->
                onDeleteContact(contact)
                contactToDelete = null
            },
            onDeleteCancel = { contactToDelete = null },
        )
    }
}

@Composable
private fun DeleteContactDialog(
    contact: Contact,
    onDeleteConfirm: (contact: Contact) -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDeleteCancel,
        title = { Text(stringResource(R.string.contacts_delete_dialog_title)) },
        text = {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.contacts_delete_dialog_text_beginning))
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(contact.name)
                    }
                    append(stringResource(R.string.contacts_delete_dialog_text_end))
                }

            )
        },
        modifier = modifier,
        confirmButton = {
            TextButton(onClick = { onDeleteConfirm(contact) }) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.repository.ChatSocketRepository
import com.dev_marinov.chatalyze.data.chatMessage.dto.OnlineUserState
import com.dev_marinov.chatalyze.domain.model.chat.ChatCompanion
import com.dev_marinov.chatalyze.domain.model.chats.Chat
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.ChatRepository
import com.dev_marinov.chatalyze.domain.repository.ChatsRepository
import com.dev_marinov.chatalyze.domain.repository.RoomRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.CombineChat
import com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.chats_screen.model.Contact
import com.dev_marinov.chatalyze.presentation.util.ConnectivityObserver
import com.dev_marinov.chatalyze.presentation.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val chatsRepository: ChatsRepository,
    private val roomRepository: RoomRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,

) : ViewModel() {

    val hideDialogPermissionNotificationFlow = preferencesDataStoreRepository.hideDialogPermissionNotificationFlow

    val getStateNotFoundRefreshToken = preferencesDataStoreRepository.getStateNotFoundRefreshToken
    val getFailureUpdatePairToken = preferencesDataStoreRepository.getFailureUpdatePairToken

    val getInternalServerError = preferencesDataStoreRepository.getInternalServerError

    private val onlineUserStateList = roomRepository.onlineUserStateList
    val filteredContacts = roomRepository.filteredContacts
    val isSessionState = preferencesDataStoreRepository.isSessionState

    val isGrantedPermissions = preferencesDataStoreRepository.isGrantedPermissions
    val isTheLifecycleEventNow = preferencesDataStoreRepository.isTheLifecycleEventNow
    val getOwnPhoneSender = preferencesDataStoreRepository.getOwnPhoneSender

    var ownPhoneSender = ""

    private var _isOpenModalBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isOpenModalBottomSheet: StateFlow<Boolean> = _isOpenModalBottomSheet

    private var _getChatListFlag: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val getChatListFlag: StateFlow<Boolean> = _getChatListFlag

    private var _combineChatList: MutableStateFlow<List<CombineChat>> =
        MutableStateFlow(emptyList())
    val combineChatList: StateFlow<List<CombineChat>> = _combineChatList

    private var _onlineUserStateList = MutableStateFlow(emptyList<OnlineUserState>())

    private var _chatList = MutableStateFlow(emptyList<Chat>())
    val chatList: StateFlow<List<Chat>> = _chatList

    private var _contacts: MutableStateFlow<List<Contact>> = MutableStateFlow(listOf())
    val contacts: StateFlow<List<Contact>> = _contacts

    init {
        saveLocalOwnPhoneSender()
    }

    fun createCombineFlow() {
        val combineChatListFlow: Flow<List<CombineChat>> =
            combine(_chatList, onlineUserStateList, _contacts) { chats, stateList, cont ->
                chats.map { chat ->
                    val onlineUserStateSender = getOnlineUserStateSender(
                        stateList = stateList,
                        chat = chat
                        )
                    val onlineUserStateRecipient = getOnlineUserStateRecipient(
                        stateList = stateList,
                        chat = chat
                    )
                    val onlineUserState = onlineUserStateSender ?: onlineUserStateRecipient

                    val senderName = getSenderName(cont = cont, chat.sender)
                    val recipientName = getRecipientName(cont = cont, chat.recipient)
                    val name = senderName ?: recipientName

                    CombineChat(
                        sender = chat.sender,
                        recipient = chat.recipient,
                        textMessage = chat.textMessage,
                        createdAt = chat.createdAt,
                        onlineOrOffline = onlineUserState,
                        name = name,
                        typeEvent = Constants.OUTGOING_CALL_EVENT
                    )
                }
            }

        viewModelScope.launch(Dispatchers.IO) {
            combineChatListFlow.collect {
                _combineChatList.value = it
            }
        }
    }

    private fun getSenderName(cont: List<Contact>, sender: String): String? {
        return cont.firstOrNull { it.phoneNumber == sender }?.name
    }

    private fun getRecipientName(cont: List<Contact>, recipient: String): String? {
        return cont.firstOrNull { it.phoneNumber == recipient }?.name
    }

    private fun getOnlineUserStateSender(stateList: List<OnlineUserState>, chat: Chat): String? {
        return stateList.firstOrNull { it.userPhone == chat.sender }?.onlineOrOffline
    }

    private fun getOnlineUserStateRecipient(stateList: List<OnlineUserState>, chat: Chat): String? {
        return stateList.firstOrNull { it.userPhone == chat.recipient }?.onlineOrOffline
    }

    fun createContactsFlow(contacts: List<Contact>) {
        _contacts.value = contacts
        saveContactsToDb(contacts = contacts)
    }

    private fun saveContactsToDb(contacts: List<Contact>) {
        val filteredContacts: MutableList<Contact> = mutableListOf()
        viewModelScope.launch(Dispatchers.Default) {
            contacts.forEach {
                if (it.phoneNumber.length == 10 && (it.phoneNumber.startsWith("9") || it.phoneNumber.startsWith("5"))) {
                    filteredContacts.add(it)
                }
            }
            withContext(Dispatchers.IO) {
                roomRepository.saveContacts(contacts = filteredContacts)
            }
        }
    }

    fun createChatListFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            val refreshTokenJob: Deferred<String> = async {
                authRepository.getRefreshTokensFromDataStore.first()
            }
            val response = chatsRepository.getChats(sender = ownPhoneSender, refreshToken = refreshTokenJob.await())
            _chatList.value = response
        }
    }

    fun onClickHideNavigationBar(isHide: Boolean) {
        viewModelScope.launch {
            preferencesDataStoreRepository.saveHideNavigationBar(
                Constants.HIDE_BOTTOM_BAR,
                isHide = isHide
            )
        }
    }

    fun openModalBottomSheet(isOpen: Boolean) {
        _isOpenModalBottomSheet.value = isOpen
    }

    private fun saveLocalOwnPhoneSender() {
        viewModelScope.launch(Dispatchers.IO) {
            getOwnPhoneSender.collect {
                ownPhoneSender = it
            }
        }
    }

    fun canGetChatList(can: Boolean) {
        _getChatListFlag.value = can
    }

    fun saveHideDialogPermissionNotification(hide: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.saveHideDialogPermissionNotification(hide = hide)
        }
    }

    fun saveCompanionOnTheServer(senderPhone: String, recipientPhone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val chatCompanion = ChatCompanion(
                senderPhone = senderPhone,
                companionPhone = recipientPhone
            )
            val response = chatRepository.saveCompanionOnTheServer(chatCompanion = chatCompanion)
        }
    }

    fun onExitFromApp(isExit: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataStoreRepository.onExitFromApp(isExit = isExit)
        }
    }
}
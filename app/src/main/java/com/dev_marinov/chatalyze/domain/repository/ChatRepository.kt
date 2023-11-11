package com.dev_marinov.chatalyze.domain.repository

import com.dev_marinov.chatalyze.domain.model.chat.ChatMessage
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.model.Contact

interface ChatRepository {
    suspend fun getChatMessage() : List<Contact>
}
package com.dev_marinov.chatalyze.presentation.ui.main_screens_activity.profile_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev_marinov.chatalyze.domain.model.auth.MessageResponse
import com.dev_marinov.chatalyze.domain.repository.AuthRepository
import com.dev_marinov.chatalyze.domain.repository.CallRepository
import com.dev_marinov.chatalyze.domain.repository.PreferencesDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import okhttp3.internal.http.HTTP_CONFLICT
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_OK
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesDataStoreRepository: PreferencesDataStoreRepository,
    private val callRepository: CallRepository
) : ViewModel() {

    private val refreshToken = authRepository.getRefreshTokensFromDataStore

    val ownPhoneSender = preferencesDataStoreRepository.getOwnPhoneSender

    private var _statusCode: MutableStateFlow<Int> = MutableStateFlow(0)
    val statusCode: StateFlow<Int> = _statusCode

    private var refreshTokenTemp = ""
    private var ownPhoneSenderTemp = ""

    init {
        writeRefreshTokenTemp()
        saveOwnPhoneInViewModel()
    }

    private fun saveOwnPhoneInViewModel() {
        viewModelScope.launch(Dispatchers.IO) {
            ownPhoneSender.collect{
                ownPhoneSenderTemp = it
            }
        }
    }

    fun executeLogout() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.logout(token = refreshTokenTemp, senderPhone = ownPhoneSenderTemp)
            response?.let {
                processTheResponse(response = response)
            }
        }
    }

    fun executeDeleteProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = authRepository.deleteProfile(token = refreshTokenTemp)
            response?.let {
                processTheResponse(response = response)
            }
        }
    }

    fun executeDeleteHistoryCalls() {
        viewModelScope.launch(Dispatchers.IO) {
            callRepository.deleteHistoryCalls()
        }
    }

    private fun writeRefreshTokenTemp() {
        viewModelScope.launch {
            refreshToken.collect {
                refreshTokenTemp = it
            }
        }
    }
    private suspend fun processTheResponse(response: MessageResponse) {
        when (response.httpStatusCode) {
            HTTP_OK -> {
                authRepository.deletePairTokensToDataStore()
                _statusCode.value = response.httpStatusCode
                delay(1000L)
                _statusCode.value = 0
            }
            HTTP_CONFLICT -> {
                _statusCode.value = response.httpStatusCode
                delay(1000L)
                _statusCode.value = 0
            }
            HTTP_INTERNAL_SERVER_ERROR -> {
                _statusCode.value = response.httpStatusCode
                delay(1000L)
                _statusCode.value = 0
            }
            HTTP_NOT_FOUND -> {
                authRepository.deletePairTokensToDataStore()
                _statusCode.value = response.httpStatusCode
                delay(1000L)
                _statusCode.value = 0
            }
        }
    }
}
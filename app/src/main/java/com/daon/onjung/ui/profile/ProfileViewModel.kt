package com.daon.onjung.ui.profile

import androidx.lifecycle.viewModelScope
import com.daon.onjung.Constants
import com.daon.onjung.data.repository.EventRepository
import com.daon.onjung.data.repository.OnjungRepository
import com.daon.onjung.network.adapter.ApiResult
import com.daon.onjung.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val onjungRepository: OnjungRepository,
    private val eventRepository: EventRepository
) : BaseViewModel<ProfileContract.State, ProfileContract.Event, ProfileContract.Effect>(
    initialState = ProfileContract.State()
) {
    init {
        getOnjungBrief()
        getOnjungCount()
        getTicketCount()
        getRemainCount()
    }

    override fun reduceState(event: ProfileContract.Event) { }

    private fun getOnjungBrief() = viewModelScope.launch {
        onjungRepository.getOnjungBrief().onStart {
            updateState(currentState.copy(isLoading = true))
        }.collect {
            updateState(currentState.copy(isLoading = false))
            when (it) {
                is ApiResult.Success -> {
                    it.data?.data?.let {
                        updateState(currentState.copy(onjungBrief = it))
                    }
                }
                is ApiResult.ApiError ->{
                    postEffect(ProfileContract.Effect.ShowSnackBar(it.message))
                }
                is ApiResult.NetworkError -> {
                    postEffect(ProfileContract.Effect.ShowSnackBar(Constants.NETWORK_ERROR_MESSAGE))
                }
            }
        }
    }

    private fun getOnjungCount() = viewModelScope.launch {
        onjungRepository.getOnjungCount().onStart {
            updateState(currentState.copy(isLoading = true))
        }.collect {
            updateState(currentState.copy(isLoading = false))
            when (it) {
                is ApiResult.Success -> {
                    it.data?.data?.let { result ->
                        updateState(currentState.copy(onjungCount = result.totalOnjungCount))
                    }
                }
                is ApiResult.ApiError -> {
                    postEffect(ProfileContract.Effect.ShowSnackBar(it.message))
                }
                is ApiResult.NetworkError -> {
                    postEffect(ProfileContract.Effect.ShowSnackBar(Constants.NETWORK_ERROR_MESSAGE))
                }
            }
        }
    }

    private fun getTicketCount() = viewModelScope.launch {
        eventRepository.getTicketCount().onStart {
            updateState(currentState.copy(isLoading = true))
        }.collect {
            updateState(currentState.copy(isLoading = false))
            when (it) {
                is ApiResult.Success -> {
                    it.data?.data?.let { result ->
                        updateState(currentState.copy(ticketCount = result.ticketCount))
                    }
                }
                is ApiResult.ApiError -> {
                    postEffect(ProfileContract.Effect.ShowSnackBar(it.message))
                }
                is ApiResult.NetworkError -> {
                    postEffect(ProfileContract.Effect.ShowSnackBar(Constants.NETWORK_ERROR_MESSAGE))
                }
            }
        }
    }

    private fun getRemainCount() = viewModelScope.launch {
        onjungRepository.getMyOnjungBriefs().onStart {
            updateState(currentState.copy(isLoading = true))
        }.collect {
            updateState(currentState.copy(isLoading = false))
            when (it) {
                is ApiResult.Success -> {
                    it.data?.data?.let { result ->
                        updateState(
                            currentState.copy(
                                storeList = result.storeNameList,
                                remainCount = result.remainCount
                            )
                        )
                    }
                }
                is ApiResult.ApiError -> {
                    postEffect(ProfileContract.Effect.ShowSnackBar(it.message))
                }
                is ApiResult.NetworkError -> {
                    postEffect(ProfileContract.Effect.ShowSnackBar(Constants.NETWORK_ERROR_MESSAGE))
                }
            }
        }
    }
}
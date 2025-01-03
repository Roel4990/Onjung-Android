package com.daon.onjung.ui.home.ocr

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.daon.onjung.Constants
import com.daon.onjung.data.repository.OnjungRepository
import com.daon.onjung.network.adapter.ApiResult
import com.daon.onjung.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OcrCameraViewModel @Inject constructor(
    private val onjungRepository: OnjungRepository
) : BaseViewModel<OcrCameraContract.State, OcrCameraContract.Event, OcrCameraContract.Effect>(
    initialState = OcrCameraContract.State()
) {

    init {
        viewModelScope.launch {
            delay(2000)
            updateState(currentState.copy(isGuideTextVisible = false))
        }
    }

    override fun reduceState(event: OcrCameraContract.Event) {
        viewModelScope.launch {
            when (event) {
                is OcrCameraContract.Event.ImageCaptured -> {
                    postOcr(event.uri)
                }

                is OcrCameraContract.Event.PostReceiptButtonClicked -> {
                    postReceipt()
                }

                is OcrCameraContract.Event.OcrErrorDialogDismissed -> {
                    postEffect(OcrCameraContract.Effect.CameraResume)
                    updateState(currentState.copy(isOcrErrorDialogVisible = false))
                }

                is OcrCameraContract.Event.OcrPostErrorDialogDismissed -> {
                    updateState(currentState.copy(isOcrPostErrorDialogVisible = false))
                }

                is OcrCameraContract.Event.PostReceiptSuccessDialogDismissed -> {
                    updateState(currentState.copy(isPostReceiptSuccessDialogVisible = false))
                }
            }
        }
    }

    private fun postOcr(receiptImageUri: Uri) = viewModelScope.launch {
        onjungRepository.postReceiptOcr(receiptImageUri).onStart {
            updateState(currentState.copy(isLoading = true))
        }.collect {
            updateState(currentState.copy(isLoading = false))
            when (it) {
                is ApiResult.Success -> {
                    val data = it.data?.data
                    data?.let {
                        updateState(currentState.copy(
                            storeName = data.storeName,
                            storeAddress = data.storeAddress,
                            paymentDate = data.paymentDate,
                            paymentAmount = data.paymentAmount
                        ))
                        postEffect(OcrCameraContract.Effect.ShowOcrResult(
                            data.storeName,
                            data.storeAddress,
                            data.paymentDate,
                            data.paymentAmount
                        ))
                    }
                }

                is ApiResult.ApiError -> {
                    updateState(currentState.copy(isOcrErrorDialogVisible = true))
                }

                is ApiResult.NetworkError -> {
                    updateState(currentState.copy(isOcrErrorDialogVisible = true))
                }
            }
        }
    }

    private fun postReceipt() = viewModelScope.launch {
        onjungRepository.postReceipt(
            storeName = currentState.storeName,
            storeAddress = currentState.storeAddress,
            paymentDate = currentState.paymentDate,
            paymentAmount = currentState.paymentAmount
        ).onStart {
            updateState(currentState.copy(isLoading = true))
        }.collect {
            updateState(currentState.copy(isLoading = false))
            when (it) {
                is ApiResult.Success -> {
                    updateState(currentState.copy(isPostReceiptSuccessDialogVisible = true))
                }

                is ApiResult.ApiError -> {
                    updateState(currentState.copy(isOcrPostErrorDialogVisible = true))
                }

                is ApiResult.NetworkError -> {
                    postEffect(OcrCameraContract.Effect.ShowSnackBar(Constants.NETWORK_ERROR_MESSAGE))
                }
            }
        }
    }
}
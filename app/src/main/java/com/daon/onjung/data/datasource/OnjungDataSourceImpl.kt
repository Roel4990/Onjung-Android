package com.daon.onjung.data.datasource

import com.daon.onjung.di.IoDispatcher
import com.daon.onjung.network.model.request.PostReceiptRequest
import com.daon.onjung.network.service.OnjungService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class OnjungDataSourceImpl @Inject constructor(
    private val onjungService: OnjungService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : OnjungDataSource {

    override fun getOnjungSummary() = flow {
        emit(onjungService.getOnjungSummary())
    }.flowOn(ioDispatcher)

    override fun postReceiptOcr(receiptFile: MultipartBody.Part) = flow {
        emit(onjungService.postReceiptOcr(receiptFile))
    }.flowOn(ioDispatcher)

    override fun postReceipt(postReceiptRequest: PostReceiptRequest) = flow {
        emit(onjungService.postReceipt(postReceiptRequest))
    }.flowOn(ioDispatcher)

    override fun getOnjungCount() = flow {
        emit(onjungService.getOnjungCount())
    }.flowOn(ioDispatcher)

    override fun getOnjungBrief() = flow {
        emit(onjungService.getOnjungBrief())
    }.flowOn(ioDispatcher)

}
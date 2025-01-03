package com.daon.onjung.network.service

import com.daon.onjung.network.adapter.ApiResult
import com.daon.onjung.network.model.BaseResponse
import com.daon.onjung.network.model.request.DonationRequest
import com.daon.onjung.network.model.request.PostReceiptRequest
import com.daon.onjung.network.model.response.DonationResponse
import com.daon.onjung.network.model.response.MyOnjungBriefResponse
import com.daon.onjung.network.model.response.OcrResponse
import com.daon.onjung.network.model.response.OnjungBriefResponse
import com.daon.onjung.network.model.response.OnjungCountResponse
import com.daon.onjung.network.model.response.OnjungSummaryResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface OnjungService {

    @GET("/api/v1/onjungs/summaries")
    suspend fun getOnjungSummary() : ApiResult<BaseResponse<OnjungSummaryResponse>>

    @Multipart
    @POST("/api/v1/receipts/ocr")
    suspend fun postReceiptOcr(
        @Part receiptFile: MultipartBody.Part
    ): ApiResult<BaseResponse<OcrResponse>>

    @POST("/api/v1/receipts")
    suspend fun postReceipt(
        @Body postReceiptRequest: PostReceiptRequest
    ): ApiResult<BaseResponse<Any>>

    @GET("/api/v1/onjungs/count")
    suspend fun getOnjungCount() : ApiResult<BaseResponse<OnjungCountResponse>>

    @GET("/api/v1/onjungs/briefs")
    suspend fun getOnjungBrief() : ApiResult<BaseResponse<OnjungBriefResponse>>

    @POST("/api/v1/stores/{id}/donations")
    suspend fun postDonation(
        @Path("id") id: Int,
        @Body donationRequest: DonationRequest
    ): ApiResult<BaseResponse<DonationResponse>>

    @GET("/api/v1/users/onjungs/briefs")
    suspend fun getMyOnjungBriefs() : ApiResult<BaseResponse<MyOnjungBriefResponse>>
}
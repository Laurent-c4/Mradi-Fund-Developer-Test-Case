package com.example.mradifundmobiledevelopertestcase;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UploadApi {

    @Multipart
    @POST("upload.json")
    Call<ResponseBody> uploadPDF(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part pdf,
            @Query("id") String userId
    );
}

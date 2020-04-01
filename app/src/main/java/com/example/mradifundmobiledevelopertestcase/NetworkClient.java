package com.example.mradifundmobiledevelopertestcase;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static Retrofit retrofit;
    public static String BASE_URL = "https://mradifund-developer-test-case.herokuapp.com/";

            public static Retrofit getRetrofit() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
                if (retrofit==null) {
                    retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClient).build();
                }
                return retrofit;
            }
}

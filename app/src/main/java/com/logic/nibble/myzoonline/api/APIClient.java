package com.logic.nibble.myzoonline.api;

import android.content.Context;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 *
 * Created by Parth Patel
 * Date : 17-12-2020 8:11 PM
 *
 * */
public class APIClient {

    //builders
//    public static final String BASE_URL = "http://192.168.2.10/myzoonline/v1/";
//    public static final String BASE_URL = "http://dhanlaxmi-finance.co.in/v1/";
    public static final String BASE_URL = "http://myzoonline.com/v1/";

    private static Retrofit retrofit = null;
    public static HttpLoggingInterceptor logging = null;
    public static OkHttpClient client = null;

    public static Retrofit getClient(final Context context) {
        //For log in console request and response : PP
        if (logging == null) {
            logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}

package com.logic.nibble.myzoonline.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/*
    *
    * Created by Parth Patel
    * Date : 19-01-2020 12:37 AM
    *
* */
public interface APIService {

    //Auth current shop session
    @FormUrlEncoded
    @POST("api.myzoonline.shop.auth")
    Call<ResponseBody> authShopSession(
            @Field("x-api") String xapi,
            @Field("access_token") String token,
            @Field("shop_id") String userid
    );

    //Shop Login
    @FormUrlEncoded
    @POST("api.myzoonline.shop.login")
    Call<ResponseBody>  shopLogin(
            @Field("x-api") String xapi,
            @Field("shop_digital_id") String userid,
            @Field("password") String password
    );

    //Add Shop account
    @FormUrlEncoded
    @POST("api.myzoonline.shop.register")
    Call<ResponseBody> createShopAccount(
            @Field("x-api") String xapi,
            @Field("first_name") String firstname,
            @Field("last_name") String lastname,
            @Field("shop_name") String shopname,
            @Field("mobile") String mobile,
            @Field("mobile_country_code") String countrycode,
            @Field("full_mobile") String fullmobile,
            @Field("gender") String gender,
            @Field("password") String password
    );

    //OTP Verification
    @FormUrlEncoded
    @POST("api.myzoonline.shop.verification.otp")
    Call<ResponseBody> authShopOTP(
            @Field("x-api") String xapi,
            @Field("shop_id") String shop_id,
            @Field("verification_code") String token

    );

    //Resend OTP
    @FormUrlEncoded
    @POST("api.myzoonline.shop.resend.otp")
    Call<ResponseBody> authShopResendOTP(
            @Field("x-api") String xapi,
            @Field("shop_id") String shop_id
    );

    //Shop profile
    @FormUrlEncoded
    @POST("api.myzoonline.shop.profile")
    Call<ResponseBody> shopProfile(
            @Field("x-api") String xapi,
            @Field("access_token") String token,
            @Field("shop_id") String userid
    );

    //Add Shop account
    @FormUrlEncoded
    @POST("api.myzoonline.shop.update")
    Call<ResponseBody> updateShopProfile(
            @Field("x-api") String xapi,
            @Field("shop_id") String shop_id,
            @Field("access_token") String token,
            @Field("first_name") String firstname,
            @Field("last_name") String lastname,
            @Field("shop_name") String shopname,
            @Field("mobile") String mobile,
            @Field("mobile_country_code") String countrycode,
            @Field("full_mobile") String fullmobile,
            @Field("gender") String gender,
            @Field("city") String city,
            @Field("state") String state,
            @Field("address_1") String address1,
            @Field("address_2") String address2,
            @Field("email") String email,
            @Field("zipcode") String shop_zipcode
    );



    //Shop Update password
    @FormUrlEncoded
    @POST("api.myzoonline.shop.update.password")
    Call<ResponseBody> updateNewPassword(
            @Field("x-api") String xapi,
            @Field("access_token") String token,
            @Field("shop_id") String shop_id,
            @Field("old_password") String old_password,
            @Field("new_password") String new_password,
            @Field("logout_from_other_devices") int logout_from_other_devices
    );

    //Auth current session
    @FormUrlEncoded
    @POST("rest-auth-current-session")
    Call<ResponseBody> authSession(
            @Field("x-api") String xapi,
            @Field("access_token") String token,
            @Field("user_id") String userid
    );

    //Login
    @FormUrlEncoded
    @POST("rest-login")
    Call<ResponseBody> userLogin(
            @Field("x-api") String xapi,
            @Field("username") String userid,
            @Field("password") String password
    );
    //Get villages
    @FormUrlEncoded
    @POST("rest-fetch-cust-village")
    Call<ResponseBody> getVillages(
            @Field("x-api") String xapi,
            @Field("access_token") String token,
            @Field("user_id") String userid
    );



    //Add Transaction
    @FormUrlEncoded
    @POST("rest-add-trans")
    Call<ResponseBody> addTransaction(
            @Field("x-api") String xapi,
            @Field("access_token") String token,
            @Field("user_id") String userid,
            @Field("amount") String amount,
            @Field("date_of_transaction") String date_of_transaction,
            @Field("account_no") String accountno,
            @Field("village_id") String village_id
    );

    //Add Customer Feed
    @FormUrlEncoded
    @POST("rest-add-cst-form-feed")
    Call<ResponseBody> listCustomers(
            @Field("x-api") String xapi,
            @Field("access_token") String token,
            @Field("user_id") String userid
    );

    //Customer Report
    @FormUrlEncoded
    @POST("rest-fetch-cst-report")
    Call<ResponseBody> viewReport(
            @Field("x-api") String xapi,
            @Field("access_token") String token,
            @Field("user_id") String userid,
            @Field("account_no") String accountno,
            @Field("village_id") String villageid,
            @Field("month") String month,
            @Field("year") String year
    );

    //Agent Report
    @FormUrlEncoded
    @POST("rest-fetch-agt-report")
    Call<ResponseBody> viewMyList(
            @Field("x-api") String xapi,
            @Field("access_token") String token,
            @Field("user_id") String userid,
            @Field("date") String date
    );
}

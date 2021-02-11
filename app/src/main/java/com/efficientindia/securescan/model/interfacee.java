package com.efficientindia.securescan.model;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface interfacee {

    @Headers(("apicode:YNVJGgy693qddc792VMAi6d3DLWE"))
    @FormUrlEncoded
    @POST("verify")
    Call<Response> scan(@Field("data") String data);

    @Headers(("apicode:YNVJGgy693qddc792VMAi6d3DLWE"))
    @FormUrlEncoded
    @POST("register")
    Call<Response> register(@Field("name") String name,@Field("email") String email,@Field("mobile") String mobile,@Field("address") String address,@Field("city") String city,@Field("pin") String pin,@Field("dateofpurchase") String dateofpurchase,@Field("date") String data);

    @Headers(("apicode:YNVJGgy693qddc792VMAi6d3DLWE"))
    @FormUrlEncoded
    @POST("report")
    Call<Response> report(@Field("name") String name,@Field("mobile") String mobile,@Field("remark") String remark,@Field("data") String data);

}

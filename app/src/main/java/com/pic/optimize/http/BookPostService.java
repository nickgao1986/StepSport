package com.pic.optimize.http;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BookPostService {
    @POST("/api/v1/books")
    @FormUrlEncoded
    Call<BookResponse> getResult(@Field("bookName") String name, @Field("bookDescription") String description);
}

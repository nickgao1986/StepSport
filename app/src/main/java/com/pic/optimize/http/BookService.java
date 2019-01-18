package com.pic.optimize.http;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BookService {
    @GET("/api/v1/books")
    Call<BookResponse> getResult();
}

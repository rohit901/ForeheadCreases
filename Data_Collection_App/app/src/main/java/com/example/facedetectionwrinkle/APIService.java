package com.example.facedetectionwrinkle;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {

    @POST("subjects/")
    Call<Subjects> createSubject(@Body Subjects subjects);

    @GET("users/{id}/")
    Call<User> getUser(@Path("id") String userCode);

    @GET("users/{id}/sub_list/")
    Call<List<Subjects>> getRelatedSubjects(@Path("id") String userCode);

    @FormUrlEncoded
    @PATCH("subjects/{id}/")
    Call<Subjects> updateSubjectCount(@Path("id") String subjCode, @Field("count") int count);

}

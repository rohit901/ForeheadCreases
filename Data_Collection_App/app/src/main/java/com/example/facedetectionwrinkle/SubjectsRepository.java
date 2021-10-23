package com.example.facedetectionwrinkle;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubjectsRepository {

    private static SubjectsRepository instance;

    private APIService subjectsService;

    public static SubjectsRepository getInstance() {
        if (instance == null) {
            instance = new SubjectsRepository();
        }
        return instance;
    }

    public SubjectsRepository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://<api-endpoint>/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        subjectsService = retrofit.create(APIService.class);

    }

    public APIService getSubjectsService() {
        return subjectsService;
    }

}

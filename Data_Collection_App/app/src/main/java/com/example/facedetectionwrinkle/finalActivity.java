package com.example.facedetectionwrinkle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class finalActivity extends AppCompatActivity {
    TextView notice;
    TextView sessionTV;
    String imageFilePath = "";
    String fileName = "";
    String id;
    Handler handler;
    Handler handler2;
    private RequestQueue rQueue;
    private String upload_URL = "http://<api-endpoint>/upload/";
    public static final String SHARED_PREF = "com.example.facedetectionwrinkle";
    private boolean isFirstTime = true;
    boolean isFirstDone = false;
    boolean isSecondDone = false;
    boolean isThirdDone = false;
    boolean isFourthDone = false;
    boolean isFifthDone = false;
    boolean isSixthDone = false;

    Button newSub;
    Button newSes;
    private String session = "session1";
    int sessNo;
    SharedPreferences mPrefs;
    int deleteCnter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        sessionTV = (TextView) findViewById(R.id.sessionTV);

        newSub = (Button)findViewById(R.id.newSubBtn);
        newSes = (Button)findViewById(R.id.newSesBtn);

        mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE); //add key
        session = mPrefs.getString("session", "session1");

        sessionTV.setText(sessionTV.getText().toString() + session);

        sessNo = Integer.parseInt(String.valueOf(session.charAt(session.length() - 1)));




        newSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sessNo >= 3) {

                    sessNo = 1;
                    session = "session" + sessNo;
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putString("session", session).apply();

                    File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    File[] files = dir.listFiles();

                    for(int i = 0; i < files.length; i++){

                        if(files[i].delete()){
                            deleteCnter += 1;
                        }
                    }
                    if (deleteCnter == 6) {

                        prefsEditor.putBoolean("first_done", false).apply();
                        prefsEditor.putBoolean("second_done", false).apply();
                        prefsEditor.putBoolean("third_done", false).apply();
                        prefsEditor.putBoolean("fourth_done", false).apply();
                        prefsEditor.putBoolean("fifth_done", false).apply();
                        prefsEditor.putBoolean("sixth_done", false).apply();
                        prefsEditor.putBoolean("first_session", true).apply();
                        prefsEditor.putBoolean("first_time", true).apply();


                        prefsEditor.commit();

                        Intent intent = new Intent(finalActivity.this, MainActivity.class);
                        startActivity(intent);


                    } else {
                        Toast.makeText(finalActivity.this, "Delete of 1 or more File has Failed.", Toast.LENGTH_SHORT).show();
                    }




                } else {
                    Toast.makeText(finalActivity.this, "Please Complete Session for Current Subject.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        newSes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessNo += 1;
                session = "session" + sessNo;

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("session", session).apply();


                File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File[] files = dir.listFiles();

                for(int i = 0; i < files.length; i++){
//                    imageFilePath = files[i].getAbsolutePath();
//                    fileName = files[i].getName();
//                    Log.d("file901", fileName);
//                    uploadData(imageFilePath, fileName);

                    if(files[i].delete()){
                        deleteCnter += 1;
                    }



                }
                if (deleteCnter == 6) {

                    prefsEditor.putBoolean("first_done", false).apply();
                    prefsEditor.putBoolean("second_done", false).apply();
                    prefsEditor.putBoolean("third_done", false).apply();
                    prefsEditor.putBoolean("fourth_done", false).apply();
                    prefsEditor.putBoolean("fifth_done", false).apply();
                    prefsEditor.putBoolean("sixth_done", false).apply();
                    prefsEditor.putBoolean("first_session", false).apply();
                    prefsEditor.putBoolean("first_time", true).apply();


                    prefsEditor.commit();

                    Intent intent = new Intent(finalActivity.this, CameraXLivePreviewActivity.class);
                    startActivity(intent);


                } else {
                    Toast.makeText(finalActivity.this, "Delete of 1 or more File has Failed.", Toast.LENGTH_SHORT).show();
                }


            }
        });


        notice = (TextView)findViewById(R.id.noticeTV);

        isFirstTime = mPrefs.getBoolean("first_time", true);

        if(!isOnline()) {
            notice.setText("Please connect your\ndevice to Internet, and Reopen the app.");
//            Toast.makeText(this, "Please connect your device to Internet.", Toast.LENGTH_SHORT).show();
        }


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File[] files = dir.listFiles();
                Log.d("Files", "Size: " + files.length);

                if (isFirstTime) {

                    if (files.length != 6) {
                        notice.setText("Image Capture Failed\n Try Again");
                        //Toast.makeText(finalActivity.this, "Image Capture Failed, Try Again.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(finalActivity.this, "Capture Success.", Toast.LENGTH_SHORT).show();
                        id = mPrefs.getString("usr_id", "");
                        if (id.equalsIgnoreCase("")) {

                            id = UUID.randomUUID().toString();
                        }
                        for(int i = 0; i < files.length; i++){
                            imageFilePath = files[i].getAbsolutePath();
                            fileName = files[i].getName();
                            Log.d("file901", fileName);
                            uploadData(imageFilePath, fileName);

                        }
                    }
                } else {

                    if (files.length == 6) {


                    SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE); //add key
                    isFirstDone = mPrefs.getBoolean(files[0].getName(), false);
                    isSecondDone = mPrefs.getBoolean(files[1].getName(), false);
                    isThirdDone = mPrefs.getBoolean(files[2].getName(), false);
                    isFourthDone = mPrefs.getBoolean(files[3].getName(), false);
                    isFifthDone = mPrefs.getBoolean(files[4].getName(), false);
                    isSixthDone = mPrefs.getBoolean(files[5].getName(), false);

                    if (isFirstDone && isSecondDone && isThirdDone && isFourthDone && isFifthDone && isSixthDone) {
                        notice.setText("Photos have been Uploaded");
                    } else {
                        notice.setText("One or More photos\n were not uploaded.");
                    }
                } else {
                        notice.setText("Image Capture Failed\n Try Again");
                    }
                }

            }
        },500);



    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    private void uploadImage(final Bitmap bitmap, String imgName){

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, upload_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.d("ressssssoo",new String(response.data));
                        rQueue.getCache().clear();

                        //Toast.makeText(finalActivity.this, new String(response.data), Toast.LENGTH_LONG).show();

                        SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        prefsEditor.putBoolean("first_time", false).apply();
                        prefsEditor.putBoolean(imgName, true).apply();
                        prefsEditor.putString("usr_id", id).apply();
                        prefsEditor.commit();
                        if(imgName.equalsIgnoreCase("pose_six_"+session+".jpg")) {

                            notice.setText("Photos have been Uploaded");
                        }

                    }
                        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("res901", "Error. "+ error.toString());
                        Log.d("res901", "Error2. "+ error.getLocalizedMessage());
                        Log.d("res901", "Error3. "+ error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // params.put("tags", "ccccc");  add string parameters

                SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE); //add key
                String name = mPrefs.getString("name", "");
                params.put("name",name+"_"+id); //user ID
                return params;
            }

            /*
             *pass files using below method
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imgName, getFileDataFromDrawable(bitmap)));
                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadData(String imageFilePath2, String fileName){

        final Bitmap[] gphoto = {null};


        Glide.with(finalActivity.this).asBitmap().load(imageFilePath2).into(new CustomTarget<Bitmap>(){
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                byte[] BYTE;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //resource.compress(Bitmap.CompressFormat.JPEG,50,bytes);
                //BYTE = bytes.toByteArray();
                Bitmap resource2;
                resource2 = getResizedBitmap(resource, 200);



                uploadImage(resource2, fileName);




            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

        });


    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

}
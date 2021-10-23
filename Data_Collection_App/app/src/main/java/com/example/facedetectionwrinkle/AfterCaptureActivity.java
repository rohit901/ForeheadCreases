package com.example.facedetectionwrinkle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.ybq.android.spinkit.SpinKitView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AfterCaptureActivity extends AppCompatActivity {
    Button captureAgn;
    Button uploadPhoto;
    String id;
    Button dialogButton;
    ImageView displayImage;
    SpinKitView mySKV;
    TextView uploadStat;
    private String session = "session1";
    AlertDialog uploadDialog;
    boolean isUploadSucc = false;
    String file;
    private RequestQueue rQueue;
    private String upload_URL = "http://<api-endpoint>/upload/";
    public static final String SHARED_PREF = "com.example.facedetectionwrinkle";
    SharedPreferences mPrefs;
    String subject;
    int subjCount;
    Bitmap croppedMap;
    private long mLastClickTime = 0;

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (exit) {
            finish(); // finish activity
        } else {
//            Toast.makeText(this, "Press Back again to Exit.",
//                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_capture);

        displayImage = (ImageView) findViewById(R.id.displayIV);

        mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        session = mPrefs.getString("session", "session1");
        subject = mPrefs.getString("subject","");
        subjCount = mPrefs.getInt("selSub_count",0);
        id = mPrefs.getString("user", "");
//        if (id.equalsIgnoreCase("")) {
//
//            id = UUID.randomUUID().toString();
//            SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
//            SharedPreferences.Editor prefsEditor = mPrefs.edit();
//            prefsEditor.putString("usr_id", id).apply();
//        }

        file = mPrefs.getString("photoPath", "");

        captureAgn = (Button) findViewById(R.id.recordAgnBtn);
        uploadPhoto = (Button) findViewById(R.id.uploadCapBtn);


        File imgFile = new File(file);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());



//            new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    try (FileOutputStream out = new FileOutputStream(file)) {
//                        croppedMap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
//                        // PNG is a lossless format, the compression factor (100) is ignored
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }}.start();





            Glide.with(AfterCaptureActivity.this).asBitmap().load(imgFile.getAbsolutePath()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new CustomTarget<Bitmap>(){
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    //Log.d("img901", imgFile.getAbsolutePath());
                    int width = resource.getWidth();
                    int height = resource.getHeight();

                    float yourwidth = 0.77f*width - ( 0.22f*width );
                    float yourheight = 0.2f*height - (0.09f*height);
                    croppedMap = Bitmap.createBitmap(resource, (int)(0.22f*width),(int)(0.09f*height),(int)yourwidth, (int)yourheight);
                    displayImage.setImageBitmap(croppedMap);

                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }

            });



        }


//        viewPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.parse(file), "image/*");
//                //intent.setData(Uri.parse(file));
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                }
//            }
//        });

        captureAgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putBoolean("pose_one", false).apply();
                Intent intent = new Intent(AfterCaptureActivity.this, CameraXLivePreviewActivity.class);
                startActivity(intent);
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                showUploadAlertDialog(R.layout.dialog_upload_layout);
                if (subjCount < 5) {
                    session = "S1";
                    subjCount += 1;
                } else {
                    session = "S2";
                    subjCount -= 4;
                }
                uploadData(file.toString(), subject+"_P1_" + session + "_"+subjCount+ ".jpg");

            }
        });




    }
    private void showUploadAlertDialog(int layout) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View layoutView = getLayoutInflater().inflate(layout, null);
        dialogButton = layoutView.findViewById(R.id.btnDialog2);
        dialogButton.setVisibility(View.GONE);
        mySKV = layoutView.findViewById(R.id.spin_kit);
        uploadStat = (TextView) layoutView.findViewById(R.id.uploadStatTV);
        uploadStat.setVisibility(View.INVISIBLE);
        dialogBuilder.setView(layoutView);



        uploadDialog = dialogBuilder.create();

        uploadDialog.setCancelable(false);
        uploadDialog.setCanceledOnTouchOutside(false);
        uploadDialog.show();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDialog.dismiss();
                if (isUploadSucc) {
                    Toast.makeText(AfterCaptureActivity.this, "Upload Success! Please Upload Data for Pose #2.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AfterCaptureActivity.this, SecondActivity.class);
                    SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putBoolean("first_upload", true).apply();
                    startActivity(intent);

                }

            }
        });
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

                        dialogButton.setVisibility(View.VISIBLE);
                        mySKV.setVisibility(View.GONE);
                        uploadStat.setVisibility(View.VISIBLE);
                        uploadStat.setText("Upload Done!");
                        isUploadSucc = true;
                        if (isUploadSucc) {
                            Toast.makeText(AfterCaptureActivity.this, "Upload Success! Please Upload Data for Pose #2.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AfterCaptureActivity.this, SecondActivity.class);
                            SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            prefsEditor.putBoolean("first_upload", true).apply();
                            startActivity(intent);

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("res901", "Error. "+ error.toString());
                        Log.d("res901", "Error2. "+ error.getLocalizedMessage());
                        Log.d("res901", "Error3. "+ error.getMessage());
                        Toast.makeText(AfterCaptureActivity.this, "Error. Make sure your internet is connected.", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        dialogButton.setVisibility(View.VISIBLE);
                        mySKV.setVisibility(View.GONE);
                        uploadStat.setVisibility(View.VISIBLE);
                        uploadStat.setText("Upload Failed, Try Again!");
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
                String name = mPrefs.getString("user", "");
                params.put("name",name); //user ID
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


        Glide.with(AfterCaptureActivity.this).asBitmap().load(imageFilePath2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(new CustomTarget<Bitmap>(){
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                byte[] BYTE;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //resource.compress(Bitmap.CompressFormat.JPEG,50,bytes);
                //BYTE = bytes.toByteArray();
                Bitmap resource2;
                resource2 = getResizedBitmap(resource, 800);



                uploadImage(resource2, fileName);




            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

        });


    }


}
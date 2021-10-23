package com.example.facedetectionwrinkle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zain.android.internetconnectivitylibrary.ConnectionUtil;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText name;
    Button submit;
    public static final String SHARED_PREF = "com.example.facedetectionwrinkle";
    Boolean isFirstTime;
    Boolean firstDone;
    Boolean secondDone;
    Boolean thirdDone;
    Boolean fourthDone;
    Boolean fifthDone;
    Boolean sixthDone;

    androidx.appcompat.widget.Toolbar toolbar;

    Boolean isFirstSession;

    boolean poseOne;
    boolean poseTwo;
    boolean first_upload;
    String myuser;
    CheckBox checkBox;
    ConnectionUtil connectionUtil;

    private SubjectsRepository subjectsRepository;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE); //add key
        poseOne = mPrefs.getBoolean("pose_one", false);
        poseTwo = mPrefs.getBoolean("pose_two", false);
        myuser = mPrefs.getString("user", "");

        first_upload = mPrefs.getBoolean("first_upload", false);
//        isFirstSession = mPrefs.getBoolean("first_session", true);
//        isFirstTime = mPrefs.getBoolean("first_time", true);
//        firstDone = mPrefs.getBoolean("first_done", false);
//        secondDone = mPrefs.getBoolean("second_done", false);
//        thirdDone = mPrefs.getBoolean("third_done", false);
//        fourthDone = mPrefs.getBoolean("fourth_done", false);
//        fifthDone = mPrefs.getBoolean("fifth_done", false);
//        sixthDone = mPrefs.getBoolean("sixth_done", false);
//
//        if (firstDone && !secondDone && !thirdDone) {
//            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//            startActivity(intent);
//        } else if (firstDone && secondDone && !thirdDone) {
//            Intent intent = new Intent(MainActivity.this, poseThree.class);
//            startActivity(intent);
//        } else if (firstDone && secondDone && thirdDone && !fourthDone) {
//            Intent intent = new Intent(MainActivity.this, poseFour.class);
//            startActivity(intent);
//        } else if (firstDone && secondDone && thirdDone && fourthDone && !fifthDone) {
//            Intent intent = new Intent(MainActivity.this, poseFive.class);
//            startActivity(intent);
//        } else if (firstDone && secondDone && thirdDone && fourthDone && fifthDone && !sixthDone) {
//            Intent intent = new Intent(MainActivity.this, poseSix.class);
//            startActivity(intent);
//        }
//        else if (firstDone && secondDone && thirdDone && fourthDone && fifthDone && sixthDone) {
//            Intent intent = new Intent(MainActivity.this, finalActivity.class);
//            startActivity(intent);
//        }

        //else if(!firstDone && isFirstSession) {
        if (poseTwo) {
            Intent intent = new Intent(MainActivity.this, AfterCaptureActivity2.class);
            startActivity(intent);
        } else if (first_upload) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        } else if (poseOne) {
            Intent intent = new Intent(MainActivity.this, AfterCaptureActivity.class);
            startActivity(intent);
        } else if (!myuser.equalsIgnoreCase("")) {
            Intent intent = new Intent(MainActivity.this, AfterLogInActivity.class);
            startActivity(intent);
        } else {




        setContentView(R.layout.activity_main);
            checkBox = (CheckBox) findViewById(R.id.checkbox1);


            toolbar =  findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.developerBTN) {
                    Intent intent = new Intent(MainActivity.this, DevPage.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.supervisorBTN) {
                    Intent intent = new Intent(MainActivity.this, SupervisorActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });


            subjectsRepository = subjectsRepository.getInstance();
        connectionUtil = new ConnectionUtil(MainActivity.this);

        name = (EditText) findViewById(R.id.name_box);
        submit = (Button) findViewById(R.id.complete_button);
        requestMultiplePermissions();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!connectionUtil.isOnline()) {
                    Toast.makeText(MainActivity.this, "Please connect your device to internet.", Toast.LENGTH_SHORT).show();
                }
                else if (!checkBox.isChecked()) {
                    Toast.makeText(MainActivity.this, "Click on the checkbox to proceed.", Toast.LENGTH_SHORT).show();
                }
                else if (name.getText().toString().trim().equalsIgnoreCase("")) {
                    name.setError("This field cannot be blank.");
                } else {

                    //Subjects mysub = new Subjects("abc123", "abc123_3", 3, 23, 'F');
                    subjectsRepository.getSubjectsService().getUser(name.getText().toString().trim()).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                prefsEditor.putString("user", name.getText().toString().trim()).apply();

                                Intent intent = new Intent(MainActivity.this, AfterLogInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                                startActivity(intent);
                                //Toast.makeText(MainActivity.this, "Success. ID: " + response.body().getBitsID(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                            Toast.makeText(MainActivity.this, "Make sure you are connected to internet.", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
//                        subjectsRepository.getSubjectsService().updateSubjectCount("abc123_3",28).enqueue(new Callback<Subjects>() {
//                            @Override
//                            public void onResponse(Call<Subjects> call, Response<Subjects> response) {
//                                if (response.isSuccessful())
//                                    Toast.makeText(getApplicationContext(), "Subj Code: " + response.body().getSubjectCode() + " Updated.", Toast.LENGTH_SHORT).show();
//                                else {
//                                   Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                              }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Subjects> call, Throwable t) {
//                                Toast.makeText(getApplicationContext(), "Error Updating Subject: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                        subjectsRepository.getSubjectsService().createSubject(mysub).enqueue(new Callback<Subjects>() {
//                            @Override
//                            public void onResponse(Call<Subjects> call, Response<Subjects> response) {
//                                if (response.isSuccessful())
//                                    Toast.makeText(getApplicationContext(), "Subj Code: " + response.body().getSubjectCode() + " created", Toast.LENGTH_SHORT).show();
//                                else {
//                                    Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Subjects> call, Throwable t) {
//                                Toast.makeText(getApplicationContext(), "Error Creating Subject: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });

//                        SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
//                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//                        prefsEditor.putString("name", name.getText().toString().trim()).apply();

//                        Intent intent = new Intent(MainActivity.this, CameraXLivePreviewActivity.class);
//                        startActivity(intent);
                }
            }
        });
        // }
//        else if (!isFirstSession && !firstDone) {
//            Intent intent = new Intent(MainActivity.this, CameraXLivePreviewActivity.class);
//            startActivity(intent);
//        }
    }

    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {



                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();


                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

}
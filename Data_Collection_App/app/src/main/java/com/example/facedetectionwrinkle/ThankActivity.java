package com.example.facedetectionwrinkle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ThankActivity extends AppCompatActivity {
    Button uploadMore;
    public static final String SHARED_PREF = "com.example.facedetectionwrinkle";
    SharedPreferences mPrefs;
    private String session = "session1";
    int sessNo;
    Button devBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank);

        uploadMore = (Button) findViewById(R.id.uploadMoreBTN);
        devBTN = (Button) findViewById(R.id.devBtn);

        mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE); //add key
        session = mPrefs.getString("session", "session1");

        sessNo = Integer.parseInt(String.valueOf(session.charAt(session.length() - 1)));
        sessNo += 1;
        session = "session" + sessNo;

        SharedPreferences mPrefs2 = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs2.edit();
        prefsEditor.putString("session", session).apply();
        prefsEditor.putBoolean("pose_one", false).apply();
        prefsEditor.putBoolean("pose_two", false).apply();
        prefsEditor.putBoolean("first_upload", false).apply();

        uploadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThankActivity.this, AfterLogInActivity.class);
                startActivity(intent);
            }
        });

        devBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ThankActivity.this, DevPage.class);
                startActivity(intent);

            }
        });
    }
}
package com.example.facedetectionwrinkle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DevPage extends AppCompatActivity {
    ImageView linkedImage;
    ImageView githubImage;
    ImageView twitImage;
    ImageView emailImage;

    TextView linkedText;
    TextView githubText;
    TextView twitText;
    TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_page);

        linkedImage = (ImageView) findViewById(R.id.linkedImageView);
        twitImage = (ImageView) findViewById(R.id.twitImageView);
        githubImage = (ImageView) findViewById(R.id.nameImageView);
        emailImage = (ImageView) findViewById(R.id.emailImageView);


        linkedText = (TextView) findViewById(R.id.linkedTextView);
        twitText = (TextView)findViewById(R.id.twitTextView);
        githubText = (TextView) findViewById(R.id.nameTextView);
        emailText = (TextView) findViewById(R.id.emailTV);

        linkedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://linkedin.com/in/rohit901";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        linkedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://linkedin.com/in/rohit901";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        twitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://twitter.com/rohit901";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        twitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://twitter.com/rohit901";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        githubImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/rohit901/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        githubText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/rohit901/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        emailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] myaddr = {"bharadwaj.rohit8@gmail.com"};
                composeEmail(myaddr, "Regarding Forehead App");
            }
        });

        emailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] myaddr = {"bharadwaj.rohit8@gmail.com"};
                composeEmail(myaddr, "Regarding Forehead App");
            }
        });



    }
    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
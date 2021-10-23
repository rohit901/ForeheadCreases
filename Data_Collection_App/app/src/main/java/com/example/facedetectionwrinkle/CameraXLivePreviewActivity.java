/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.facedetectionwrinkle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.impl.VideoCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Integer.min;
import static java.lang.Math.abs;

//import com.google.mlkit.vision.demo.preference.PreferenceUtils;
//import com.google.mlkit.vision.demo.preference.SettingsActivity;
//import com.google.mlkit.vision.demo.preference.SettingsActivity.LaunchSource;

/**
 * Live preview demo app for ML Kit APIs using CameraX.
 */


public final class CameraXLivePreviewActivity extends AppCompatActivity
        implements OnRequestPermissionsResultCallback {
    private static final String TAG = "CameraXLivePreview";
    private static final int PERMISSION_REQUESTS = 1;
    private TextView botText;

    private static final String FACE_DETECTION = "Face Detection";


    private static final String STATE_SELECTED_MODEL = "selected_model";
    private static final String STATE_LENS_FACING = "lens_facing";

    private PreviewView previewView;
    public static final String SHARED_PREF = "com.example.facedetectionwrinkle";
    SharedPreferences mPrefs;
    private GraphicOverlay graphicOverlay;

    @Nullable
    private ProcessCameraProvider cameraProvider;
    @Nullable
    private Preview previewUseCase;
    @Nullable
    private ImageAnalysis analysisUseCase;
    @Nullable
    private ImageCapture captureUseCase;
//    @Nullable
//    private VisionImageProcessor imageProcessor;
    @Nullable
    private VideoCapture videoCaptureUseCase;
    private boolean needUpdateGraphicOverlayImageSourceInfo;
    private ExecutorService mVideoCaptureExecutorService;
    private ExecutorService mImageCaptureExecutorService;

    private String selectedModel = FACE_DETECTION;
    private int lensFacing = CameraSelector.LENS_FACING_FRONT;
    private CameraSelector cameraSelector;

    double usrLat = 0.0;
    double usrLong = 0.0;

    private double RATIO_4_3_VALUE = 4.0 / 3.0;
    private double RATIO_16_9_VALUE = 16.0 / 9.0;

    Button captureBtn;
    String subject;
    int subjCount;
    String user;
    private String session = "session1";
    private long mLastClickTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Toast.makeText(CameraXLivePreviewActivity.this, "Capturing....", Toast.LENGTH_SHORT).show();

            File storageDir =
                    getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
//                File photoFile = new File(storageDir, new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US)
//                        .format(System.currentTimeMillis()) + ".jpg");
            if (subjCount < 5) {
                session = "S1";
                subjCount += 1;
            } else {
                session = "S2";
                subjCount -= 4;
            }
            File photoFile = new File(storageDir, subject+"_P1_" + session + "_"+subjCount+ ".jpg");
            // File videoFile = new File(storageDir, "palm_" + session + ".mp4");
            ImageCapture.Metadata metadata = new ImageCapture.Metadata();
            metadata.setReversedHorizontal(true);

            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                    photoFile
            ).setMetadata(metadata).build();

//                VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(
//                        videoFile
//                ).build();


//                videoCaptureUseCase.startRecording(outputFileOptions, mVideoCaptureExecutorService, new VideoCapture.OnVideoSavedCallback() {
//
//                    @Override
//                    public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
//                        Toast.makeText(CameraXLivePreviewActivity.this, "Video Saved!", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
//                        Toast.makeText(CameraXLivePreviewActivity.this, "Video Error!", Toast.LENGTH_SHORT).show();
//                    }
//                });


//                final Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        videoCaptureUseCase.stopRecording();
//                    }
//                }, 10000);
//



            captureUseCase.takePicture(outputFileOptions, mImageCaptureExecutorService, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                    //Toast.makeText(getApplicationContext(), "Saved Image: "+outputFileResults.getSavedUri(), Toast.LENGTH_SHORT).show();
                    //Log.e("save901", "Saved Image: " + outputFileResults.toString());

                    Intent intent = new Intent(CameraXLivePreviewActivity.this, AfterCaptureActivity.class);
                    SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putBoolean("pose_one", true).apply();
                    prefsEditor.putString("photoPath", photoFile.toString()).apply();


                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    startActivity(intent);



                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {

                    Log.e("err901", exception.getMessage());

                }
            });
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        session = mPrefs.getString("session", "session1");
        user = mPrefs.getString("user", "");
        subject = mPrefs.getString("subject","");
        subjCount = mPrefs.getInt("selSub_count",0);


        if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
            Toast.makeText(
                    getApplicationContext(),
                    "CameraX is only supported on SDK version >=21. Current SDK version is "
                            + VERSION.SDK_INT,
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

//        if (savedInstanceState != null) {
//            selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, FACE_DETECTION);
//            lensFacing = savedInstanceState.getInt(STATE_LENS_FACING, CameraSelector.LENS_FACING_BACK);
//        }
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
        setContentView(R.layout.activity_camerax_live_preview);
        botText = (TextView)findViewById(R.id.bot_Txt);
        captureBtn = (Button) findViewById(R.id.captureButton);

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                Toast.makeText(CameraXLivePreviewActivity.this, "Capturing....", Toast.LENGTH_SHORT).show();

                                File storageDir =
                        getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
//                File photoFile = new File(storageDir, new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US)
//                        .format(System.currentTimeMillis()) + ".jpg");
                if (subjCount < 5) {
                    session = "S1";
                    subjCount += 1;
                } else {
                    session = "S2";
                    subjCount -= 4;
                }
                File photoFile = new File(storageDir, subject+"_P1_" + session + "_"+subjCount+ ".jpg");
               // File videoFile = new File(storageDir, "palm_" + session + ".mp4");
                ImageCapture.Metadata metadata = new ImageCapture.Metadata();
                metadata.setReversedHorizontal(true);

                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                        photoFile
                ).setMetadata(metadata).build();

//                VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(
//                        videoFile
//                ).build();


//                videoCaptureUseCase.startRecording(outputFileOptions, mVideoCaptureExecutorService, new VideoCapture.OnVideoSavedCallback() {
//
//                    @Override
//                    public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
//                        Toast.makeText(CameraXLivePreviewActivity.this, "Video Saved!", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
//                        Toast.makeText(CameraXLivePreviewActivity.this, "Video Error!", Toast.LENGTH_SHORT).show();
//                    }
//                });


//                final Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        videoCaptureUseCase.stopRecording();
//                    }
//                }, 10000);
//



                                        captureUseCase.takePicture(outputFileOptions, mImageCaptureExecutorService, new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                                //Toast.makeText(getApplicationContext(), "Saved Image: "+outputFileResults.getSavedUri(), Toast.LENGTH_SHORT).show();
                                //Log.e("save901", "Saved Image: " + outputFileResults.toString());

                                Intent intent = new Intent(CameraXLivePreviewActivity.this, AfterCaptureActivity.class);
                                SharedPreferences mPrefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                prefsEditor.putBoolean("pose_one", true).apply();
                                prefsEditor.putString("photoPath", photoFile.toString()).apply();


                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                                startActivity(intent);



                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {

                                Log.e("err901", exception.getMessage());

                            }
                        });





            }
        });




//        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500,0,mLocationListener);



        previewView = findViewById(R.id.preview_view);
        if (previewView == null) {
            Log.d(TAG, "previewView is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }


//        List<String> options = new ArrayList<>();
////        options.add(OBJECT_DETECTION);
////        options.add(OBJECT_DETECTION_CUSTOM);
//        options.add(FACE_DETECTION);
//        options.add(TEXT_RECOGNITION);
//        options.add(BARCODE_SCANNING);
//        options.add(IMAGE_LABELING);
//        options.add(IMAGE_LABELING_CUSTOM);
//        options.add(AUTOML_LABELING);
        // Creating adapter for spinner
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
//        // Drop down layout style - list view with radio button
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
//        spinner.setAdapter(dataAdapter);
//        spinner.setOnItemSelectedListener(this);

        //ToggleButton facingSwitch = findViewById(R.id.facing_switch);
       // facingSwitch.setOnCheckedChangeListener(this);

        new ViewModelProvider(this, AndroidViewModelFactory.getInstance(getApplication()))
                .get(CameraXViewModel.class)
                .getProcessCameraProvider()
                .observe(
                        this,
                        provider -> {
                            cameraProvider = provider;
                            if (allPermissionsGranted()) {
                                bindAllCameraUseCases();
                            }
                        });

      //  ImageView settingsButton = findViewById(R.id.settings_button);
//        settingsButton.setOnClickListener(
//                v -> {
//                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
//                    intent.putExtra(
//                            SettingsActivity.EXTRA_LAUNCH_SOURCE,
//                            SettingsActivity.LaunchSource.CAMERAX_LIVE_PREVIEW);
//                    startActivity(intent);
//                });

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle bundle) {
//        super.onSaveInstanceState(bundle);
//        bundle.putString(STATE_SELECTED_MODEL, selectedModel);
//        bundle.putInt(STATE_LENS_FACING, lensFacing);
//    }

//    @Override
//    public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//        // An item was selected. You can retrieve the selected item using
//        // parent.getItemAtPosition(pos)
//        selectedModel = parent.getItemAtPosition(pos).toString();
//        Log.d(TAG, "Selected model: " + selectedModel);
//        bindAnalysisUseCase();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//        // Do nothing.
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        Log.d(TAG, "Set facing");
//        if (cameraProvider == null) {
//            return;
//        }
//
//        int newLensFacing =
//                lensFacing == CameraSelector.LENS_FACING_FRONT
//                        ? CameraSelector.LENS_FACING_BACK
//                        : CameraSelector.LENS_FACING_FRONT;
//        CameraSelector newCameraSelector =
//                new CameraSelector.Builder().requireLensFacing(newLensFacing).build();
//        try {
//            if (cameraProvider.hasCamera(newCameraSelector)) {
//                lensFacing = newLensFacing;
//                cameraSelector = newCameraSelector;
//                bindAllCameraUseCases();
//                return;
//            }
//        } catch (CameraInfoUnavailableException e) {
//            // Falls through
//        }
//        Toast.makeText(
//                getApplicationContext(),
//                "This device does not have lens with facing: " + newLensFacing,
//                Toast.LENGTH_SHORT)
//                .show();
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.live_preview_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.settings) {
//            Intent intent = new Intent(this, SettingsActivity.class);
//            intent.putExtra(SettingsActivity.EXTRA_LAUNCH_SOURCE, LaunchSource.CAMERAX_LIVE_PREVIEW);
//            startActivity(intent);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onResume() {
        super.onResume();
        bindAllCameraUseCases();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (imageProcessor != null) {
//            imageProcessor.stop();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (imageProcessor != null) {
//            imageProcessor.stop();
//        }
    }

    private void bindAllCameraUseCases() {
        bindPreviewUseCase();
        //bindAnalysisUseCase();
        //bindVideoCaptureUseCase();
        bindCaptureUseCase();
    }

    private void bindPreviewUseCase() {
//        if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
//            return;
//        }
        if (cameraProvider == null) {
            return;
        }
        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        previewUseCase = new Preview.Builder().build();
        previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, previewUseCase);
        graphicOverlay.clear();
        graphicOverlay.add(new CameraImageGraphic(graphicOverlay));
        graphicOverlay.postInvalidate();
    }

//    public static FaceDetectorOptions getFaceDetectorOptionsForLivePreview() {
//        int landmarkMode = FaceDetectorOptions.LANDMARK_MODE_NONE;
//        int contourMode = FaceDetectorOptions.CONTOUR_MODE_NONE;
//        int classificationMode = FaceDetectorOptions.CLASSIFICATION_MODE_NONE;
//        int performanceMode = FaceDetectorOptions.PERFORMANCE_MODE_FAST;
//
//        boolean enableFaceTracking = false;
//        float minFaceSize = Float.parseFloat("0.1");
//
//        FaceDetectorOptions.Builder optionsBuilder =
//                new FaceDetectorOptions.Builder()
//                        .setLandmarkMode(landmarkMode)
//                        .setContourMode(contourMode)
//                        .setClassificationMode(classificationMode)
//                        .setPerformanceMode(performanceMode)
//                        .setMinFaceSize(minFaceSize);
//        if (enableFaceTracking) {
//            optionsBuilder.enableTracking();
//        }
//        return optionsBuilder.build();
//    }




    int max(int width, int height) {
        if (width >= height) {
            return width;
        } else {
            return height;
        }
    }

    int min(int width, int height) {
        if (width >= height) {
            return height;
        } else {
            return width;
        }
    }

    private int aspectRatio(int width, int height) {
        double previewRatio = (double) max(width, height) / min(width, height);
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    private void bindCaptureUseCase() {
        if(cameraProvider == null){
            return;
        }
        if(captureUseCase != null){
            cameraProvider.unbind(captureUseCase);
            mImageCaptureExecutorService.shutdown();
        }

        mImageCaptureExecutorService = Executors.newSingleThreadExecutor();
        //mImageCaptureExecutorService = ContextCompat.getMainExecutor(this);
        ImageCapture.Builder builder = new ImageCapture.Builder();
        builder.setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY);

        captureUseCase = builder.build();

        cameraProvider.bindToLifecycle(this,cameraSelector, captureUseCase);


    }

//    private void bindAnalysisUseCase() {
//        if (cameraProvider == null) {
//            return;
//        }
//        if (analysisUseCase != null) {
//            cameraProvider.unbind(analysisUseCase);
//        }
//        if (imageProcessor != null) {
//            imageProcessor.stop();
//        }
//
//        try {
//            switch (selectedModel) {
//                case FACE_DETECTION:
//                    Log.i(TAG, "Using Face Detector Processor");
//                    FaceDetectorOptions faceDetectorOptions = getFaceDetectorOptionsForLivePreview();
//                    imageProcessor = new FaceDetectorProcessor(this, faceDetectorOptions);
//                    break;
//                default:
//                    throw new IllegalStateException("Invalid model name");
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Can not create image processor: " + selectedModel, e);
//            Toast.makeText(
//                    getApplicationContext(),
//                    "Can not create image processor: " + e.getLocalizedMessage(),
//                    Toast.LENGTH_LONG)
//                    .show();
//            return;
//        }
//
//        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
//        Size targetAnalysisSize = null;
//        if (targetAnalysisSize != null) {
//            builder.setTargetResolution(targetAnalysisSize);
//        }
//        analysisUseCase = builder.build();
//
//        needUpdateGraphicOverlayImageSourceInfo = true;
//        analysisUseCase.setAnalyzer(
//                // imageProcessor.processImageProxy will use another thread to run the detection underneath,
//                // thus we can just runs the analyzer itself on main thread.
//                ContextCompat.getMainExecutor(this),
//                imageProxy -> {
//                    if (needUpdateGraphicOverlayImageSourceInfo) {
//                        boolean isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT;
//                        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
//                        if (rotationDegrees == 0 || rotationDegrees == 180) {
//                            graphicOverlay.setImageSourceInfo(
//                                    imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
//                        } else {
//                            graphicOverlay.setImageSourceInfo(
//                                    imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
//                        }
//                        needUpdateGraphicOverlayImageSourceInfo = false;
//                    }
//                    try {
//
//                        imageProcessor.processImageProxy(imageProxy, graphicOverlay, captureUseCase, mImageCaptureExecutorService, getApplicationContext());
//                    } catch (MlKitException e) {
//                        Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
//                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
//                                .show();
//                    }
//                });
//
//        cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, analysisUseCase);
//    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            bindAllCameraUseCases();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }
}

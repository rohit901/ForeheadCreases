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

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import static android.content.Context.MODE_PRIVATE;

/**
 * Draw camera image to background.
 */
public class CameraImageGraphic extends GraphicOverlay.Graphic {


    private final Paint textPaint;
    private final Paint textPaint2;
    private final Paint bluePaint;
    private final Paint blueSpotPaint;
    private final Paint ovalPaint;
    private final Paint bgPaint;
    boolean isLocChanged;
    public static final String SHARED_PREF = "com.example.facedetectionwrinkle";
    SharedPreferences mPrefs;
    private final Paint ovalSpotPaint;
    private static final float OVAL_STROKE_WIDTH = 4f;

    public CameraImageGraphic(GraphicOverlay overlay) {
        super(overlay);


        mPrefs = getApplicationContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE); //add key
        isLocChanged = false;
        ovalPaint = new Paint();
        ovalPaint.setColor(Color.WHITE);
        ovalPaint.setStyle(Paint.Style.STROKE);
        ovalPaint.setStrokeWidth(OVAL_STROKE_WIDTH);
        ovalPaint.setPathEffect(new DashPathEffect(new float[] {20,40}, 0));

        bluePaint = new Paint();
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStyle(Paint.Style.STROKE);
        bluePaint.setStrokeWidth(6f);

        blueSpotPaint = new Paint();
        blueSpotPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        blueSpotPaint.setStyle(Paint.Style.FILL);
        blueSpotPaint.setColor(Color.WHITE);
        blueSpotPaint.setPathEffect(new DashPathEffect(new float[] {20,40}, 0));

        ovalSpotPaint = new Paint();
        ovalSpotPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        ovalSpotPaint.setStyle(Paint.Style.FILL);
        ovalSpotPaint.setColor(Color.WHITE);
        ovalSpotPaint.setPathEffect(new DashPathEffect(new float[] {20,40}, 0));

        bgPaint = new Paint();
        bgPaint.setAlpha(140);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(70f);

        textPaint2 = new Paint();
        textPaint2.setColor(Color.WHITE);
        textPaint2.setTextSize(50f);

    }

    @Override
    public void draw(Canvas canvas) {

        //isLocChanged = mPrefs.getBoolean("loc_changed",false);

        //canvas.drawBitmap(bitmap, getTransformationMatrix(), null);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        float left = 0.070f;
        RectF oval = new RectF(left*width,0.0225f*height, (1-left)*width,0.6925f*height);


        RectF foreHead = new RectF(0.22f*width, 0.09f*height, 0.77f*width,0.2f*height);

        canvas.drawPaint(bgPaint);
        //canvas.drawOval(oval, ovalSpotPaint);
        //canvas.drawOval(oval, ovalPaint);
        canvas.drawRect(foreHead, bluePaint);
        canvas.drawRect(foreHead, blueSpotPaint);

//        if(isLocChanged) {
//            canvas.drawText("Proceed with Photo.", width*0.22f,0.63f*height,textPaint);
//        } else {
//            canvas.drawText("Please wait for Location to be initialized.", width*0.11f,0.63f*height,textPaint2);
//        }

    }
}
package com.jaykapadia.qrscanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import java.io.IOException;


public class scan extends AppCompatActivity{
   private SurfaceView cameraSurfaceView;
   private BarcodeDetector barcodeDetector;
   private CameraSource cameraSource;
   final int requestCameraPermissionID = 1001;


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case requestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    try {
                        cameraSource.start(cameraSurfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);

        cameraSurfaceView = findViewById(R.id.cameraSurfaceView);
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setAutoFocusEnabled(true).build();
    }

    @Override
    public void onResume() {
        super.onResume();

        cameraSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request Permission
                    ActivityCompat.requestPermissions(scan.this, new String[]{Manifest.permission.CAMERA}, requestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraSurfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcode = detections.getDetectedItems();
                if (qrcode.size() != 0) {
                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);

                    Intent my = new Intent(scan.this,result.class);
                    my.putExtra("type", qrcode.valueAt(0));
                    Gson gson = new Gson();
                    String json = gson.toJson(qrcode.valueAt(0));
                    SharedPreferences sp = getSharedPreferences("list",Context.MODE_PRIVATE);
                    addhistory(sp,json);
                    startActivity(my);
                    finish();
                    barcodeDetector.release();
                }
            }
        });
    }


    public void addhistory(SharedPreferences sp,String json){
        int x = sp.getInt("index",0);
        SharedPreferences.Editor editor = sp.edit();
           editor.putString(String.valueOf(x),json);
           int  y;
            y = x+1;
            editor.putInt("index",y);
            editor.commit();


    }
    public void onBackPressed() {
        Intent my = new Intent(scan.this,MainActivity.class);
        startActivity(my);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}

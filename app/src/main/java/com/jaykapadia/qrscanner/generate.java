package com.jaykapadia.qrscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class generate extends AppCompatActivity{
    private Button generateButton;
    private EditText inputEditText;
    private ImageView QRCodeImageView;
    private Button share;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        generateButton = findViewById(R.id.generateButton);
        inputEditText =  findViewById(R.id.inputEditText);
        QRCodeImageView = findViewById(R.id.QRCodeImageView);
        share = findViewById(R.id.share);
    }

    @Override
    public void onResume() {
        super.onResume();

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textQR = inputEditText.getText().toString();
                MultiFormatWriter multiFormWriter = new MultiFormatWriter();
                if (textQR.length()==0){
                    Toast.makeText(getApplicationContext(),"Enter Any Text",Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        BitMatrix bitMatrix = multiFormWriter.encode(textQR, BarcodeFormat.QR_CODE, 260, 260);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap qrcodebitmap = barcodeEncoder.createBitmap(bitMatrix);
                        QRCodeImageView.setImageBitmap(qrcodebitmap);
                        try {

                            File cachePath = new File(getApplicationContext().getCacheDir(), "images");
                            cachePath.mkdirs(); // don't forget to make the directory
                            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                            qrcodebitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            stream.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File imagePath = new File(getApplicationContext().getCacheDir(), "images");
                File newFile = new File(imagePath, "image.png");
                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.myapp.fileprovider", newFile);

                if (contentUri != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                }
            }
        });
    }
    public void onBackPressed() {
        Intent my = new Intent(generate.this,MainActivity.class);
        startActivity(my);
        finish();
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent my = new Intent(generate.this,MainActivity.class);
            startActivity(my);
            finish();
        }
        if (item.getItemId() == R.id.history){
            Intent my = new Intent(generate.this,history.class);
            startActivity(my);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

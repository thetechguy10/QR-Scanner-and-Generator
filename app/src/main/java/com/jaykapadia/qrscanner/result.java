package com.jaykapadia.qrscanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.barcode.Barcode;
import java.util.Calendar;


public class result extends AppCompatActivity {

    private TextView t1,t2;
    private Button b2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        t1 = findViewById(R.id.type);
        t2 = findViewById(R.id.content);
        b2 = findViewById(R.id.button2);

        final Barcode b1 = getIntent().getParcelableExtra("type");
        switch (b1.valueFormat){
            case 1:
                t1.setText("Contact:");
                t2.setText(b1.contactInfo.name.first+" "+b1.contactInfo.name.last);
                b2.setText("Import Contact");
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent my = new Intent(Intent.ACTION_INSERT);
                        my.setType(ContactsContract.Contacts.CONTENT_TYPE);
                        my.putExtra(ContactsContract.Intents.Insert.NAME, b1.contactInfo.name.formattedName);
                        my.putExtra(ContactsContract.Intents.Insert.PHONE,b1.contactInfo.phones[0].number);
                        my.putExtra(ContactsContract.Intents.Insert.COMPANY, b1.contactInfo.organization);
                        my.putExtra(ContactsContract.Intents.Insert.EMAIL,b1.contactInfo.emails[0].address);
                        my.putExtra(ContactsContract.Intents.Insert.POSTAL, b1.contactInfo.addresses[0].addressLines[0]);
                        startActivity(my);
                    }
                });
                break;


            case 2:
                t1.setText("E-mail:");
                t2.setText(b1.email.address);
                b2.setText("Send E-mail");
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent my =new Intent(Intent.ACTION_SENDTO);
                        my.setData(Uri.parse("mailto:"));
                        my.putExtra(Intent.EXTRA_EMAIL,new String[]{b1.email.address});
                        my.putExtra(Intent.EXTRA_SUBJECT,b1.email.subject);
                        my.putExtra(Intent.EXTRA_TEXT,b1.email.body);
                        startActivity(my);
                    }
                });
                break;


            case 4:
                t1.setText("Phone:");
                t2.setText(b1.phone.number);
                b2.setText("Call");
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent my = new Intent(Intent.ACTION_DIAL);
                        my.setData(Uri.parse(b1.rawValue));
                        startActivity(my);
                    }
                });
                break;


            case 6:
                t1.setText("SMS:");
                t2.setText(b1.sms.message);
                b2.setText("Send Message");
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent my = new Intent(Intent.ACTION_VIEW);
                        my.setData(Uri.parse("smsto:"+b1.sms.phoneNumber));
                        my.putExtra("sms_body",b1.sms.message);
                        startActivity(my);
                    }
                });
                break;


            case 8:
                t1.setText("Link:");
                t2.setText(b1.rawValue);
                b2.setText("Visit");
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent my = new Intent(Intent.ACTION_VIEW);
                        my.setData(Uri.parse(b1.rawValue));
                        startActivity(my);
                    }
                });
                break;


            case 9:
                t1.setText("Wi-Fi:");
                t2.setText(b1.wifi.ssid);
                b2.setText("Connect");
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WifiConfiguration wifiConfig = new WifiConfiguration();
                        wifiConfig.SSID = String.format("\"%s\"",b1.wifi.ssid);
                        wifiConfig.preSharedKey = String.format("\"%s\"", b1.wifi.password);

                        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
                        int netId = wifiManager.addNetwork(wifiConfig);
                        wifiManager.setWifiEnabled(true);
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(netId, true);
                        wifiManager.reconnect();
                    }
                });
                break;


            case 10:
                t1.setText("Location:");
                t2.setText("Latitude:"+b1.geoPoint.lat+"  Longitude:"+b1.geoPoint.lng);
                b2.setText("Open in Maps");
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent my = new Intent(Intent.ACTION_VIEW);
                        my.setData(Uri.parse("geo:"+b1.geoPoint.lat+","+b1.geoPoint.lng));
                        startActivity(my);
                    }
                });
                break;


            case 11:
                t1.setText("Event:");
                t2.setText(b1.calendarEvent.summary);
                b2.setText("Add to Calendar");
                final String summary = b1.calendarEvent.summary;
                final String location = b1.calendarEvent.location;
                final String desc = b1.calendarEvent.description;
                Barcode.CalendarDateTime x = b1.calendarEvent.start;
                Barcode.CalendarDateTime y = b1.calendarEvent.end;
                final Calendar beginTime = Calendar.getInstance();
                beginTime.set(x.year,(x.month-1),x.day,x.hours,x.minutes);
                final Calendar endTime = Calendar.getInstance();
                endTime.set(y.year,(y.month-1),y.day,y.hours,y.minutes);
                b2.setOnClickListener(new View.OnClickListener() {
                 @Override
                      public void onClick(View view) {
                     Intent my = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE,summary)
                    .putExtra(CalendarContract.Events.DESCRIPTION,desc)
                    .putExtra(CalendarContract.Events.EVENT_LOCATION,location);
                    startActivity(my);
                            }
                     });
                     break;


            default:
                t1.setText("Text:");
                t2.setText(b1.rawValue);
                b2.setText("Copy");
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager c =(ClipboardManager)getApplicationContext().getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData =ClipData.newPlainText("text",b1.rawValue);
                        c.setPrimaryClip(clipData);
                        Toast.makeText(getApplicationContext(),"Text Copied",Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
}

    public void onBackPressed() {

        Intent my = new Intent(result.this,MainActivity.class);
        startActivity(my);
        finish();
        super.onBackPressed();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            Intent my = new Intent(result.this,MainActivity.class);
            startActivity(my);
            finish();
        }
        if (item.getItemId() == R.id.history){
            Intent my = new Intent(result.this,history.class);
            startActivity(my);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.jaykapadia.qrscanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;



public class history extends AppCompatActivity {

    ListView l1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
       l1 = findViewById(R.id.l1);
         Gson gson = new Gson();
         SharedPreferences sp = getSharedPreferences("list", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sp.getAll();
      int size = allEntries.entrySet().size();
      if(size == 0){
          final String[] display = getResources().getStringArray(R.array.history);
          ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,display);
          l1.setAdapter(adapter);
      }else{
          int siez = size-1;
          final String[] display = new String [siez];
          for(int i=0;i<siez;i++){
              String temp =  sp.getString(String.valueOf(i),"");
              final Barcode b1 = gson.fromJson(temp,Barcode.class);
              String pr = b1.displayValue;
              display[i]= pr;
          }
          Collections.reverse(Arrays.asList(display));
          ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,display);
          l1.setAdapter(adapter);
          l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                  int siez = display.length;
                  int x = siez-i-1;
                  action(x);
              }
          });
      }

    }

public void action(int i){
    Gson gson = new Gson();
    SharedPreferences sp = getSharedPreferences("list", Context.MODE_PRIVATE);
    String temp =  sp.getString(String.valueOf(i),"");
      final Barcode b1 = gson.fromJson(temp,Barcode.class);
    Intent my = new Intent(history.this,result.class);
     my.putExtra("type",b1);
      startActivity(my);
      finish();
}
    public void onBackPressed() {
        Intent my = new Intent(history.this,MainActivity.class);
        startActivity(my);
        finish();
        super.onBackPressed();
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent my = new Intent(history.this,MainActivity.class);
            startActivity(my);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

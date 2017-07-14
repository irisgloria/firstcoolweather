package com.example.fisrtcoolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather",null) != null){
            Intent intent = new Intent(this, WeatherActivity.class);  //如果有存储的天气情况，就先获取直接显示出来
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this,"对的",Toast.LENGTH_SHORT).show();
        }

    }
}

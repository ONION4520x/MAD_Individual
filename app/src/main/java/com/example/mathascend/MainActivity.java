package com.example.mathascend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageButton NumberOrdering, NumberComparing, NumberComposing ;
        NumberComparing = findViewById(R.id.myButton1);
        NumberOrdering = findViewById(R.id.myButton2);
        NumberComposing = findViewById(R.id.myButton3);

        //Topic 1
        NumberComparing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NumberComparing.class);
                startActivity(intent);
            }
        });

        //Topic 2
        NumberOrdering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NumberOrdering.class);
                startActivity(intent);
            }
        });

        //Topic 3
        NumberComposing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NumberComposing.class);
                startActivity(intent);
            }
        });


    }
}
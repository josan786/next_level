package ru.konstantin_starikov.samsung.aliensquest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Story.play((TextView) findViewById(R.id.textView), (Button) findViewById(R.id.button7), findViewById(R.id.buttonsLayout), getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
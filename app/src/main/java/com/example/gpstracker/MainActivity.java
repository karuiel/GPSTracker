package com.example.gpstracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.gpstracker.MESSAGE";
    public static int numberPoints=0;
    public static ArrayList<Punto> puntos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        puntos = new ArrayList<>();
    }

    /** Called when the user clicks the Send button */
    public void next(View view) {
        Intent intent = new Intent(this, IntroduceCoordinatesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

}

package com.example.gpstracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.gpstracker.MainActivity.EXTRA_MESSAGE;
import static com.example.gpstracker.MainActivity.numberPoints;

public class IntroduceCoordinatesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_coordinates);
        numberPoints+=1;
        TextView textView = (TextView) this.findViewById(R.id.punto);
        textView.setText("Punto "+String.valueOf(numberPoints));
    }

    public void anotherPoint(View view) {
        Intent intent = new Intent(this, IntroduceCoordinatesActivity.class);
        EditText lat = (EditText) findViewById(R.id.latitude);
        EditText lon = (EditText) findViewById(R.id.longitude);
        double latitude = Double.valueOf(lat.getText().toString());
        double longitude = Double.valueOf(lon.getText().toString());
        Punto punto = new Punto(longitude,latitude);
        MainActivity.puntos.add(punto);
        startActivity(intent);

    }



    public void gotoRoute(View view){
        Intent intent = new Intent(this, Route.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

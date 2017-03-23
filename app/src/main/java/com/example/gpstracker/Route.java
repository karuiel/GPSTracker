package com.example.gpstracker;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.hardware.GeomagneticField;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import java.text.DateFormat;
import java.util.Date;

import static com.example.gpstracker.MainActivity.numberPoints;
import static com.example.gpstracker.MainActivity.puntos;
public class Route extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, SensorEventListener, LocationListener {

        private SensorManager mSensorManager;
        GoogleApiClient mGoogleApiClient;
        private ImageView image;
        private TextView tvDistance;
        private Location target = new Location("B");
        private Sensor mMagneticSensor;
        private Sensor mAccelerometer;
        private float[] mLastAccelerometer = new float[3];
        private float[] mLastMagnetometer = new float[3];
        private boolean mLastAccelerometerSet = false;
        private boolean mLastMagnetometerSet = false;
        private float[] mR = new float[9];
        private float[] mOrientation = new float[3];
        private float mTargetDirection;
        String mLastUpdateTime;
        TextView mLongitudeTextView;
        TextView mLastUpdateTimeTextView;
        TextView mLatitudeTextView;
        Location mLastLocation;
        Double mLastDistance;
        int contador=0;
    private static final int MY_PERMISSIONS_REQUEST=0;
    LocationRequest mLocationRequest;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_route);
            mLatitudeTextView = (TextView) findViewById(R.id.mLatitude);
            mLongitudeTextView = (TextView) findViewById(R.id.mLongitude);
            mLastUpdateTimeTextView = (TextView) findViewById(R.id.mLastUpdate);
            //posicion actual
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            createLocationRequest();
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                            builder.build());


            ActivityCompat.requestPermissions(this,
                 new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                 MY_PERMISSIONS_REQUEST);


            image = (ImageView) findViewById(R.id.imageViewCompass);
            tvDistance = (TextView) findViewById(R.id.tvDistance);
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(puntos.size()!=0) {
            target.setLatitude(puntos.get(contador).getLatitude());
            target.setLongitude(puntos.get(contador).getLongitude());
            contador += 1;
        }else{
            Intent intent = new Intent(this, Final.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

       }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                    }

                } else {
                    break;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    @Override
    public void onConnected(Bundle connectionHint) {
            startLocationUpdates();
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }

    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onLocationChanged(Location location){
        mLastLocation=location;
        Punto currentLocation= new Punto(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        Punto currentTarget= new Punto(target.getLatitude(),target.getLongitude());
        mLastDistance =currentLocation.distancia(currentTarget);
        if(mLastDistance<50){
            if(contador==puntos.size()){
                Intent intent = new Intent(this, Final.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }else {
                target.setLatitude(puntos.get(contador).getLatitude());
                target.setLongitude(puntos.get(contador).getLongitude());
                contador += 1;
            }
        }

        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        updateUI();

    }
    private void updateUI() {
        mLatitudeTextView.setText(String.valueOf("Latitude: "+mLastLocation.getLatitude()));
        mLongitudeTextView.setText(String.valueOf("Longitude: "+mLastLocation.getLongitude()));
        mLastUpdateTimeTextView.setText("updateTime: "+mLastUpdateTime);
        tvDistance.setText("Distance: " + String.valueOf(mLastDistance));
    }
        @Override
    protected void onResume() {
        super.onResume();
        if (mMagneticSensor != null) {
            mSensorManager.registerListener(this, mMagneticSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        }

            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mMagneticSensor != null) {
            mSensorManager.unregisterListener(this);
        }
        if (mAccelerometer != null) {
            mSensorManager.unregisterListener(this);
        }
    }
    @Override
    public void onConnectionSuspended(int a){
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    @Override
    public void onSensorChanged(SensorEvent event) {

    // get the angle around the z-axis rotated
        if (event.sensor == mMagneticSensor) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        } else if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        }

        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];

            float azimuthInDegress = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

            mTargetDirection = azimuthInDegress;
        }

        float azimuth = -mTargetDirection;
        GeomagneticField geoField = new GeomagneticField( Double
                .valueOf( mLastLocation.getLatitude() ).floatValue(), Double
                .valueOf( mLastLocation.getLongitude() ).floatValue(),
                Double.valueOf( mLastLocation.getAltitude() ).floatValue(),
                System.currentTimeMillis() );
        if(geoField.getDeclination()<0) {
            azimuth += geoField.getDeclination(); // converts magnetic north into true north
        }else{
            azimuth -= geoField.getDeclination();
        }
        //Correct the azimuth
        azimuth = (azimuth+360) % 360;
        //get the bearing
        float y = (float)Math.sin(Math.toRadians(target.getLongitude()-mLastLocation.getLongitude())) * (float)Math.cos(Math.toRadians(target.getLatitude()));
        float x = (float)Math.cos(Math.toRadians(mLastLocation.getLatitude()))*(float)Math.sin(Math.toRadians(target.getLatitude())) -
                (float)Math.sin(Math.toRadians(mLastLocation.getLatitude()))*(float)Math.cos(Math.toRadians(target.getLatitude()))*(float)Math.cos(Math.toRadians(target.getLongitude()-mLastLocation.getLongitude()));
        float bearing = (float)Math.toDegrees(Math.atan2(y, x));
    image.setRotation(azimuth+bearing);
}
}




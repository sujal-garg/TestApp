package com.example.QAapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.PreviewConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Size;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.QAapp.utils.CommonMethods;
import com.example.QAapp.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class CameraXActivity extends AppCompatActivity {

    ImageCapture imageCapture = null;
    ListenableFuture<ProcessCameraProvider> processCameraProvider;

    private PreviewView mainPreview;
    private ImageAnalysis imageAnalysis;
    public SensorManager sensorManager;
    public Sensor proximitySensor;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_xactivity);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor == null){
            Toast.makeText(this,"Proximity sensor not present in this device",Toast.LENGTH_LONG).show();
        }else {
            sensorManager.registerListener(sensorEventListener,proximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.bottomNavFragmentHolder,
                CommonMethods.getBottomNavBundleFragment(Constants.CameraXActi)).commit();

        mainPreview = findViewById(R.id.cameraPreview);
        mainPreview.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);

        startCamera();

        findViewById(R.id.takePicture).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/camera.png";
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions
                        .Builder(new File(path)).build();
                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(getBaseContext()),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                Uri uri = outputFileResults.getSavedUri();
                                Toast.makeText(getApplicationContext(),uri.getPath(),Toast.LENGTH_LONG).show();
                                ImageView imageView = findViewById(R.id.clickedImage);
                                imageView.setImageURI(uri);
                                imageView.setVisibility(View.VISIBLE);
                            }
                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                Toast.makeText(getApplicationContext(),"Some error came while clicking picture",Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        findViewById(R.id.takePicture2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCapture.takePicture(ContextCompat.getMainExecutor(getApplicationContext()), new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        super.onCaptureSuccess(image);
                        Toast.makeText(getApplicationContext(),"Image Captured Successfully",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                        Toast.makeText(getApplicationContext(),"Image Not Captured Successfully",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
                if (event.values[0]==0){
                    Toast.makeText(getApplicationContext(),"Proximity is Near", Toast.LENGTH_LONG).show();
                }else {

                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startCamera(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            imageAnalysis = new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)
                    , new ImageAnalysis.Analyzer() {
                        @Override
                        public void analyze(@NonNull ImageProxy image) {
                            int rotationDegree = image.getImageInfo().getRotationDegrees();
                            image.close();
                        }
                    });

            processCameraProvider = ProcessCameraProvider.getInstance(this);
            processCameraProvider.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = processCameraProvider.get();
                    cameraProvider.unbindAll();

                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(mainPreview.getSurfaceProvider());
                    preview.setTargetRotation(Surface.ROTATION_0);

                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                    imageCapture = new ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();

                    cameraProvider.bindToLifecycle( this, cameraSelector,preview,imageCapture,imageAnalysis);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            },ContextCompat.getMainExecutor(this));
        }
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            Fragment supportFragment = null;
            switch (item.getItemId()) {
                case R.id.webpage:
                    supportFragment = new BrowserFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
                            .addToBackStack("browser").commit();
                    break;
                case R.id.buttonPage:
                    supportFragment = new ButtonViewFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
                            .addToBackStack("button").commit();
                    break;
                case R.id.hybridView:
                    supportFragment = new HybridViewFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
                            .addToBackStack("hybrid").commit();
                    break;
                case R.id.GPS:
//                    supportFragment = new LocationFragment(getGPSLocation());
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder,supportFragment)
//                            .addToBackStack("gps").commit();
                    break;
                case R.id.google:
                    supportFragment = new GoogleSignInFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
                            .addToBackStack("google").commit();
            }
            return true;
        }
    };

    @SuppressLint("MissingPermission")
    public Location getGPSLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                            , 10);
                }
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return new Location("");
    }
}
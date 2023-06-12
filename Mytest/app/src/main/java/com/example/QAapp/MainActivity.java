package com.example.QAapp;

import static com.example.QAapp.utils.Constants.ActivityName;
import static com.example.QAapp.utils.Constants.MainActi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import android.provider.MediaStore;
import android.provider.Settings;
import android.service.media.MediaBrowserService;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.media.MediaBrowserServiceCompat;

import com.example.QAapp.Camera.CameraActivity;
import com.example.QAapp.DTO.RequestDTO;
import com.example.QAapp.DTO.WebSocketConnRequest;
import com.example.QAapp.utils.CommonMethods;
import com.example.QAapp.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import tech.gusavila92.websocketclient.WebSocketClient;

public class MainActivity extends AppCompatActivity {

    public Location gpsLocation;
    protected WebSocketClient webSocketClient;
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static Integer countOfImages = 0;
    public SensorManager sensorManager;
    public Sensor proximitySensor;

    private static final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
    final String orderBy = MediaStore.Images.Media._ID;

    private String textUDID = "hello";

    private static String[] PERMISSION_EXTERNAL = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        registerReceiver(new ProximityOnOff(),new IntentFilter("com.example.QAapp.Proximity"));

        if (proximitySensor == null){
            Toast.makeText(this,"Proximity sensor not present in this device",Toast.LENGTH_LONG).show();
        }else {
            sensorManager.registerListener(sensorEventListener,proximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CAMERA);
        }
//        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED
//                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
//
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CAMERA);
//        } else {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    PERMISSION_EXTERNAL, REQUEST_EXTERNAL_STORAGE);
//        }

//        String[] proj = new String[] { Browser.BOOKMARKS_URI };
//        Cursor cursor = getContentResolver().query(Browser.BOOKMARKS_URI,
//                new String[] { Browser.BookmarkColumns.TITLE,
//                Browser.BookmarkColumns.URL }, null, null, null);

        List<PackageInfo> packageInfosUnknown = getApplication().getPackageManager().getInstalledPackages(0);
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,null,null,orderBy);

        if (cursor != null){
            countOfImages = cursor.getCount();
            cursor.close();
        }

        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setInstalledPackages(packageInfosUnknown);
        requestDTO.setGalleryImageCount(countOfImages);

        findViewById(R.id.cameraButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newActivity = new Intent(getApplicationContext(), CameraActivity.class);
                newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(newActivity);
            }
        });

        findViewById(R.id.cameraXActivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraXActivity = new Intent(getBaseContext(), CameraXActivity.class);
                cameraXActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(cameraXActivity);
            }
        });

        findViewById(R.id.QRCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent barCodeActivity = new Intent(getBaseContext(), BarCodeScannerActivity.class);
                barCodeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(barCodeActivity);
            }
        });

        findViewById(R.id.defaultCameraApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment defaultCameraAppFragment = new CameraAppFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentHolder,defaultCameraAppFragment).commit();
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.bottomNavFragmentHolder,
                CommonMethods.getBottomNavBundleFragment(MainActi)).commit();

        createWebSocketClient();
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
                if (event.values[0]==0){
                    Toast.makeText(getApplicationContext(),"Proximity is Near", Toast.LENGTH_LONG).show();
                }else {
//                    Toast.makeText(getApplicationContext(),"Proximity is Far", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    //    BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
//        @Override
//        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
//            Fragment supportFragment = null;
//            switch (item.getItemId()) {
//                case R.id.webpage:
//                    supportFragment = new BrowserFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
//                            .addToBackStack("browser").commit();
//                    break;
//                case R.id.buttonPage:
//                    supportFragment = new ButtonViewFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
//                            .addToBackStack("button").commit();
//                    break;
//                case R.id.hybridView:
//                    supportFragment = new HybridViewFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
//                            .addToBackStack("hybrid").commit();
//                    break;
//                case R.id.GPS:
////                    supportFragment = new LocationFragment(getGPSLocation());
////                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder,supportFragment)
////                            .addToBackStack("gps").commit();
//                    break;
//                case R.id.google:
//                    supportFragment = new GoogleSignInFragment();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
//                            .addToBackStack("google").commit();
//            }
//            return true;
//        }
//    };
//
//    @SuppressLint("MissingPermission")
//    public Location getGPSLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
//                            , 10);
//                }
//                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            }
//
//        } else {
//            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        }
//        return new Location("");
//    }

    private void createWebSocketClient() {
        URI uri;
        try {
            // Connect to local host
            uri = new URI("ws://192.168.1.84:8081/websocket");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
                SharedPreferences sharedPreferences = getSharedPreferences("globalVar",MODE_PRIVATE);
                String udid = sharedPreferences.getString("udid","1");
                WebSocketConnRequest webSocketConnRequest = new WebSocketConnRequest(udid);
                webSocketClient.send(new Gson().toJson(webSocketConnRequest));
            }

            @Override
            public void onTextReceived(String s) {
                Log.i("WebSocket", "Message received");
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TextView textView = findViewById(R.id.textChanger);
                            System.out.println(message + "Ankit123");
                            textView.setText(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }
            @Override
            public void onPingReceived(byte[] data) {
            }
            @Override
            public void onPongReceived(byte[] data) {
            }
            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                System.out.println("onCloseReceived");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }

    public void startHttpServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket socket = serverSocket.accept();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter os = new PrintWriter(socket.getOutputStream(), true);
                        inputStream.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
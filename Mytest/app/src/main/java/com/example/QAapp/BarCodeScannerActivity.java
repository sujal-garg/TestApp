package com.example.QAapp;

import static com.example.QAapp.utils.Constants.barcodeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.QAapp.utils.CommonMethods;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class BarCodeScannerActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    Button scanButton;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    TextView qrCodeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code_scanner);

        getSupportFragmentManager().beginTransaction().replace(R.id.bottomNavFragmentHolder,
                CommonMethods.getBottomNavBundleFragment(barcodeActivity)).commit();
        scanBarCode();
    }

    public void scanBarCode() {
        qrCodeTextView = findViewById(R.id.barCodeTextView);
        surfaceView = findViewById(R.id.barcodeViewer);
        scanButton = findViewById(R.id.barcodeClickButton);
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(BarCodeScannerActivity.this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(BarCodeScannerActivity.this
                                , new String[]{Manifest.permission.CAMERA}, 201);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leakage", Toast.LENGTH_LONG).show();
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodeData = detections.getDetectedItems();
                if (barcodeData.size() != 0 ){
                    qrCodeTextView.setText(barcodeData.valueAt(0).displayValue);
                }
            }
        });

    }
}
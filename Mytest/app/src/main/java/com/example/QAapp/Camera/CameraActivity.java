package com.example.QAapp.Camera;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.QAapp.R;
import com.google.android.gms.vision.CameraSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    private Camera camera;
    private CameraBackend cameraBackend;
    private Button cameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraButton = findViewById(R.id.camera2API);
        camera = Camera.open();

        cameraBackend = new CameraBackend(this,camera);
        FrameLayout cameralayout = findViewById(R.id.cameraHolder);
        cameralayout.addView(cameraBackend);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {

                camera.takePicture(null, null, (Camera.PictureCallback) (data, camera) -> {
                    File savedImage = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+"/clickedImage.jpg");
                    FileOutputStream fileOutputStream = null;
                    try {
                        savedImage.createNewFile();

                        fileOutputStream = new FileOutputStream(savedImage);
                        fileOutputStream.write(data);
                        fileOutputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.d("Camera API","Error came while reading the data stream from picture callback method");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("Camera API","Error came while reading the data stream from picture callback method");
                    }

                    cameralayout.removeAllViews();

                    ImageView capturedImageView = new ImageView(getApplicationContext());
                    capturedImageView.setId(R.id.clickedImageView);
                    capturedImageView.setMaxHeight(700);
                    capturedImageView.setMaxWidth(cameralayout.getWidth());
                    capturedImageView.setImageURI(Uri.fromFile(savedImage));

                    Button backButton = new Button(getApplicationContext());
                    backButton.setId(R.id.backButton);
                    backButton.setBackgroundColor(R.color.ravish_button_color);
                    backButton.setText("Back");

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.addRule(RelativeLayout.BELOW,R.id.clickedImageView);

                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cameralayout.removeAllViews();
                            cameralayout.addView(cameraBackend);
                        }
                    });
                    cameralayout.addView(capturedImageView);
                    cameralayout.addView(backButton, layoutParams);
                });
            }
        });
    }
}
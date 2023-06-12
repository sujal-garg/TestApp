package com.example.QAapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class ProximityOnOff extends BroadcastReceiver {

    Sensor proximitySensor = null;
    public String sensorActiveFlag = "false";

    public String getProximitySensorInformation(Context context){
        final SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor==null){
            Toast.makeText(context,"Proximity Sensor is not available in this device",Toast.LENGTH_LONG);
        }else {
            sensorManager.registerListener(proximitySensorEventListener,proximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
        return sensorActiveFlag;
    }

    SensorEventListener proximitySensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
                if (event.values[0] == 0) {
                    sensorActiveFlag = "true";
                }else {
                    sensorActiveFlag = "false";
                }
            }else {
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = getProximitySensorInformation(context);
        if (status.equalsIgnoreCase("false")){
            setResultCode(Activity.RESULT_CANCELED);
            setResultData("false");
            return;
        }else {
            setResultCode(Activity.RESULT_OK);
            setResultData("true");
            return;
        }
    }
}

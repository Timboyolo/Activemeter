package com.example.tim.acceleropres;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;


public class MainActivity extends ActionBarActivity implements SensorEventListener{

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float truX = 0;
    private float truY = 0;
    private float truZ = 0;

    private float vibrateThreshold = 0;
    private float moveThreshold = 0;

    private double vector = 0;

    private TextView currentX;
    private TextView currentY;
    private TextView currentZ;
    private TextView maxX;
    private TextView maxY;
    private TextView maxZ;
    private TextView Activiteit;
    private TextView Vector;
    private SeekBar progX;
    private SeekBar progY;
    private SeekBar progZ;
    private static DecimalFormat REAL_FORMATTER = new DecimalFormat("0.###");

    public Vibrator v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        Activiteit = (TextView) findViewById(R.id.Activiteit);
        Vector = (TextView) findViewById(R.id.Vector);

        progX = (SeekBar) findViewById(R.id.progressX);
        progY = (SeekBar) findViewById(R.id.progressY);
        progZ = (SeekBar) findViewById(R.id.progressZ);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
            moveThreshold = accelerometer.getMaximumRange() / 3;
        } else {
            // fail! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }


    //onResume() register the accelerometer for listening the events
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();
        // calculate the 3D motion vector
        calculateVector();

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        truX = lastX - event.values[0];
        truY = lastY - event.values[1];
        truZ = lastZ - event.values[2];

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if (deltaZ < 2)
            deltaZ = 0;
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            //v.vibrate(50);
            Activiteit.setText("Moving Fast");
        } else if ((deltaX > moveThreshold) || (deltaY > moveThreshold) || (deltaZ > moveThreshold)) {
            Activiteit.setText("Moving");
        } else {
            Activiteit.setText("Rest");
        }
    }

    public void calculateVector() {
        vector = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
        Vector.setText(REAL_FORMATTER.format(vector));
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
        progX.setEnabled(false);
        progY.setEnabled(false);
        progZ.setEnabled(false);
        int maxVal = 30;
        progX.setMax(maxVal);
        progY.setMax(maxVal);
        progZ.setMax(maxVal);
        progX.setProgress((int) ((maxVal / 2) + truX));
        progY.setProgress((int) ((maxVal / 2) + truY));
        progZ.setProgress((int) ((maxVal / 2) + truZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }
}

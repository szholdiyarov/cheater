package com.zholdiyarov.cheater;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by szholdiyarov on 2/1/16.
 */
public class ShakerActivity extends AppCompatActivity {

    private Button button_shake;
    private TextView textView_text;
    private Vibrator vibrator;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private SensorsShakersListener sensorsShakersListener;
    private Handler timerHandler;

    private boolean shakingIsStarted = false;
    private boolean timeIsRunning = false;
    private boolean isCounterUpdated;
    private boolean init;
    private int counterInitialValue = 0;
    private int count = counterInitialValue;
    private int seconds;
    private long startTime = 0;


    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            print(String.format("%d:%02d", minutes, seconds));

            if (seconds > 02) {
                if (getIsCounterUpdated()) {
                    restartTimer();
                } else {
                    vibrate();
                    stopShakingService();
                }

            } else {
                timerHandler.postDelayed(this, 500);
            }

        }
    };

    private void stopShakingService() {
        changeButtonState();
        deregisterSensorManager();
        resetCounterToItsInitialVal();
        resetTextViewToItsInitialVal();
        stopTimer();
    }

    private void restartTimer() {
        stopTimer();
        startTimer();
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        timeIsRunning = false;
    }

    private void startTimer() {
        if (!timeIsRunning) {
            timeIsRunning = true;
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }

    private void deregisterSensorManager() {
        mSensorManager.unregisterListener(sensorsShakersListener);
    }

    private void resetCounterToItsInitialVal() {
        count = counterInitialValue;
    }

    private void resetTextViewToItsInitialVal() {
        textView_text.setText("Your text:");
    }

    private void vibrate() {
        vibrator.vibrate(500);
    }

    private void declareAllVariables() {
        timerHandler = new Handler();
        button_shake = (Button) findViewById(R.id.button_shake);
        textView_text = (TextView) findViewById(R.id.textView_morseCodeText);
        sensorsShakersListener = new SensorsShakersListener();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void registerSensorManager() {
        mSensorManager.registerListener(sensorsShakersListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setIsCounterUpdated(boolean trueOrFalse) {
        isCounterUpdated = trueOrFalse;
    }

    private boolean getIsCounterUpdated() {
        return isCounterUpdated;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shaker);

        declareAllVariables();

        changeButtonState();

        button_shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shakingIsStarted) { // if button is already pressed => stop
                    stopShakingService();
                } else { // start shaking
                    changeButtonState();
                    registerSensorManager();
                }
            }
        });
    }

    private void changeButtonState() {
        Log.d("custom", "changeButtonState with " + shakingIsStarted);
        if (shakingIsStarted == false) {
            shakingIsStarted = true;
            button_shake.setText("Stop");

        } else {
            shakingIsStarted = false;
            button_shake.setText("Start");
        }
    }

    //Register the Listener when the Activity is resumed
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorsShakersListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //Unregister the Listener when the Activity is paused
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorsShakersListener);
    }

    public void print(String text) {
        Log.d("custom", text);
    }


    private class SensorsShakersListener implements SensorEventListener {


        private float x1, x2, x3;
        private static final float ERROR = (float) 5.0;


        @Override
        public void onSensorChanged(SensorEvent e) {

            if (count > 0 && !timeIsRunning) {
                print("if");
                startTimer();
            }
            //Get x,y and z values
            float x, y, z;
            x = e.values[0];
            y = e.values[1];
            z = e.values[2];

            //print("Sensor changed with x=" + x + ", y=" + y + ", z=" + z);

            if (!init) {
                x1 = x;
                x2 = y;
                x3 = z;
                init = true;
            } else {

                float diffX = Math.abs(x1 - x);
                float diffY = Math.abs(x2 - y);
                float diffZ = Math.abs(x3 - z);

                //Handling ACCELEROMETER Noise
                if (diffX < ERROR) {

                    diffX = (float) 0.0;
                }
                if (diffY < ERROR) {
                    diffY = (float) 0.0;
                }
                if (diffZ < ERROR) {

                    diffZ = (float) 0.0;
                }


                x1 = x;
                x2 = y;
                x3 = z;


                //Horizontal Shake Detected!
                if (diffX > diffY) {
                    count = count + 1;
                    setIsCounterUpdated(true);
                    textView_text.setText(textView_text.getText() + " " + count);
                } else {
                    setIsCounterUpdated(false);
                }
            }


        }


        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //Noting to do!!
        }


    }
}


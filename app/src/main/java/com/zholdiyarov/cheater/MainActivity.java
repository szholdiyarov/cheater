package com.zholdiyarov.cheater;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zholdiyarov.cheater.helpers.ShakeDetector;


public class MainActivity extends AppCompatActivity {

    private Button button_start_stop;
    private boolean shakingIsStarted = false;
    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    int counter = 0;
    private Vibrator vibrator;
    private boolean isTimerRunning;
    private boolean canCounterAcceptShakesNow;
    private int questionNumber;
    private int asnwerNumber;
    private boolean isWaitingForAnswer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_start_stop = (Button) findViewById(R.id.button_start_stop);
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        button_start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShakerActivity.class);
                startActivity(intent);
                if (shakingIsStarted) { // button started and now needs to stop;
                    changeButtonState();

                } else { // button has not start so now it needs to start
                    changeButtonState();
                }
            }
        });

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake() {
                if (isTimerRunning) {
                    if (canCounterAcceptShakesNow) {
                        counter++;
                        resetTimer();
                    } else {
                        return;
                    }
                } else {
                    startTimer();
                    counter++;
                }
                Log.d("shaker", "shake is detected " + counter);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        timerHandler.removeCallbacks(timerRunnable);
        super.onPause();
    }

    private void changeButtonState() {
        if (shakingIsStarted == false) {
            shakingIsStarted = true;
            button_start_stop.setText("Stop");
        } else {
            shakingIsStarted = false;
            button_start_stop.setText("Start");
        }
    }


    long startTime = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            int permittedSeconds = 2;
            int youCanNowSetAnswer = 7;
            int appWillSendAmessage = 15;

            long millis = System.currentTimeMillis() - startTime;
            long seconds = (long) (millis / 1000);
            long minutes = seconds / 60;
            seconds = seconds % 60;

            Log.d("timer", "seconds " + seconds);

            if (seconds < permittedSeconds) {
                canCounterAcceptShakesNow = false;
            } else if (seconds == permittedSeconds) { // notify that first letter is accepted
                Log.d("timer", permittedSeconds + " second is gone ");
                canCounterAcceptShakesNow = false;
                vibrator.vibrate(100);
                canCounterAcceptShakesNow = true;
            } else if (seconds == youCanNowSetAnswer) { // can send message
                Log.d("timer", youCanNowSetAnswer + " second is gone ");
                isWaitingForAnswer = true;
                canCounterAcceptShakesNow = false;
                long[] pattern = {0, 100, 400, 100};
                vibrator.vibrate(pattern, -1);
                //stopTimer();
                Log.d("timer", "final counter is " + counter);
                questionNumber = counter;
                counter = 0;
                canCounterAcceptShakesNow = true;
                return;
            } else if (seconds == appWillSendAmessage) {

            }
            timerHandler.postDelayed(this, 1000);
        }
    };


    private void startTimer() {
        setIsTimerRunning(true);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void stopTimer() {
        setIsTimerRunning(false);
        timerHandler.removeCallbacks(timerRunnable);
    }


    private void resetTimer() {
        stopTimer();
        startTimer();
    }


    public void setIsTimerRunning(boolean isTimerRunning) {
        this.isTimerRunning = isTimerRunning;
    }
}


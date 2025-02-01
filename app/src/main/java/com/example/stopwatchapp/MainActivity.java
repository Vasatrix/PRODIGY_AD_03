package com.example.stopwatchapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button startButton, pauseButton, resetButton, lapButton;
    private LinearLayout lapContainer;
    private ScrollView lapScrollView;

    private Handler handler = new Handler();
    private long startTime = 0L, elapsedTime = 0L;
    private boolean isRunning = false;
    private int lapCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);
        lapButton = findViewById(R.id.lapButton);
        lapContainer = findViewById(R.id.lapContainer);
        lapScrollView = findViewById(R.id.lapScrollView);

        // Button Click Listeners
        startButton.setOnClickListener(view -> startTimer());
        pauseButton.setOnClickListener(view -> pauseTimer());
        resetButton.setOnClickListener(view -> resetTimer());
        lapButton.setOnClickListener(view -> recordLap());
    }

    private void startTimer() {
        if (!isRunning) {
            isRunning = true;
            startTime = System.currentTimeMillis() - elapsedTime;
            handler.post(timerRunnable);

            // Update button states
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            resetButton.setEnabled(true);
            lapButton.setEnabled(true);
        }
    }

    private void pauseTimer() {
        if (isRunning) {
            isRunning = false;
            elapsedTime = System.currentTimeMillis() - startTime;
            handler.removeCallbacks(timerRunnable);

            // Update button states
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            lapButton.setEnabled(false);
        }
    }

    private void resetTimer() {
        isRunning = false;
        elapsedTime = 0L;
        handler.removeCallbacks(timerRunnable);
        timerTextView.setText("00:00:000");

        // Clear lap times
        lapContainer.removeAllViews();
        lapCount = 0;

        // Update button states
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);
        lapButton.setEnabled(false);
    }

    private void recordLap() {
        lapCount++;
        String lapTime = timerTextView.getText().toString();

        // Create a new TextView for the lap
        TextView lapTextView = new TextView(this);
        lapTextView.setText(String.format("Lap %d: %s", lapCount, lapTime));
        lapTextView.setTextSize(18);
        lapTextView.setTextColor(getResources().getColor(android.R.color.white));
        lapTextView.setPadding(8, 8, 8, 8);

        // Add the TextView to the lap container
        lapContainer.addView(lapTextView);

        // Scroll to the bottom of the ScrollView to show the latest lap
        lapScrollView.post(() -> lapScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime;

                int minutes = (int) (elapsedTime / 60000);
                int seconds = (int) (elapsedTime % 60000) / 1000;
                int milliseconds = (int) (elapsedTime % 1000);

                timerTextView.setText(String.format("%02d:%02d:%03d", minutes, seconds, milliseconds));
                handler.postDelayed(this, 10);
            }
        }
    };
}

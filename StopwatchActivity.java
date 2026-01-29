package com.example.timetrackpro;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StopwatchActivity extends AppCompatActivity {

    TextView tvTime;
    Button btnStart, btnStop, btnReset;

    Handler handler = new Handler();
    boolean running = false;
    long startTime = 0;
    long timeInMillis = 0;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (running) {
                timeInMillis = System.currentTimeMillis() - startTime;
                int seconds = (int) (timeInMillis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                tvTime.setText(String.format("%02d:%02d", minutes, seconds));
                handler.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        tvTime = findViewById(R.id.tvTime);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);

        btnStart.setOnClickListener(v -> {
            running = true;
            startTime = System.currentTimeMillis() - timeInMillis;
            handler.post(runnable);
        });

        btnStop.setOnClickListener(v -> running = false);

        btnReset.setOnClickListener(v -> {
            running = false;
            timeInMillis = 0;
            tvTime.setText("00:00");
        });
    }
}

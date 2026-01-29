package com.example.timetrackpro;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SleepFragment extends Fragment {

    Button btnSleepStart, btnWakeUp;
    TextView tvSleepStatus;
    ListView listSleepHistory;

    DatabaseHelper db;

    long currentSleepId = -1;
    long currentSleepStartTime = -1;

    ArrayList<String> sleepList = new ArrayList<>();

    public SleepFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_sleep, container, false);

        db = new DatabaseHelper(requireContext());

        btnSleepStart = v.findViewById(R.id.btnSleepStart);
        btnWakeUp = v.findViewById(R.id.btnWakeUp);
        tvSleepStatus = v.findViewById(R.id.tvSleepStatus);
        listSleepHistory = v.findViewById(R.id.listSleepHistory);

        btnSleepStart.setOnClickListener(view -> startSleep());
        btnWakeUp.setOnClickListener(view -> stopSleep());

        loadSleepHistory();

        return v;
    }

    private void startSleep() {
        if (currentSleepId != -1) {
            Toast.makeText(requireContext(), "Already sleeping 😴", Toast.LENGTH_SHORT).show();
            return;
        }

        currentSleepStartTime = System.currentTimeMillis();
        currentSleepId = db.startSleep(currentSleepStartTime);

        tvSleepStatus.setText("Status: Sleeping... 😴");
        Toast.makeText(requireContext(), "Sleep started ✅", Toast.LENGTH_SHORT).show();
    }

    private void stopSleep() {
        if (currentSleepId == -1) {
            Toast.makeText(requireContext(), "First click Sleep Start ❌", Toast.LENGTH_SHORT).show();
            return;
        }

        long end = System.currentTimeMillis();
        long duration = end - currentSleepStartTime;

        db.stopSleep(currentSleepId, end, duration);

        Toast.makeText(requireContext(), "Wake Up ✅ Sleep saved", Toast.LENGTH_SHORT).show();

        tvSleepStatus.setText("Status: Not Sleeping");

        currentSleepId = -1;
        currentSleepStartTime = -1;

        loadSleepHistory();
    }

    private void loadSleepHistory() {
        sleepList.clear();

        Cursor c = db.getAllSleepLogs();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());

        while (c.moveToNext()) {
            long start = c.getLong(1);
            long end = c.getLong(2);
            long duration = c.getLong(3);

            String startStr = sdf.format(new Date(start));
            String endStr = end == 0 ? "Running" : sdf.format(new Date(end));

            sleepList.add("Sleep: " + startStr + "\nWake: " + endStr + "\nTotal: " + formatDuration(duration));
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, sleepList);
        listSleepHistory.setAdapter(adapter);
    }

    private String formatDuration(long ms) {
        long sec = ms / 1000;
        long min = sec / 60;
        long hr = min / 60;
        min = min % 60;
        sec = sec % 60;
        return hr + "h " + min + "m " + sec + "s";
    }
}

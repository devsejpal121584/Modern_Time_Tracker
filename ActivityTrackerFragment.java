package com.example.timetrackpro;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActivityTrackerFragment extends Fragment {

    Button btnAddActivity;
    ListView listActivities, listLogs;

    DatabaseHelper db;

    ArrayList<String> activitiesList = new ArrayList<>();
    ArrayList<Integer> activityIds = new ArrayList<>();

    ArrayList<String> logsList = new ArrayList<>();
    ArrayList<Long> runningLogIds = new ArrayList<>();

    long currentRunningLogId = -1;
    long currentStartTime = -1;

    public ActivityTrackerFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_activity_tracker, container, false);

        db = new DatabaseHelper(requireContext());

        btnAddActivity = v.findViewById(R.id.btnAddActivity);
        listActivities = v.findViewById(R.id.listActivities);
        listLogs = v.findViewById(R.id.listLogs);

        loadActivities();
        loadLogs();

        btnAddActivity.setOnClickListener(view -> showAddActivityDialog());

        listActivities.setOnItemClickListener((parent, view, position, id) -> {
            int activityId = activityIds.get(position);
            String name = activitiesList.get(position);

            startTracking(activityId, name);
        });

        return v;
    }

    private void showAddActivityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Activity");

        EditText input = new EditText(requireContext());
        input.setHint("Example: Coding");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Enter activity name", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = db.addActivity(name);
            if (ok) {
                Toast.makeText(requireContext(), "Added ✅", Toast.LENGTH_SHORT).show();
                loadActivities();
            } else {
                Toast.makeText(requireContext(), "Already exists or error ❌", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void loadActivities() {
        activitiesList.clear();
        activityIds.clear();

        Cursor c = db.getAllActivities();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);

            activityIds.add(id);
            activitiesList.add(name);
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, activitiesList);
        listActivities.setAdapter(adapter);
    }

    private void startTracking(int activityId, String activityName) {

        if (currentRunningLogId != -1) {
            Toast.makeText(requireContext(), "Stop current activity first ❌", Toast.LENGTH_SHORT).show();
            return;
        }

        currentStartTime = System.currentTimeMillis();
        currentRunningLogId = db.startActivityLog(activityId, currentStartTime);

        Toast.makeText(requireContext(), "Started: " + activityName + " ✅", Toast.LENGTH_SHORT).show();

        new AlertDialog.Builder(requireContext())
                .setTitle("Tracking: " + activityName)
                .setMessage("Click STOP when you're done.")
                .setPositiveButton("STOP", (dialog, which) -> {
                    long end = System.currentTimeMillis();
                    long duration = end - currentStartTime;

                    db.stopActivityLog(currentRunningLogId, end, duration);

                    Toast.makeText(requireContext(), "Saved ✅", Toast.LENGTH_SHORT).show();

                    currentRunningLogId = -1;
                    currentStartTime = -1;

                    loadLogs();
                })
                .setCancelable(false)
                .show();
    }

    private void loadLogs() {
        logsList.clear();

        Cursor c = db.getAllLogs();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());

        while (c.moveToNext()) {
            String activityName = c.getString(1);
            long start = c.getLong(2);
            long end = c.getLong(3);
            long duration = c.getLong(4);

            String startStr = sdf.format(new Date(start));
            String endStr = end == 0 ? "Running" : sdf.format(new Date(end));
            String durationStr = formatDuration(duration);

            logsList.add(activityName + "\n" + startStr + " → " + endStr + "\nDuration: " + durationStr);
        }
        c.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, logsList);
        listLogs.setAdapter(adapter);
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

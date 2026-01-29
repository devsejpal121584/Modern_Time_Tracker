package com.example.timetrackpro;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppUsageActivity extends AppCompatActivity {

    TextView tvTotalScreenTime;
    Button btnPermission, btnLoadApps;
    RecyclerView recyclerApps;

    ArrayList<UsageAppModel> appList = new ArrayList<>();
    UsageAppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);

        tvTotalScreenTime = findViewById(R.id.tvTotalScreenTime);
        btnPermission = findViewById(R.id.btnPermission);
        btnLoadApps = findViewById(R.id.btnLoadApps);
        recyclerApps = findViewById(R.id.recyclerApps);

        recyclerApps.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsageAppAdapter(appList);
        recyclerApps.setAdapter(adapter);

        btnPermission.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            Toast.makeText(this, "Enable permission for TimeTrack Pro ✅", Toast.LENGTH_LONG).show();
        });

        btnLoadApps.setOnClickListener(v -> {
            if (!hasUsageAccessPermission()) {
                Toast.makeText(this, "Usage access permission NOT enabled!", Toast.LENGTH_LONG).show();
                return;
            }
            loadTodayUsage();
        });
    }

    private boolean hasUsageAccessPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void loadTodayUsage() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
        );

        if (stats == null || stats.size() == 0) {
            Toast.makeText(this, "No usage data found!", Toast.LENGTH_LONG).show();
            return;
        }

        appList.clear();

        long totalTime = 0;
        PackageManager pm = getPackageManager();

        for (UsageStats usageStats : stats) {
            long time = usageStats.getTotalTimeInForeground();

            if (time > 0) {
                totalTime += time;

                try {
                    ApplicationInfo ai = pm.getApplicationInfo(usageStats.getPackageName(), 0);
                    String appName = pm.getApplicationLabel(ai).toString();

                    appList.add(new UsageAppModel(appName, usageStats.getPackageName(), time));

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        long totalMinutes = totalTime / (1000 * 60);
        tvTotalScreenTime.setText("Total Screen Time Today: " + totalMinutes + " min");

        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Loaded ✅", Toast.LENGTH_SHORT).show();
    }
}

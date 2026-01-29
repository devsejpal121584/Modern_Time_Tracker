package com.example.timetrackpro;

public class UsageAppModel {

    private String appName;
    private String packageName;
    private long timeInForeground;

    public UsageAppModel(String appName, String packageName, long timeInForeground) {
        this.appName = appName;
        this.packageName = packageName;
        this.timeInForeground = timeInForeground;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public long getTimeInForeground() {
        return timeInForeground;
    }
}

package com.example.timetrackpro;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileOutputStream;

public class SettingsFragment extends Fragment {

    Button btnExport, btnClear, btnAbout;
    DatabaseHelper db;

    public SettingsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        db = new DatabaseHelper(requireContext());

        btnExport = v.findViewById(R.id.btnExport);
        btnClear = v.findViewById(R.id.btnClear);
        btnAbout = v.findViewById(R.id.btnAbout);

        btnExport.setOnClickListener(view -> exportCSV());
        btnClear.setOnClickListener(view -> clearAllData());
        btnAbout.setOnClickListener(view -> showAbout());

        return v;
    }

    private void exportCSV() {
        try {
            String fileName = "timetrack_export.csv";
            String data = "Feature Coming Soon (Pro Export)\n";

            FileOutputStream fos = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();

            Toast.makeText(requireContext(), "Exported ✅ (Internal Storage): " + fileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Export Failed ❌ " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearAllData() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Clear All Data?")
                .setMessage("This will delete all activity logs & sleep logs.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    requireContext().deleteDatabase(DatabaseHelper.DB_NAME);
                    Toast.makeText(requireContext(), "Data Cleared ✅ Restart App", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showAbout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("About TimeTrack Pro")
                .setMessage("TimeTrack Pro 🔥\n\nTrack your daily activities, sleep, and productivity.\n\nDeveloped by Abhi 💙")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

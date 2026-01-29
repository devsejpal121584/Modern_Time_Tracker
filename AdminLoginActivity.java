package com.example.timetrackpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {

            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // ✅ FIXED ADMIN CREDENTIALS (HERE)
            if (username.equals("admin") && password.equals("1234")) {

                Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show();

                // OPEN ADMIN PANEL
                startActivity(new Intent(this, AdminPanelActivity.class));
                finish();

            } else {
                Toast.makeText(this, "Wrong Admin Username or Password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

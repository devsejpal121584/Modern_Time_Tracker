package com.example.timetrackpro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_register);

        DatabaseHelper db = new DatabaseHelper(this);

        EditText name = findViewById(R.id.etName);
        EditText user = findViewById(R.id.etUsername);
        EditText pass = findViewById(R.id.etPassword);

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            if (db.registerUser(
                    name.getText().toString(),
                    user.getText().toString(),
                    pass.getText().toString())) {

                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });
    }
}

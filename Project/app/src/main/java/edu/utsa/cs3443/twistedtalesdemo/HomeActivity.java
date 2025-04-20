package edu.utsa.cs3443.twistedtalesdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    Button btnStart, btnSettings, btnCredits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnStart = findViewById(R.id.btnStart);
        btnSettings = findViewById(R.id.btnSettings);
        btnCredits = findViewById(R.id.btnCredits);

        btnStart.setOnClickListener(v -> startActivity(new Intent(this, StartActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnCredits.setOnClickListener(v -> startActivity(new Intent(this, CreditsActivity.class)));
    }
}

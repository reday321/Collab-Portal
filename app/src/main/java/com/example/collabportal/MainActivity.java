package com.example.collabportal;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null) startActivity(new Intent(this, ProfileActivity.class));
        else startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}

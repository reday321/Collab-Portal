package com.reday.collabportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import androidx.cardview.widget.CardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNavigationView;
    private ExtendedFloatingActionButton fabAddPost;

    // ✅ NEW: Declare home screen UI elements
    private View welcomeCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

// ✅ NEW: Initialize home screen UI elements
    welcomeCard = findViewById(R.id.welcomeCard);

// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

// Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
// Redirect to login activity if not authenticated
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

// Setup bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

// ✅ NEW: Initially show home elements (since we start with HomeFragment)
        showHomeElements();

// Load the default fragment (Home)
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();

// Setup FAB for adding posts
        fabAddPost = findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Navigate to Add Post fragment
                bottomNavigationView.setSelectedItemId(R.id.nav_add_post);
            }
        });

// Setup profile icon click
        ImageView ivProfile = findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Navigate to Profile fragment
                bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            }
        });
    }

    // ✅ NEW: Method to show home elements
    public void showHomeElements() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (welcomeCard != null) welcomeCard.setVisibility(View.VISIBLE);
                if (fabAddPost != null) fabAddPost.show();
            }
        });
    }

    // ✅ NEW: Method to hide home elements
    public void hideHomeElements() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (welcomeCard != null) welcomeCard.setVisibility(View.GONE);
                if (fabAddPost != null) fabAddPost.hide();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_home) {
                        selectedFragment = new HomeFragment();
                        fabAddPost.show();
                        showHomeElements(); // ✅ Show home elements for Home
                    } else if (itemId == R.id.nav_add_post) {
                        selectedFragment = new AddPostFragment();
                        fabAddPost.hide();
                        hideHomeElements(); // ✅ Hide home elements for other fragments
                    } else if (itemId == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                        fabAddPost.hide();
                        hideHomeElements(); // ✅ Hide home elements for other fragments
                    } else if (itemId == R.id.nav_about) {
                        selectedFragment = new AboutUsFragment();
                        fabAddPost.hide();
                        hideHomeElements(); // ✅ Hide home elements for other fragments
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }

                    return true;
                }
            };

    public void setSelectedNavigationItem(int itemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(itemId);
        }
    }


    public void loadHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
        setSelectedNavigationItem(R.id.nav_home);
    }
}
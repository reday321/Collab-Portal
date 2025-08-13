package com.example.collabportal;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private RecyclerView rvPosts;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabAddPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        rvPosts = findViewById(R.id.rvPosts);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        fabAddPost = findViewById(R.id.fabAddPost);

        setupToolbar();
        setupRecyclerView();
        setupSwipeRefresh();
        setupFab();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.action_profile) {
                // Navigate to profile activity
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Set up adapter and load posts
    }

    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            // TODO: Refresh posts
            swipeRefresh.setRefreshing(false);
        });
    }

    private void setupFab() {
        fabAddPost.setOnClickListener(v -> {
            // TODO: Navigate to create post screen
        });
    }
}

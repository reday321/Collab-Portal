package com.reday.collabportal;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView postsRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private com.reday.collabportal.PostAdapter postAdapter;
    private List<com.reday.collabportal.Post> postList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private MainActivity mainActivity;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ✅ Get the parent MainActivity
        mainActivity = (MainActivity) getActivity();

        // ✅ Ensure home elements are visible when this fragment is created
        if (mainActivity != null) {
            mainActivity.showHomeElements();
        }

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateText = view.findViewById(R.id.emptyStateText);

        // Setup RecyclerView
        postList = new ArrayList<>();
        postAdapter = new com.reday.collabportal.PostAdapter(getActivity(), postList, mAuth.getCurrentUser().getUid());
        postsRecyclerView.setHasFixedSize(true);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postsRecyclerView.setAdapter(postAdapter);

        // Load posts from Firestore
        loadPosts();

        return view;
    }

    private void loadPosts() {
        progressBar.setVisibility(View.VISIBLE);

        // Query to get all posts ordered by timestamp (newest first)
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Error loading posts: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    postList.clear();
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            com.reday.collabportal.Post post = doc.toObject(com.reday.collabportal.Post.class);
                            post.setPostId(doc.getId());
                            postList.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                        emptyStateText.setVisibility(View.GONE);
                        postsRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        emptyStateText.setVisibility(View.VISIBLE);
                        postsRecyclerView.setVisibility(View.GONE);
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }
}
package com.reday.collabportal;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddPostFragment extends Fragment {

    private TextInputEditText postContentEditText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public AddPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        postContentEditText = view.findViewById(R.id.postContentEditText);
        progressBar = view.findViewById(R.id.progressBar);

        // Post button click listener
        view.findViewById(R.id.postButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost();
            }
        });

        return view;
    }

    private void addPost() {
        String content = postContentEditText.getText().toString().trim();

        // Validate input
        if (content.isEmpty()) {
            postContentEditText.setError("Post content cannot be empty");
            postContentEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Get current user info
        String userId = mAuth.getCurrentUser().getUid();

        // ✅ FIX: Firestore theke user er name niye asha
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = "Anonymous User"; // Default value

                    if (documentSnapshot.exists()) {
                        // Different possible field names check kora
                        userName = documentSnapshot.getString("name");
                        if (userName == null) {
                            userName = documentSnapshot.getString("userName");
                        }
                        if (userName == null) {
                            userName = documentSnapshot.getString("displayName");
                        }
                        if (userName == null) {
                            userName = documentSnapshot.getString("fullName");
                        }
                        // Jodi kono field e name na pay
                        if (userName == null) {
                            userName = "Anonymous User";
                        }
                    }

                    // ✅ Ekhon post create korar somoy proper userName use kora
                    Map<String, Object> post = new HashMap<>();
                    post.put("userId", userId);
                    post.put("userName", userName); // ✅ Actual user name
                    post.put("content", content);
                    post.put("timestamp", new Date());
                    post.put("loves", 0);
                    post.put("lovedBy", new ArrayList<String>());

                    // Add post to Firestore
                    db.collection("posts")
                            .add(post)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.GONE);
                                    postContentEditText.setText("");
                                    Toast.makeText(getActivity(), "Post added successfully",
                                            Toast.LENGTH_SHORT).show();

                                    navigateToHomeFragment();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Error adding post: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Error getting user info: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToHomeFragment() {
        // Create new instance of HomeFragment
        HomeFragment homeFragment = new HomeFragment();

        // Use FragmentTransaction to replace the current fragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.addToBackStack(null); // Optional: Add to back stack
        transaction.commit();

        // Also update the bottom navigation selection
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setSelectedNavigationItem(R.id.nav_home);
        }
    }
}
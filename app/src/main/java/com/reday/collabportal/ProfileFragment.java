package com.reday.collabportal;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView nameTextView, emailTextView, studentIdTextView, phoneTextView, departmentTextView;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        studentIdTextView = view.findViewById(R.id.studentIdTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        departmentTextView = view.findViewById(R.id.departmentTextView);
        progressBar = view.findViewById(R.id.progressBar);


        // Load user data
        loadUserData();


        // Email edit button
        view.findViewById(R.id.editEmailButton).setOnClickListener(v -> editEmail());

        // Phone number edit button click listener
        view.findViewById(R.id.editPhoneButton).setOnClickListener(v -> editPhoneNumber());

        // Logout button click listener
        view.findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }

    private void loadUserData() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        progressBar.setVisibility(View.GONE);

                        if (documentSnapshot.exists()) {
                            // Set user data to views
                            nameTextView.setText(documentSnapshot.getString("name"));
                            emailTextView.setText(documentSnapshot.getString("email"));
                            studentIdTextView.setText(documentSnapshot.getString("studentId"));
                            phoneTextView.setText(documentSnapshot.getString("phone"));
                            departmentTextView.setText(documentSnapshot.getString("department"));
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Error loading user data", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    // NEW: Edit Email Method
    private void editEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Email Address");

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setText(emailTextView.getText().toString());
        input.setSelection(input.getText().length());
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newEmail = input.getText().toString().trim();
            if (!newEmail.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                updateEmail(newEmail);
            } else {
                Toast.makeText(getActivity(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // NEW: Update Email in Firebase Auth AND Firestore
    private void updateEmail(String newEmail) {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Step 1: Firebase Authentication e email update
            currentUser.verifyBeforeUpdateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Step 2: Firestore e email update
                            String userId = currentUser.getUid();
                            db.collection("users").document(userId)
                                    .update("email", newEmail)
                                    .addOnSuccessListener(aVoid -> {
                                        progressBar.setVisibility(View.GONE);
                                        emailTextView.setText(newEmail);
                                        Toast.makeText(getActivity(),
                                                "Verification email sent. Please verify your new email address.",
                                                Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(),
                                                "Error updating email in database: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(),
                                    "Error updating email: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    // Edit Phone Number Method
    private void editPhoneNumber() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Phone Number");

        // Input field create kora
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setText(phoneTextView.getText().toString());
        input.setSelection(input.getText().length());
        builder.setView(input);

        // Buttons add kora
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newPhone = input.getText().toString().trim();
            if (!newPhone.isEmpty()) {
                updatePhoneNumber(newPhone);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Update Phone Number in Firebase
    private void updatePhoneNumber(String newPhone) {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId)
                    .update("phone", newPhone)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        phoneTextView.setText(newPhone);
                        Toast.makeText(getActivity(), "Phone number updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Error updating phone number", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
}
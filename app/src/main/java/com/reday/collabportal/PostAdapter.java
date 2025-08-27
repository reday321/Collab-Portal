package com.reday.collabportal;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private String currentUserId;
    private FirebaseFirestore db;

    public PostAdapter(Context context, List<Post> postList, String currentUserId) {
        this.context = context;
        this.postList = postList;
        this.currentUserId = currentUserId;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.userNameTextView.setText(post.getUserName());
        holder.contentTextView.setText(post.getContent());

        // Show edit/delete buttons only if current user is the post owner
        if (post.getUserId().equals(currentUserId)) {
            holder.editDeleteLayout.setVisibility(View.VISIBLE);
        } else {
            holder.editDeleteLayout.setVisibility(View.GONE);
        }

        // Edit button click listener
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPost(post);
            }
        });

        // Delete button click listener
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost(post);
            }
        });

        // Format timestamp
        if (post.getTimestamp() != null) {
            String timeAgo = getTimeAgo(post.getTimestamp());
            holder.timestampTextView.setText(timeAgo);
        }

        holder.lovesTextView.setText(String.valueOf(post.getLoves()));

        // Set heart icon and tint based on loved state
        int colorPrimary = ContextCompat.getColor(context, R.color.md_primary);
        int colorOnSurface = ContextCompat.getColor(context, R.color.text_secondary);
        holder.loveButton.setImageResource(R.drawable.ic_heart);
        if (post.getLovedBy() != null && post.getLovedBy().contains(currentUserId)) {
            ImageViewCompat.setImageTintList(holder.loveButton, android.content.res.ColorStateList.valueOf(colorPrimary));
        } else {
            ImageViewCompat.setImageTintList(holder.loveButton, android.content.res.ColorStateList.valueOf(colorOnSurface));
        }

        // Love button click listener
        holder.loveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLove(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, contentTextView, timestampTextView, lovesTextView;
        ImageButton loveButton, editButton, deleteButton;
        LinearLayout editDeleteLayout;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            lovesTextView = itemView.findViewById(R.id.lovesTextView);
            loveButton = itemView.findViewById(R.id.loveButton);

            // Edit/Delete buttons
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editDeleteLayout = itemView.findViewById(R.id.editDeleteLayout);
        }
    }

    private void toggleLove(Post post) {
        DocumentReference postRef = db.collection("posts").document(post.getPostId());

        // Check if user already loved this post
        if (post.getLovedBy() != null && post.getLovedBy().contains(currentUserId)) {
            // User already loved, so remove the love
            postRef.update(
                    "loves", FieldValue.increment(-1),
                    "lovedBy", FieldValue.arrayRemove(currentUserId)
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Update successful
                    post.setLoves(post.getLoves() - 1);
                    post.getLovedBy().remove(currentUserId);
                    notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // User hasn't loved yet, so add love
            postRef.update(
                    "loves", FieldValue.increment(1),
                    "lovedBy", FieldValue.arrayUnion(currentUserId)
            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Update successful
                    post.setLoves(post.getLoves() + 1);
                    if (post.getLovedBy() != null) {
                        post.getLovedBy().add(currentUserId);
                    }
                    notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getTimeAgo(Date date) {
        long time = date.getTime();
        long now = System.currentTimeMillis();
        final long diff = now - time;

        if (diff < 1000) {
            return "just now";
        } else if (diff < 60 * 1000) {
            return diff / 1000 + "s ago";
        } else if (diff < 60 * 60 * 1000) {
            return diff / (60 * 1000) + "m ago";
        } else if (diff < 24 * 60 * 60 * 1000) {
            return diff / (60 * 60 * 1000) + "h ago";
        } else {
            return new SimpleDateFormat("MMM d", Locale.getDefault()).format(date);
        }
    }

    // ✅ NEW: Edit Post Method
    private void editPost(Post post) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Edit Post");

        final android.widget.EditText input = new android.widget.EditText(context);
        input.setText(post.getContent());
        input.setSelection(input.getText().length());
        builder.setView(input);

        builder.setPositiveButton("Save", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                String newContent = input.getText().toString().trim();
                if (!newContent.isEmpty()) {
                    updatePostContent(post, newContent);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // ✅ NEW: Update Post in Firebase
    private void updatePostContent(Post post, String newContent) {
        DocumentReference postRef = db.collection("posts").document(post.getPostId());

        postRef.update("content", newContent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        post.setContent(newContent);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Post updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error updating post", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ✅ NEW: Delete Post Method
    private void deletePost(Post post) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Delete Post");
        builder.setMessage("Are you sure you want to delete this post?");

        builder.setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                // ✅ FIRST: Find the current position in the list
                int position = postList.indexOf(post);
                if (position == -1) {
                    Toast.makeText(context, "Post not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ SECOND: Remove from local list FIRST
                postList.remove(position);
                notifyItemRemoved(position); // ✅ Notify adapter about removal

                // ✅ THIRD: Delete from Firebase
                DocumentReference postRef = db.collection("posts").document(post.getPostId());
                postRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // ✅ UNDO: Jodi Firebase e delete fail hoy, tahole abar list e add koro
                                postList.add(position, post);
                                notifyItemInserted(position);
                                Toast.makeText(context, "Error deleting post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
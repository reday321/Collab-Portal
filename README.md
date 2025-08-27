git clone <repo-url>
# Collab Portal

A lightweight Android social/collaboration app for sharing short posts within a team or community.

---

## Summary
Collab Portal is an Android app (Java) where users can create short posts, read others' posts, and react with a "love". The project uses AndroidX, Material Components, and Firebase (Auth + Firestore). This README explains how to set up, build, and where to find the main code areas.

---

## Key Features
- User authentication (Firebase Auth)
- Create and view posts
- Toggle "love" (like) on posts (Firestore-backed)
- Edit / Delete option for post owners
- Splash screen and app themes handled

---

## Tech stack
- Language: Java
- Build: Gradle (Kotlin DSL)
- UI: AndroidX + Material Components
- Backend: Firebase Firestore & Auth

---

## Prerequisites
- JDK 11 or newer
- Android SDK (Android Studio recommended)
- Platform-tools (adb)
- Internet access for Firebase

---

## Quick start
1. Clone the repo:

```powershell
# Windows PowerShell

cd "Collab Portal"
```

2. Open the project in Android Studio or build from CLI:

```powershell
./gradlew assembleDebug --no-daemon
```

3. Install the debug APK on an emulator or device:

```powershell
adb install -r "app\build\outputs\apk\debug\app-debug.apk"
```

4. Launch the app (example):

```powershell
adb shell am start -n com.reday.collabportal/.LoginActivity
```

---

## Important files
- `app/src/main/java/com/reday/collabportal/LoginActivity.java` — splash + login flow
- `app/src/main/java/com/reday/collabportal/MainActivity.java` — main activity, navigation, FAB handling
- `app/src/main/java/com/reday/collabportal/PostAdapter.java` — RecyclerView adapter (bind post data, love toggle, edit/delete handlers)
- `app/src/main/java/com/reday/collabportal/Post.java` — Post model
- `app/src/main/res/layout/item_post.xml` — post item layout (love icon, edit/delete buttons)
- `app/src/main/res/layout/activity_home.xml` — home activity layout

---

## Recent fixes / maintainer notes
- Fixed ambiguous call to the SplashScreen API by casting to `ComponentActivity`.
- Removed problematic drawables that caused resource merge errors.
- Resolved a runtime ClassCastException in `MainActivity` by matching view types to layout widgets.
- `PostAdapter` now safely initializes `lovedBy` when null and uses `notifyItemChanged(idx)` for targeted UI updates.
- Removed the posts header and its button from the activity layout and cleaned up corresponding code in `MainActivity`.
- Removed the navigation (drawer) icon from the toolbar.

---

## Implementing Edit / Delete for posts (guide)
Below are concise steps and code guidance to enable edit/delete for post owners.

1) UI (`item_post.xml`)
   - Add two prominent `ImageButton`s for Edit and Delete with ids `@id/btnEdit` and `@id/btnDelete`.
   - Use clear icons and a visible tint/background (36dp size, colored tint).

2) Adapter (`PostAdapter.java`)
   - In `onBindViewHolder`, show edit/delete only when `post.getUserId().equals(currentUserId)`.
   - Delete handler example:

```java
DocumentReference postRef = db.collection("posts").document(post.getPostId());
postRef.delete().addOnSuccessListener(v -> {
    int idx = postList.indexOf(post);
    if (idx >= 0) {
        postList.remove(idx);
        notifyItemRemoved(idx);
    }
}).addOnFailureListener(e -> {
    Toast.makeText(context, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
});
```

   - Edit handler suggestion: open a dialog or `EditPostFragment` pre-filled with `post.getContent()`, call `postRef.update("content", newContent)` on submit, then update model and call `notifyItemChanged(idx)`.

3) Firestore security rules
   - Enforce that only the post owner can update/delete:

```
allow update, delete: if request.auth.uid == resource.data.userId;
```

4) UX
   - Confirm deletion with a dialog and show progress indicators while network operations are in progress.

---

## Troubleshooting
- If build fails: `./gradlew clean` then `./gradlew assembleDebug --no-daemon` and check `build/reports/problems`.
- If icons render as blocks: ensure vector drawables are supported or use PNGs; check `vectorDrawables.useSupportLibrary` if needed.
- If a ClassCastException occurs, check that Java field types match layout widgets.
- Ensure `google-services.json` exists locally for Firebase functionality.

---

## Contributing
- Fork the repo, create a branch, and open a PR with a clear description. Include screenshots for UI changes and tests where applicable.

---

## License & contact
- Add a `LICENSE` file at the repo root if needed.
- For questions or issues, open an issue in the repository.

---

If you want, I can implement the Edit/Delete UI and adapter handlers now and run a local build/test—say “Implement edit/delete” and I will proceed.


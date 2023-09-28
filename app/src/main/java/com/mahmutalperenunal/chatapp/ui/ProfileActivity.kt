package com.mahmutalperenunal.chatapp.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.MenuItemCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.databinding.ActivityProfileBinding
import com.mahmutalperenunal.chatapp.model.User

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null
    private var storageReference: StorageReference? = null
    private var imageUri: Uri? = null
    private var coverChecker: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.settingsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //set action bar title
        supportActionBar!!.title = "Profile"

        //initialize
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageReference = FirebaseStorage.getInstance().reference.child("User Images")

        setUserProfilePicture()

        binding.profileEditButton.setOnClickListener { editProfileAlertDialog() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.app_bar_logout -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(
                    applicationContext,
                    WelcomeActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setUserProfilePicture() {
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: User? = snapshot.getValue(User::class.java)

                    //set data
                    binding.profileUsernameTextView.text = user!!.username
                    Glide.with(applicationContext).load(user.profile).centerCrop()
                        .placeholder(R.drawable.profile_image).into(binding.profilePpImageView)
                    Glide.with(applicationContext).load(user.cover).centerCrop()
                        .placeholder(R.drawable.cover_image).into(binding.profileCoverImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    "Error Message: " + error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun editProfileAlertDialog() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Edit Profile")
            .setMessage("Select the section you want to edit on your profile.")
            .setPositiveButton("Change Profile Picture") { dialog, _ ->
                coverChecker = "Profile"
                editProfileAndCoverPicture()
                dialog.dismiss()
            }
            .setNegativeButton("Change Cover Picture") { dialog, _ ->
                coverChecker = "Cover"
                editProfileAndCoverPicture()
                dialog.dismiss()
            }
            .setNeutralButton("Change Username") { dialog, _ ->
                editUsername()
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun editUsername() {
        val editText = EditText(applicationContext)
        editText.hint = "Username"

        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(editText)
            .setTitle("Edit Username")
            .setMessage("Change your username")
            .setPositiveButton("Change") { dialog, _ ->
                val newUsername = editText.text.toString()
                if (newUsername.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Username cannot be blank!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    saveUsername(newUsername)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun saveUsername(username: String) {
        val map = HashMap<String, Any>()
        map["username"] = username
        refUsers!!.updateChildren(map).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    "Username updated successfully.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun editProfileAndCoverPicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 438)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUri = data.data
            Toast.makeText(applicationContext, "Uploading...", Toast.LENGTH_SHORT).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(applicationContext)
        progressBar.setMessage("The image is uploading to database, please wait...")
        progressBar.show()

        if (imageUri != null) {
            val fileRef = storageReference!!.child(System.currentTimeMillis().toString() + ".jpg")

            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "Cover") {
                        val map = HashMap<String, Any>()
                        map["cover"] = url
                        refUsers!!.updateChildren(map)
                        coverChecker = ""
                    } else {
                        val map = HashMap<String, Any>()
                        map["profile"] = url
                        refUsers!!.updateChildren(map)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }
}
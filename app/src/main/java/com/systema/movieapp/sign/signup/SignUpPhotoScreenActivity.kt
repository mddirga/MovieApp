package com.systema.movieapp.sign.signup

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.systema.movieapp.home.HomeActivity
import com.systema.movieapp.R
import com.systema.movieapp.utils.Preferences
import kotlinx.android.synthetic.main.activity_sign_up_photo_screen.*
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SignUpPhotoScreenActivity : AppCompatActivity(), PermissionListener {

    private var statusAdd: Boolean = false
    private lateinit var filePath: Uri

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var mFirebaseDatabase: DatabaseReference

    private lateinit var preferences: Preferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_photo_screen)

        preferences = Preferences(this)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("User")

        `@+id/tv_username`.text = "Selamat Datang, \n" + intent.getStringExtra("nama")

        //start - kondisi ketika button add foto di tekan
        iv_add.setOnClickListener {
            if (statusAdd){
                statusAdd = false
                btn_save.visibility = View.INVISIBLE
                iv_add.setImageResource(R.drawable.ic_btn_upload)
                iv_profile.setImageResource(R.drawable.user_pic)
            } else {
                ImagePicker.with(this)
                    .cameraOnly()
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start()
            }
        }
        //end - kondisi ketika button add foto di tekan

        //start - kondisi ketika button upload foto di tekan
        btn_upload.setOnClickListener {
            finishAffinity()
            val intent = Intent(
                this@SignUpPhotoScreenActivity,
                HomeActivity::class.java
            )
            startActivity(intent)
        }
        //end - kondisi ketika button upload foto di tekan

        btn_save.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            Log.v("tamvan", "file uri upload 2$filePath")

            val ref = storageReference.child("image/" + UUID.randomUUID().toString())
            ref.putFile(filePath)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@SignUpPhotoScreenActivity,
                        "Uploaded",
                        Toast.LENGTH_SHORT
                    ).show()

                    ref.downloadUrl.addOnSuccessListener {
                        preferences.setValues("url", it.toString())

                        //start - transfer url to database
                        mFirebaseDatabase.child(intent.getStringExtra("username")).child("url").setValue(it.toString())

                        finishAffinity()
                        val intent = Intent(
                            this@SignUpPhotoScreenActivity,
                            HomeActivity::class.java
                        )
                        startActivity(intent)
                    }
                }

                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@SignUpPhotoScreenActivity,
                        "Failed" + e.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                        .totalByteCount
                    progressDialog.setMessage("Uploaded" + progress.toInt() + "%")
                }

        }
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        ImagePicker.with(this)
            .cameraOnly()
            .crop()
            .compress(1024)
            .maxResultSize(1080,1080)
            .start()
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest?,
        token: PermissionToken?
    ) {

    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(this, "Kamu tidak bisa menambahkan photo profile", Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Tergesa? Klik tombol Upload Nanti aja", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                statusAdd = true
                filePath = data?.data!!

                Glide.with(this)
                    .load(filePath)
                    .apply(RequestOptions.circleCropTransform())
                    .into(iv_profile)

                btn_save.visibility = View.VISIBLE
                iv_add.setImageResource(R.drawable.ic_btn_delete)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

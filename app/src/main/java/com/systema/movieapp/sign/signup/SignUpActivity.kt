package com.systema.movieapp.sign.signup

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import com.systema.movieapp.R
import com.systema.movieapp.sign.signin.SignInActivity
import com.systema.movieapp.sign.signin.User
import com.systema.movieapp.utils.Preferences
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var sUsername: String
    private lateinit var sPassword: String
    private lateinit var sRetypePassword: String
    private lateinit var sNama: String
    private lateinit var sEmail: String

    private lateinit var mFirebaseInstance: FirebaseDatabase
    private lateinit var mFirebaseDatabase: DatabaseReference
    private lateinit var mDatabase: DatabaseReference

    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mFirebaseInstance = FirebaseDatabase.getInstance()
        mFirebaseDatabase = mFirebaseInstance.getReference("User")
        mDatabase = FirebaseDatabase.getInstance().reference

        iv_back.setOnClickListener {
            val intent = Intent(
                this@SignUpActivity,
                SignInActivity::class.java
            )
            startActivity(intent)
        }

        preferences = Preferences(this)

        btn_daftar.setOnClickListener {
            sUsername = et_username.text.toString()
            sPassword = et_password.text.toString()
            sRetypePassword = et_retype_password.text.toString()
            sNama = et_nama.text.toString()
            sEmail = et_email.text.toString()

            when {
                sUsername == "" -> {
                    et_username.error = "Silahkan isi Username"
                    et_username.requestFocus()
                }
                sPassword == "" -> {
                    et_password.error = "Silahkan isi Password"
                    et_password.requestFocus()
                }
                sRetypePassword == "" -> {
                    et_retype_password.error = "Silahkan ulangi Password"
                    et_retype_password.requestFocus()
                }
                sNama == "" -> {
                    et_nama.error = "Silahkan isi Nama"
                    et_nama.requestFocus()
                }
                sEmail == "" -> {
                    et_email.error = "Silahkan isi Email"
                    et_email.requestFocus()
                }
                sPassword != sRetypePassword -> {
                    et_retype_password.error = "Password tidak sama"
                    et_retype_password.requestFocus()
                }
                else -> {
                    saveUser(sUsername, sPassword, sNama, sEmail)
                }
            }
        }
    }

    private fun saveUser(sUsername: String, sPassword: String, sNama: String, sEmail: String) {

        val user = User()
        user.email = sEmail
        user.username = sUsername
        user.nama = sNama
        user.password = sPassword
        user.saldo = "100000"

        checkingUsername(sUsername, user)
    }

    private fun checkingUsername(iUsername: String, data: User) {
        mFirebaseDatabase.child(iUsername).addValueEventListener(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val user = dataSnapshot.getValue(User::class.java)
                if (user == null){
                    mFirebaseDatabase.child(iUsername).setValue(data) //menyimpan ke database yang sudah diedit di preferences

                    preferences.setValues("nama", data.nama.toString())
                    preferences.setValues("username", data.username.toString())
                    preferences.setValues("password", data.password.toString())
                    preferences.setValues("saldo", data.saldo.toString())
                    preferences.setValues("email", data.email.toString())

                    preferences.setValues("status", "1")

                    val intent = Intent(
                        this@SignUpActivity,
                        SignUpPhotoScreenActivity::class.java
                    ).putExtra("nama", data.nama)
                        .putExtra("username", data.username)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignUpActivity, "User sudah digunakan", Toast.LENGTH_LONG)
                        .show()
                }
            }

            @SuppressLint("ShowToast")
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignUpActivity, "" + error.message, Toast.LENGTH_LONG)
            }
        })
    }
}

package com.systema.movieapp.sign.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import com.systema.movieapp.home.HomeActivity
import com.systema.movieapp.R
import com.systema.movieapp.sign.signup.SignUpActivity
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var iUsername: String
    private lateinit var iPassword: String

    private lateinit var mDatabase: DatabaseReference
    private lateinit var preferences: com.systema.movieapp.utils.Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mDatabase = FirebaseDatabase.getInstance().getReference("User")
        preferences = com.systema.movieapp.utils.Preferences(this)

        preferences.setValues("onboarding", "1")

        //Cek Username dan Password Ketika Tombol Login Ditekan
        btn_login.setOnClickListener {
            iUsername = et_username.text.toString()
            iPassword = et_password.text.toString()

            when {
                iUsername == "" -> {
                    et_username.error = "Username tidak boleh kosong"
                    et_username.requestFocus()
                }
                iPassword == "" -> {
                    et_password.error = "Password tidak boleh kosong"
                    et_password.requestFocus()
                }
                else -> {
                    pushLogin(iUsername, iPassword) //method untuk login
                }
            }
        }

        //pengecekan username dan password ketika tombol login di tekan
        btn_daftar.setOnClickListener {
            val intent = Intent(
                this@SignInActivity, SignUpActivity::class.java
            )
            startActivity(intent)
        }

        if (preferences.getValues("status").equals("1")) {
            finishAffinity()
            val intent = Intent(
                this@SignInActivity, HomeActivity::class.java
            )
            startActivity(intent)
        }
    }

    // start - method login, mengambil data user
    private fun pushLogin(iUsername: String, iPassword: String) {
        mDatabase.child(iUsername).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val user = dataSnapshot.getValue(User::class.java)

                if (user == null) {
                    Toast.makeText(this@SignInActivity, "Username tidak ditemukan", Toast.LENGTH_LONG)
                        .show()
                } else {
                    if (user.password.equals(iPassword)) {
                        preferences.setValues("nama", user.nama.toString())
                        preferences.setValues("username", user.username.toString())
                        preferences.setValues("password", user.password.toString())
                        preferences.setValues("url", user.url.toString())
                        preferences.setValues("email", user.email.toString())
                        preferences.setValues("saldo", user.saldo.toString())

                        preferences.setValues("status", "1") //set status preferences

                        val nama = preferences.getValues("nama")
                        Toast.makeText(this@SignInActivity, "Selamat Datang '$nama'", Toast.LENGTH_LONG)
                            .show()

                        finishAffinity()

                        val intent = Intent(
                            this@SignInActivity,
                            HomeActivity::class.java
                        )
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@SignInActivity, "Password Anda Salah", Toast.LENGTH_LONG
                        ). show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignInActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }
        })
    }
    //end - method login, mengambil data user
}
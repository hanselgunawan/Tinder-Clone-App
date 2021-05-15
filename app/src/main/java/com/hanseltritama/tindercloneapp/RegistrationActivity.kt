package com.hanseltritama.tindercloneapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        setupFirebase()
        setupUI()

    }

    private fun setupFirebase() {
        mAuth = FirebaseAuth.getInstance()
        firebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            } else {
                Toast.makeText(this, "User does not exist!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUI() {
        register_submit_button.setOnClickListener {
            val selectId: Int = radio_group.checkedRadioButtonId
            val radioButton: RadioButton = findViewById(selectId)
            radioButton.text ?: return@setOnClickListener

            val email: String = email_field.text.toString()
            val password: String = password_field.text.toString()
            val name: String = name_field.text.toString()

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this, "Sign Up Error", Toast.LENGTH_SHORT).show()
                } else {
                    val userId: String? = mAuth.currentUser?.uid
                    val currentUserDb: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("Users")
                        .child(radioButton.text.toString())
                        .child(userId.toString())
                        .child("name")

                    currentUserDb.setValue(name)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // start listening
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        // stop listening
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }
}
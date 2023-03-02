package com.opdeveloper.pedidos.signup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.opdeveloper.pedidos.MainActivity

import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.home.HomeActivity
import kotlinx.android.synthetic.main.activity_sign_up.*



class SignUpActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_up)


        auth = FirebaseAuth.getInstance()

        btnSignRegist.setOnClickListener {
            val email = edtEmailUp.text.toString()
            val password = edtPasswordUp.text.toString()
            createAccount(email, password)

        }
    }


    private fun validateForm(): Boolean {
        var valid = true

        val email = edtEmailUp.text.toString()
        if (TextUtils.isEmpty(email)) {
            edtEmailUp.error = "Required."
            valid = false
        } else {
            edtEmailUp.error = null
        }

        val password = edtPasswordUp.text.toString()
        if (TextUtils.isEmpty(password)) {
            edtPasswordUp.error = "Required."
            valid = false
        } else {
            edtPasswordUp.error = null
        }

        return valid
    }

    private fun createAccount(email: String, password: String) {

        if (!validateForm()) {
            return
        }

        loading.visibility = View.VISIBLE

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = auth.currentUser
                    // Create a new user with a first and last name
                    val userdb = hashMapOf(
                        "email" to user!!.email,
                        "displayName" to user.displayName,
                        "uid" to user.uid,
                        "photoUrl" to user.photoUrl
                    )

// Add a new document with a generated ID
                    db.collection("users")
                        .add(userdb)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this@SignUpActivity, "User Created", Toast.LENGTH_LONG).show()
                            val id = documentReference.id
                            documentReference.set("id" to id)

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@SignUpActivity, "User Create failure", Toast.LENGTH_LONG).show()
                        }
                    val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }

                // [START_EXCLUDE]
                loading.visibility = View.GONE
                // [END_EXCLUDE]
            }
        // [END create_user_with_email]
    }

}



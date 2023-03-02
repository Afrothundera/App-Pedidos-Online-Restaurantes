package com.opdeveloper.pedidos.signin

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth
import com.opdeveloper.pedidos.MainActivity
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.home.HomeActivity

import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.content_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()

        btnSignRegist.setOnClickListener {
            val email = edtEmailIn.text.toString()
            val pass = edtPasswordIn.text.toString()
            signIn(email, pass)
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = edtEmailIn.text.toString()
        if (TextUtils.isEmpty(email)) {
            edtEmailIn.error = "Required."
            valid = false
        } else {
            edtEmailIn.error = null
        }

        val password = edtPasswordIn.text.toString()
        if (TextUtils.isEmpty(password)) {
            edtPasswordIn.error = "Required."
            valid = false
        } else {
            edtPasswordIn.error = null
        }

        return valid
    }

    private fun signIn(email: String, password: String) {

        if (!validateForm()) {
            return
        }

        loadingSignIn.visibility = View.VISIBLE

        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    val user = auth.currentUser
                    val intent = Intent(this@SignInActivity, HomeActivity::class.java)
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

                loadingSignIn.visibility = View.GONE
                // [END_EXCLUDE]
            }
        // [END sign_in_with_email]
    }

}

package com.opdeveloper.pedidos.welcome

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.opdeveloper.pedidos.MainActivity
import com.opdeveloper.pedidos.R
import com.opdeveloper.pedidos.home.HomeActivity
import com.opdeveloper.pedidos.signin.SignInActivity
import com.opdeveloper.pedidos.signup.SignUpActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        auth = FirebaseAuth.getInstance()
        val face : Typeface = Typeface.createFromAsset(assets, "fonts/NABILA.TTF")

        txtSlogan.typeface = face

        btnSignUp.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        btnSignIn.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null){
            val intent = Intent(this@WelcomeActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        /*if (currentUser!!.email != null) {
            val intent = Intent(this@WelcomeActivity, SignInActivity::class.java)
            startActivity(intent)
        }*/
    }
}

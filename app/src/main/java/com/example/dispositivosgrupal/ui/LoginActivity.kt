package com.example.dispositivosgrupal.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.dispositivosgrupal.R
import com.example.dispositivosgrupal.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        setup()
        session()

    }

    override fun onStart() {
        super.onStart()
        binding.authLayout.visibility = View.VISIBLE
    }

    private fun session() {

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)

        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null) {
            binding.authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider))
        }

    }

    private fun setup() {


        binding.btRegistro.setOnClickListener {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                binding.editxtCorreo.text.toString(),
                binding.editxtContrasena.text.toString()
            ).addOnCompleteListener(this) {

                if (it.isSuccessful) {
                    showHome(it.result?.user?.email ?: "", ProviderType.BASIC)

                } else {

                    showAlect()
                }
            }
        }

        binding.btIngreso.setOnClickListener {

            if (binding.editxtCorreo.text.isNotEmpty() && binding.editxtContrasena.text.isNotEmpty()) {

                auth.signInWithEmailAndPassword(
                    binding.editxtCorreo.text.toString(),
                    binding.editxtContrasena.text.toString()
                ).addOnCompleteListener {

                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)

                    } else {

                        showAlect()
                    }

                }
            }

        }

        binding.btGoogle.setOnClickListener {

            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            // Manejar la respuesta de One Tap aquí
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(account.email ?: "", ProviderType.GOOGLE)
                            } else {
                                showAlect()
                            }
                        }
                }

            } catch (e: ApiException) {
                showAlect()
            }
        }
    }


    private fun showAlect() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType) {

        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

}
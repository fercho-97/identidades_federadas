package com.example.dispositivosgrupal.ui


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dispositivosgrupal.R

import com.example.dispositivosgrupal.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


enum class ProviderType {
    BASIC,
    GOOGLE,
    GITHUB
}

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setUp(email?:"",provider?:"")

        //Guardar Datos

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun setUp(email: String, provider: String) {
        title = "Inicio"

        binding.editxtCorreo.text = email
        binding.editxtType.text = provider


        binding.btIngreso.setOnClickListener{
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }


}
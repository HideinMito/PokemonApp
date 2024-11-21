package com.example.pokemontypesapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import java.util.Calendar

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Vinculando las vistas
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etBirthDate = findViewById(R.id.etBirthDate)
        btnLogin = findViewById(R.id.btnLogin)

        // Configurar el DatePicker al hacer clic en el campo de fecha
        etBirthDate.setOnClickListener {
            showDatePicker()
        }

        // Configurar el botón de inicio de sesión
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            val birthDate = etBirthDate.text.toString()

            // Validar los campos
            if (username.isEmpty() || password.isEmpty() || birthDate.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Si la validación es exitosa, continuar a la MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Opcional: cerrar la actividad de Login después de iniciar sesión
            }
        }
    }

    private fun showDatePicker() {
        // Obtener la fecha actual
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Crear el DatePickerDialog
        val picker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Formatear la fecha seleccionada y mostrarla en el EditText
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            etBirthDate.setText(formattedDate)
        }, year, month, day)

        // Establecer la fecha máxima permitida (hoy)
        picker.datePicker.maxDate = c.timeInMillis

        // Mostrar el DatePickerDialog
        picker.show()
    }
}

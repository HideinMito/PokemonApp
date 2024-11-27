package com.example.pokemontypesapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var etBirthDate: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Vinculando las vistas
        etBirthDate = findViewById(R.id.etBirthDate)
        btnLogin = findViewById(R.id.btnLogin)

        // Configurar el DatePicker al hacer clic en el campo de fecha
        etBirthDate.setOnClickListener {
            showDatePicker()
        }

        // Configurar el botón de inicio de sesión
        btnLogin.setOnClickListener {
            val birthDate = etBirthDate.text.toString()

            // Validar que la fecha de nacimiento no esté vacía
            if (birthDate.isEmpty()) {
                Toast.makeText(this, "Por favor, completa el campo de fecha de nacimiento", Toast.LENGTH_SHORT).show()
            } else {
                // Validar que la persona tiene al menos 18 años
                if (isAdult(birthDate)) {
                    // Si la persona tiene 18 años o más, continuar a la MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Opcional: cerrar la actividad de Login después de iniciar sesión
                } else {
                    // Si la persona no tiene 18 años, mostrar un mensaje de error
                    Toast.makeText(this, "Debes ser mayor de 18 años para acceder a la aplicación", Toast.LENGTH_SHORT).show()
                }
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

    private fun isAdult(birthDate: String): Boolean {
        val c = Calendar.getInstance()
        val today = c.timeInMillis

        // Convertir la fecha de nacimiento en un objeto Calendar
        val birthCalendar = Calendar.getInstance()
        val parts = birthDate.split("/")
        val birthDay = parts[0].toInt()
        val birthMonth = parts[1].toInt() - 1
        val birthYear = parts[2].toInt()

        birthCalendar.set(birthYear, birthMonth, birthDay)

        // Calcular la diferencia de años entre la fecha actual y la fecha de nacimiento
        val age = (today - birthCalendar.timeInMillis) / (365.25 * 24 * 60 * 60 * 1000)

        // Verificar si la edad es mayor o igual a 18 años
        return age >= 18
    }
}

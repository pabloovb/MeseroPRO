// src/main/java/com/example/meseropro/ui/LoginActivity.java
package com.example.meseropro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meseropro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etPassword;
    private Button btnLogin;
    private FirebaseAuth      firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreo   = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore    = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> {
            String correo   = etCorreo.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(correo, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                fetchUserRoleAndRedirect(user.getUid());
                            }
                        } else {
                            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void fetchUserRoleAndRedirect(String uid) {
        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String role = doc.getString("role");
                        switch (role != null ? role : "") {
                            case "camarero":
                                startActivity(new Intent(this, CamareroActivity.class));
                                break;
                            case "cocina":
                                startActivity(new Intent(this, CocinaActivity.class));
                                break;
                            case "barra":
                                startActivity(new Intent(this, BarraActivity.class));
                                break;
                            case "admin":
                                startActivity(new Intent(this, AdminActivity.class));
                                break;
                            default:
                                Toast.makeText(this,
                                        "Rol desconocido: " + role,
                                        Toast.LENGTH_LONG).show();
                                return;
                        }
                        finish();
                    } else {
                        Toast.makeText(this,
                                "No se encontró perfil de usuario",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Error al leer rol: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}

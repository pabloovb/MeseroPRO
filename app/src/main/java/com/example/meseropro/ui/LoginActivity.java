package com.example.meseropro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meseropro.R;
import com.example.meseropro.model.Usuario;
import com.example.meseropro.network.APIClient;
import com.example.meseropro.network.UsuarioService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            UsuarioService service = APIClient.getClient().create(UsuarioService.class);
            Call<List<Usuario>> call = service.loginUsuario(correo, password);

            call.enqueue(new Callback<List<Usuario>>() {
                @Override
                public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        Usuario user = response.body().get(0);
                        redirigirSegunRol(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Usuario>> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void redirigirSegunRol(Usuario user) {
        String rol = user.getRol();
        Intent intent;

        switch (rol) {
            case "camarero":
                intent = new Intent(this, CamareroActivity.class);
                break;
            case "cocina":
                intent = new Intent(this, CocinaActivity.class);
                break;
            case "barra":
                intent = new Intent(this, BarraActivity.class);
                break;
            case "admin":
                intent = new Intent(this, AdminActivity.class);
                break;
            default:
                Toast.makeText(this, "Rol no reconocido", Toast.LENGTH_SHORT).show();
                return;
        }

        intent.putExtra("usuario", user.getNombre());
        startActivity(intent);
        finish();
    }
}


package com.example.meseropro.ui;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.meseropro.R;

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Configura el Toolbar si existe en el layout.
     * @param toolbarResId id del Toolbar en el layout
     */
    protected void setupToolbar(int toolbarResId) {
        View view = findViewById(toolbarResId);
        if (view instanceof Toolbar) {
            Toolbar toolbar = (Toolbar) view;
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_active_pedidos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.action_view_list) {
            startActivity(new Intent(this, ActivePedidosActivity.class));
            return true;
        }
        if (id == R.id.action_notifications) {
            startActivity(new Intent(this, NotificacionesActivity.class));
            return true;
        }
        if (id == R.id.action_settings) {
            // TODO: lanzar SettingsActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

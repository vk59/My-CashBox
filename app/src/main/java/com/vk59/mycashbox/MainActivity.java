package com.vk59.mycashbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonStartScan).setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, ScanActivity.class);
            startActivity(i);
        });

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        assert manager != null;
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        width = point.x;
        TextView panelTop = findViewById(R.id.panelTop);
        panelTop.getLayoutParams().width = width * 3 / 5;
    }
}

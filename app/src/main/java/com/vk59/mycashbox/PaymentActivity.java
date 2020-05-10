package com.vk59.mycashbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static com.vk59.mycashbox.ScanActivity.products;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "ERROR QR";
    private String codesProducts = getStringOfCodes();
    private ImageView qrImage = findViewById(R.id.qrImage);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initQRGEncoder();
        findViewById(R.id.buttonEnd).setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
            products.clear();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        findViewById(R.id.buttonBackToBasket).setOnClickListener(v -> finish());

        TextView panelTop = findViewById(R.id.panelTopPayment);
        panelTop.getLayoutParams().width = MainActivity.width * 3 / 5;
    }

    private void initQRGEncoder() {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        assert manager != null;
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = Math.min(width, height);
        QRGEncoder qrgEncoder = new QRGEncoder(codesProducts, null,
                QRGContents.Type.TEXT, smallerDimension );
        qrgEncoder.setColorBlack(Color.BLACK);
        qrgEncoder.setColorWhite(Color.WHITE);
        try {
            // Getting QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
    }

    private String getStringOfCodes() {
        StringBuilder s = new StringBuilder();
        for (String code : products) {
            s.append(code).append(" ");
        }
        return s.toString();
    }
}

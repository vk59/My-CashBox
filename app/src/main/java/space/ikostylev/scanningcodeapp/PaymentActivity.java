package space.ikostylev.scanningcodeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static space.ikostylev.scanningcodeapp.ScanActivity.products;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "ERROR QR";

//    public static final String PAYMENT_TYPE = "Payment type"; // QR, Voice

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
//        Intent i = getIntent();
//        String paymentType = i.getStringExtra(PAYMENT_TYPE);

//        String jsonProducts = convertProductsToJson(products);
        String codesProducts = getStringOfCodes();
        Bitmap bitmap;
        ImageView qrImage = findViewById(R.id.qrImage);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;

        QRGEncoder qrgEncoder = new QRGEncoder(codesProducts, null, QRGContents.Type.TEXT, smallerDimension );
        qrgEncoder.setColorBlack(Color.BLACK);
        qrgEncoder.setColorWhite(Color.WHITE);
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }

        findViewById(R.id.btnEnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                products.clear();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnBackToBasket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView panelTop = findViewById(R.id.panelTop_payment);
        panelTop.getLayoutParams().width = MainActivity.width * 3 / 5;
    }

    private String getStringOfCodes() {
        String s = "";
        for (String code : products) {
            s += code + " ";
        }
        return s;
    }

    private String convertProductsToJson(ArrayList<Product> products) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String currentProductsJson = gson.toJson(products);
        return currentProductsJson;
    }
}

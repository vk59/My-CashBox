package space.ikostylev.scanningcodeapp;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static space.ikostylev.scanningcodeapp.MainActivity.products;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "ERROR QR";

    public static final String PAYMENT_TYPE = "Payment type"; // QR, Voice

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        String jsonProducts = convertProductsToJson(products);
        Bitmap bitmap;
        ImageView qrImage = findViewById(R.id.qrImage);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;

        QRGEncoder qrgEncoder = new QRGEncoder(jsonProducts, null, QRGContents.Type.TEXT, smallerDimension );
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
    }

    private String convertProductsToJson(ArrayList<Product> products) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String currentProductsJson = gson.toJson(products);
        return currentProductsJson;
    }
}

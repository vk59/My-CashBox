package com.vk59.mycashbox;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends AppCompatActivity {
    CameraView cameraView;
    boolean isDetected = true;
    Button buttonStart;
    ImageView buttonToPurchases;
    public static ArrayList<String> products = new ArrayList<>();

    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;

    LayoutInflater ltInflater;
    public final static int TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);


        Dexter.withActivity(this)
                .withPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        setupCamera();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();

        ltInflater = getLayoutInflater();
        buttonToPurchases = findViewById(R.id.buttonToPurchases);
        buttonToPurchases.setOnClickListener(onToPurchasesListener);

        TextView panelTop = findViewById(R.id.panelTopScan);
        panelTop.getLayoutParams().width = MainActivity.width * 3 / 5;
    }

    private void setupCamera() {
        buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(v -> isDetected = false);
        cameraView = findViewById(R.id.cameraView);
        cameraView.setLifecycleOwner(this);
        cameraView.addFrameProcessor(frame -> {
            if (!isDetected) {
                processImage(getVisionImageFromFrame(frame));
            }
        });
        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_EAN_13)
                .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
    }

    @SuppressLint("ShowToast")
    private void processImage(FirebaseVisionImage image) {
        detector.detectInImage(image)
                .addOnSuccessListener(firebaseVisionBarcodes -> {
                    if (!isDetected) {
                        processResult(firebaseVisionBarcodes);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ScanActivity.this, "" + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    // TYPES OF INPUT (TEXT, URL, VCARD, PRODUCT)
    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        if (firebaseVisionBarcodes.size() > 0) {
            isDetected = true;
            showResult(firebaseVisionBarcodes);
        }
    }

    private void showResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        for (FirebaseVisionBarcode item : firebaseVisionBarcodes) {
            int value_type = item.getValueType();
            if (value_type == FirebaseVisionBarcode.TYPE_PRODUCT) {
                createInfoView(item.getRawValue());
                addProductToBasket(item.getRawValue());
            } else {
                Toast.makeText(ScanActivity.this,
                        "Type: " + value_type, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addProductToBasket(String barCode) {
        products.add(barCode);
    }

    private void createInfoView(String text) {
        @SuppressLint("InflateParams") View view = ltInflater.inflate(R.layout.item_purchase,
                null, false);
        int productNum = products.size();
        TextView codeOfProduct = view.findViewById(R.id.barCode);
        codeOfProduct.setText(text);
        LinearLayout scanContainer = findViewById(R.id.scanContainer);
        scanContainer.removeAllViews();
        ImageView buttonDelete = view.findViewById(R.id.buttonDeletePurchase);
        buttonDelete.setOnClickListener(v -> {
            products.remove(productNum);
            scanContainer.removeView(view);
        });
        scanContainer.addView(view);

        new Handler().postDelayed(() -> scanContainer.removeView(view), TIME_OUT);
    }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
                .build();
        return FirebaseVisionImage.fromByteArray(data, metadata);
    }

    private View.OnClickListener onToPurchasesListener = v -> {
        Intent intent = new Intent(ScanActivity.this, PurchasesListActivity.class);
        isDetected = false;
        startActivity(intent);
    };
}

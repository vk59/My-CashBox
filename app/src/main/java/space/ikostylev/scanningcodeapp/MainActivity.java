package space.ikostylev.scanningcodeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CameraView cameraView;
    boolean isDetected = false;
    Button btnStart;
    Button btnToPurchases;
    public static ArrayList<Product> products = new ArrayList<>();

    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;

    LayoutInflater ltInflater;
    final int TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.withActivity(this)
                .withPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        setupCamera();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();

        ltInflater = getLayoutInflater();
        btnToPurchases = findViewById(R.id.btnToPurchases);
        btnToPurchases.setOnClickListener(onToPurchasesListener);
    }

    private void setupCamera() {
        btnStart = findViewById(R.id.btnStart);
        btnStart.setEnabled(isDetected);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDetected = !isDetected;
            }
        });

        cameraView = findViewById(R.id.cameraView);
        cameraView.setLifecycleOwner(this);
        cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                if (!isDetected) {
                    processImage(getVisionImageFromFrame(frame));
                }
            }
        });

        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_EAN_13)
                .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
    }

    private void processImage(FirebaseVisionImage image) {
        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        if (!isDetected) {
                            processResult(firebaseVisionBarcodes);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
    }

    // TYPES OF INPUT (TEXT, URL, VCARD, PRODUCT)
    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        if (firebaseVisionBarcodes.size() > 0) {
            isDetected = true;
            btnStart.setEnabled(isDetected);
            for (FirebaseVisionBarcode item : firebaseVisionBarcodes) {
                int value_type = item.getValueType();
                switch (value_type) {
                    case FirebaseVisionBarcode.TYPE_TEXT:
                    case FirebaseVisionBarcode.TYPE_PRODUCT: {
//                        createDialog(item.getRawValue());

                        createInfoView(item.getRawValue());
                        addProductToBasket(item.getRawValue());
                    }
                    break;
                    case FirebaseVisionBarcode.TYPE_URL: {
                        // start browser Intent
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getRawValue()));
                        startActivity(i);
                    }
                    break;
                    case FirebaseVisionBarcode.TYPE_CONTACT_INFO: {
                        String info = new StringBuilder("Name: ")
                                .append(item.getContactInfo().getName().getFormattedName())
                                .append("\n")
                                .append("Address: ")
                                .append(item.getContactInfo().getAddresses().get(0).getAddressLines())
                                .append("\n")
                                .append(item.getContactInfo().getEmails().get(0).getAddress())
                                .toString();
                    }
                    break;
                    default: {
                        Toast.makeText(MainActivity.this,
                                "Type: " + Integer.toString(value_type), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }
    }

    private void addProductToBasket(String barCode) {
        products.add(new Product(" ", " ", 20, Long.parseLong(barCode)));
    }

    private void createInfoView(String text) {
        View view = ltInflater.inflate(R.layout.item_purchase, null, false);
        int productNum = products.size();
        TextView codeOfProduct = view.findViewById(R.id.barCode);
        codeOfProduct.setText(text);
        LinearLayout scanContainer = findViewById(R.id.scanContainer);
        Button btnDelete = view.findViewById(R.id.btnDeletePurchase);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                products.remove(productNum);
                scanContainer.removeView(view);
            }
        });
        scanContainer.addView(view);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scanContainer.removeView(view);
            }
        }, TIME_OUT);
    }

    private void createDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
                //.setRotation(frame.getRotation())
                .build();
        return FirebaseVisionImage.fromByteArray(data, metadata);
    }

    private View.OnClickListener onToPurchasesListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            Intent intent = new Intent(MainActivity.this, PurchasesListActivity.class);
            startActivity(intent);
        }
    };
}

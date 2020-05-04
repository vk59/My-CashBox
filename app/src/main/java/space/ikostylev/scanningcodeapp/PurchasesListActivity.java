package space.ikostylev.scanningcodeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PurchasesListActivity extends AppCompatActivity {
    LayoutInflater ltInflater;
    LinearLayout containerPurchases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases_list);

        ltInflater = getLayoutInflater();
        initPurchases();

        Button btnPayWithQR = findViewById(R.id.btnPayWithQR);
        btnPayWithQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ScanActivity.products.size() > 0) {
                    Intent i = new Intent(PurchasesListActivity.this, PaymentActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(PurchasesListActivity.this, "Add at list one product, please", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initPurchases() {
        for (String product : ScanActivity.products) {
            View view = ltInflater.inflate(R.layout.item_purchase, null, false);
            TextView barCode = view.findViewById(R.id.barCode);
            Button btnDelete = view.findViewById(R.id.btnDeletePurchase);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScanActivity.products.remove(product);
                    containerPurchases.removeAllViews();
                    initPurchases();
                }
            });
            barCode.setText(String.valueOf(product));
            containerPurchases = findViewById(R.id.containerPurchases);
            containerPurchases.addView(view);
        }
    }
}

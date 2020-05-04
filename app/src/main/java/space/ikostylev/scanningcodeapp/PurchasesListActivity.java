package space.ikostylev.scanningcodeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
                Intent i = new Intent(PurchasesListActivity.this, PaymentActivity.class);

                startActivity(i);
            }
        });

        Button btnPayWithVoice = findViewById(R.id.btnPayWithVoice);
        btnPayWithVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PurchasesListActivity.this, PaymentVoiceActivity.class);

                startActivity(i);
            }
        });
    }

    private void initPurchases() {
        for (Product product : MainActivity.products) {
            View view = ltInflater.inflate(R.layout.item_purchase, null, false);
            TextView barCode = view.findViewById(R.id.barCode);
            Button btnDelete = view.findViewById(R.id.btnDeletePurchase);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.products.remove(product);
                    containerPurchases.removeAllViews();
                    initPurchases();
                }
            });
            barCode.setText(String.valueOf(product.getCode()));
            containerPurchases = findViewById(R.id.containerPurchases);
            containerPurchases.addView(view);
        }
    }
}

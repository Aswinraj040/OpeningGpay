package com.aswin.openinggpay;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aswin.openinggpay.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    int GOOGLE_PAY_REQUEST_CODE = 123;
    String amnt;
    String name = "TYPE YOUR MERCHANT NAME HERE";
    String id = "TYPE YOUR MERCHANT UPI ID HERE";
    String note = "TYPE YOUR TRANSACTION NOTE HERE";
    String status;
    Uri uri;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amnt = binding.amt.getText().toString();
                if (!amnt.isEmpty()) {
                    uri = getUpiPaymentUri(name, id, note, amnt);
                    payWithGPay();
                } else {
                    binding.amt.setError("Amount is required!");
                    binding.amt.requestFocus();
                }
            }
        });
    }

    private Uri getUpiPaymentUri(String name, String upiId, String transactionNote, String amount) {
        return new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", transactionNote)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();
    }

    private void payWithGPay() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
        } else {
            Toast.makeText(MainActivity.this, "No App Available to handle this transaction", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            status = data.getStringExtra("Status").toLowerCase();
        }

        if ((RESULT_OK == resultCode) && status.equals("success")) {
            Toast.makeText(MainActivity.this, "Transaction Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
        }
    }
}

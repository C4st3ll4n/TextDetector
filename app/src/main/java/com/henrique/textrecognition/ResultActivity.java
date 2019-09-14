package com.henrique.textrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    TextView txtResult;
    Button btnVoltar;
    String resultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtResult = findViewById(R.id.txtResultado);
        btnVoltar = findViewById(R.id.btnVoltar);
        resultText = getIntent().getStringExtra(LCOTextRecognization.RESULT_TEXT);
        txtResult.setText(resultText);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

package com.henrique.textrecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class MainActivity extends AppCompatActivity {
    Button btnCamera;
    private final static int REQUEST_CAMERA_OPEN = 200;
    FirebaseVisionTextRecognizer textRecognizer;
    FirebaseVisionImage fvi;
    String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = findViewById(
                R.id.btnAbrirCamera
        );

        FirebaseApp.initializeApp(this);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, REQUEST_CAMERA_OPEN);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA_OPEN && resultCode == RESULT_OK){
            Bitmap b = (Bitmap) data.getExtras().get("data");

            recognizeText(b);
        }
    }

    private void recognizeText(Bitmap b) {
        fvi = FirebaseVisionImage.fromBitmap(b);
        textRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        textRecognizer.processImage(fvi).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Deu ruim", Toast.LENGTH_SHORT).show();
                    }
                }
        ).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                if (firebaseVisionText.getText().isEmpty()){
                    Toast.makeText(MainActivity.this, "Nenhum texto reconhecido", Toast.LENGTH_SHORT).show();
                }else {
                    resultText = firebaseVisionText.getText();
                    Intent i = new Intent(MainActivity.this, ResultActivity.class);
                    i.putExtra(LCOTextRecognization.RESULT_TEXT, resultText);
                            startActivity(i);
                }
            }
        });
    }
}

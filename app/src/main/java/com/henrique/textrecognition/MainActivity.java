package com.henrique.textrecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

import static com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.ACCURATE;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.ALL_CONTOURS;
import static com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CAMERA_TEXT = 200;
    private final static int REQUEST_CAMERA_FACE = 201;

    Button btnCamera, btnFace;
    FirebaseVisionTextRecognizer textRecognizer;
    FirebaseVisionImage fvi;
    FirebaseVisionFaceDetector faceDetector;
    String resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = findViewById(
                R.id.btnAbrirCamera
        );

        btnFace = findViewById(
                R.id.btnDetectarFace);

        FirebaseApp.initializeApp(this);

        btnCamera.setOnClickListener((view)->{
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, REQUEST_CAMERA_TEXT);
                }
        });

        btnFace.setOnClickListener((view)->{
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, REQUEST_CAMERA_FACE);
                }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA_TEXT && resultCode == RESULT_OK){
            Bitmap b = (Bitmap) data.getExtras().get("data");
            recognizeText(b);
        }else if (requestCode == REQUEST_CAMERA_FACE && resultCode == RESULT_OK){
            Bitmap b = (Bitmap) data.getExtras().get("data");
            recognizeFace(b);
        }
    }

    private void recognizeFace(Bitmap b) {
        fvi = FirebaseVisionImage.fromBitmap(b);
        FirebaseVisionFaceDetectorOptions opts = new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();

        faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(opts);
        faceDetector.detectInImage(fvi).addOnSuccessListener(firebaseVisionFaces -> {

            if (firebaseVisionFaces.size() > 1) {
                int indice = 0;
                for (FirebaseVisionFace face : firebaseVisionFaces) {
                    AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Face "+(++indice)+" encontrada")
                            .setMessage("Probabilidade de sorriso: " + (face.getSmilingProbability() *100)+"%")
                            .setPositiveButton("OK", (v, p) -> v.dismiss())
                            .create();
                    ad.show();
                }
            }
                       // Toast.makeText(this, "And we have a face !!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(this, "Nenhuma face detectada!", Toast.LENGTH_SHORT).show());
    }

    private void recognizeText(Bitmap b) {
        fvi = FirebaseVisionImage.fromBitmap(b);
        textRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        textRecognizer.processImage(fvi).addOnFailureListener(
                e -> Toast.makeText(MainActivity.this, "Deu ruim", Toast.LENGTH_SHORT).show()
        ).addOnSuccessListener(firebaseVisionText -> {
            if (firebaseVisionText.getText().isEmpty()){
                Toast.makeText(MainActivity.this, "Nenhum texto reconhecido", Toast.LENGTH_SHORT).show();
            }else {
                resultText = firebaseVisionText.getText();
                Intent i = new Intent(MainActivity.this, ResultActivity.class);
                i.putExtra(Recognizer.RESULT_TEXT, resultText);
                        startActivity(i);
            }
        });
    }
}

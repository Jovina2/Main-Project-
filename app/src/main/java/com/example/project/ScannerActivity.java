package com.example.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

public class ScannerActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageView btnFlash;

    private Camera camera;
    private boolean isFlashOn = false;

    private boolean scannedOnce = false;

    private ProcessCameraProvider cameraProvider;

    // ✅ User ID received properly
    private int user_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_food);

        previewView = findViewById(R.id.previewView);
        btnFlash = findViewById(R.id.btnFlash);

        // ✅ Get user_id from previous screen
        user_id = getIntent().getIntExtra("user_id", -1);

        if (user_id == -1) {
            Toast.makeText(this, "User ID Missing!", Toast.LENGTH_LONG).show();
        }

        btnFlash.setOnClickListener(v -> toggleFlash());

        // ✅ Permission Check
        if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    // =====================================================
    // ✅ Start Camera
    // =====================================================
    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {

            try {
                cameraProvider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                BarcodeScanner scanner = BarcodeScanning.getClient();

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                analysis.setAnalyzer(
                        ContextCompat.getMainExecutor(this),
                        imageProxy -> {

                            if (scannedOnce) {
                                imageProxy.close();
                                return;
                            }

                            if (imageProxy.getImage() == null) {
                                imageProxy.close();
                                return;
                            }

                            InputImage image = InputImage.fromMediaImage(
                                    imageProxy.getImage(),
                                    imageProxy.getImageInfo().getRotationDegrees()
                            );

                            scanner.process(image)
                                    .addOnSuccessListener(barcodes -> {

                                        for (Barcode barcode : barcodes) {

                                            scannedOnce = true;

                                            String scannedValue = barcode.getRawValue();

                                            Toast.makeText(
                                                    this,
                                                    "Scanned: " + scannedValue,
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            // ✅ Send Barcode to PredictionResult
                                            Intent intent = new Intent(
                                                    ScannerActivity.this,
                                                    PredictionResult.class
                                            );

                                            intent.putExtra("barcode", scannedValue);

                                            // ✅ Tell PredictionResult this came from scanner
                                            intent.putExtra("source", "scanner");

                                            // ✅ Send user_id properly
                                            intent.putExtra("user_id", user_id);

                                            startActivity(intent);
                                            finish();

                                            break;
                                        }
                                    })
                                    .addOnCompleteListener(task -> imageProxy.close());
                        }
                );

                CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();

                camera = cameraProvider.bindToLifecycle(
                        this,
                        selector,
                        preview,
                        analysis
                );

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));
    }

    // =====================================================
    // ✅ Flash Toggle
    // =====================================================
    private void toggleFlash() {

        if (camera == null) return;

        if (camera.getCameraInfo().hasFlashUnit()) {

            isFlashOn = !isFlashOn;
            camera.getCameraControl().enableTorch(isFlashOn);

            Toast.makeText(
                    this,
                    isFlashOn ? "Flash ON" : "Flash OFF",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =====================================================
    // ✅ Release Camera Safely
    // =====================================================
    @Override
    protected void onPause() {
        super.onPause();
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }

    // =====================================================
    // ✅ Permission Result
    // =====================================================
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startCamera();

        } else {
            Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
        }
    }
}

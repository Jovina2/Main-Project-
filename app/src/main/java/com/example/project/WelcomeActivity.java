package com.example.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class
WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Create Account Button
        findViewById(R.id.btnCreate).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish(); //
        });

        // Login Button
        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        // Admin Login Button
        findViewById(R.id.btnAdminLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminActivity.class));
            finish(); //
        });

        // Google & Facebook login
        ImageView btnGoogle = findViewById(R.id.btnGoogle);
        ImageView btnFacebook = findViewById(R.id.btnFacebook);

        btnGoogle.setOnClickListener(v -> openUrl("https://accounts.google.com"));
        btnFacebook.setOnClickListener(v -> openUrl("https://www.facebook.com/login"));

        // Terms & Privacy clickable
        TextView txtTerms = findViewById(R.id.txtTerms);
        makeTermsClickable(txtTerms);
    }

    private void makeTermsClickable(TextView textView) {
        String text = "By continuing, you agree to our Terms of Service and Privacy Policy.";
        SpannableString spannable = new SpannableString(text);

        ClickableSpan termsClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openUrl("https://yourwebsite.com/terms");
            }
        };

        ClickableSpan privacyClick = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openUrl("https://yourwebsite.com/privacy");
            }
        };

        spannable.setSpan(termsClick, 32, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(privacyClick, 53, 67, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannable);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}

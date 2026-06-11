package com.example.campusmarket;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.ivSplashLogo);
        TextView tvTitle = findViewById(R.id.tvSplashTitle);
        TextView tvSubtitle = findViewById(R.id.tvSplashSubtitle);
        TextView tvVersion = findViewById(R.id.tvSplashVersion);

        playEntrance(ivLogo, 0);
        playEntrance(tvTitle, 150);
        playEntrance(tvSubtitle, 300);
        tvVersion.animate().alpha(1f).setDuration(600).setStartDelay(600).start();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }

    private void playEntrance(View view, long delay) {
        view.setAlpha(0f);
        view.setTranslationY(24f);

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator transY = ObjectAnimator.ofFloat(view, "translationY", 24f, 0f);

        set.playTogether(alpha, transY);
        set.setDuration(600);
        set.setStartDelay(delay);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }
}
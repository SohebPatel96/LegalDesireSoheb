package com.example.msp.legaldesire;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullScreenImage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fullscreenimage);
        ImageView fullscreenimage = (ImageView) findViewById(R.id.image_fullscreen);
        Intent intent = getIntent();
        if (intent != null) {
            Uri imageuri = intent.getData();
            if (imageuri != null && fullscreenimage != null) {
                Glide.with(this).load(imageuri).into(fullscreenimage);
            }
        }
    }
}

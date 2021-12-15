package com.example.applauncherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ApplicationActivity extends AppCompatActivity {

    private ImageView ivIcon;
    private TextView tvAppName, tvAppDescription, tvPackageName,
            tvAppCategory, tvAppSize, tvLastTimeUsed, tvTotalTimeInForeground;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);


        setUpWidgets();

        String name = getIntent().getStringExtra("name");
        tvAppName.setText(name);

        String description = getIntent().getStringExtra("description");
        tvAppDescription.setText(description.equals("null") ?  "No description was provided" : description);

        String packageName = getIntent().getStringExtra("packageName");
        tvPackageName.setText(packageName == null ? "No package name" : packageName);

        long size = Long.parseLong(getIntent().getStringExtra("size"));
        float sizeInMB = (float)size/1000000;
        tvAppSize.setText(size == 0 ? "Could not find the size" : df.format(sizeInMB) + "MB");

        long totalTimeInForeground = Long.parseLong(getIntent().getStringExtra("totalTimeInForeground"));
        tvTotalTimeInForeground.setText(DateUtils.formatElapsedTime(totalTimeInForeground / 1000));

        long lastTimeUsed = Long.parseLong(getIntent().getStringExtra("lastTimeUsed"));
        tvLastTimeUsed.setText(DateUtils.formatSameDayTime(lastTimeUsed,
                System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM));
        if(lastTimeUsed == -1){
            tvLastTimeUsed.setText("Not available");
        } else if(lastTimeUsed == 0){
            tvLastTimeUsed.setText("Was not used");
        }else{
            tvLastTimeUsed.setText(DateUtils.formatSameDayTime(lastTimeUsed,
                    System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM));
        }

        int category = Integer.parseInt((getIntent().getStringExtra("category")));
        switch (category) {
            case -1:
                tvAppCategory.setText("Category undefined");
                break;
            case 8:
                tvAppCategory.setText("Accessibility");
                break;
            case 1:
                tvAppCategory.setText("Audio");
                break;
            case 0:
                tvAppCategory.setText("Game");
                break;
            case 3:
                tvAppCategory.setText("Image");
                break;
            case 6:
                tvAppCategory.setText("Maps");
                break;
            case 5:
                tvAppCategory.setText("News");
                break;
            case 7:
                tvAppCategory.setText("Productivity");
                break;
            case 4:
                tvAppCategory.setText("Social");
                break;
            case 2:
                tvAppCategory.setText("Video");
                break;
        }


        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("icon");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        //Log.d("TEST", "icon: " + Arrays.toString(b));
        ivIcon.setImageBitmap(bmp);

    }

    public void setUpWidgets(){
        ivIcon = findViewById(R.id.ivLogo);
        tvAppName = findViewById(R.id.tvAppName);
        tvAppDescription = findViewById(R.id.tvAppDescription);
        tvPackageName = findViewById(R.id.tvPackageName);
        tvAppCategory = findViewById(R.id.tvAppCategory);
        tvAppSize = findViewById(R.id.tvAppSize);
        tvLastTimeUsed = findViewById(R.id.tvLastTimeUsed);
        tvTotalTimeInForeground = findViewById(R.id.tvTotalTimeInForeground);
    }
}
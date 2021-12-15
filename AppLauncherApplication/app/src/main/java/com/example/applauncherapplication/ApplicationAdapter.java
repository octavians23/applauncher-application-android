package com.example.applauncherapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {
    private ArrayList<SoftwareApp> softwareApps;
    private Context context;

    public ApplicationAdapter(Context context, ArrayList<SoftwareApp> softwareApps) {

        this.softwareApps = softwareApps;
        this.context = context;

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivIcon;
        private TextView tvName;
        private SoftwareApp softwareApp;
        private Context context;


        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            this.context = context;
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvName = (TextView) itemView.findViewById(R.id.tvName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (softwareApp.getAppName().equals("Search with Google Play")) {
                try {
                    this.context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?q=" + softwareApp.getDescription().toString())));
                } catch (android.content.ActivityNotFoundException exception) {
                    this.context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + softwareApp.getDescription().toString())));
                }
            } else {
                Intent intent = new Intent(this.context,
                        com.example.applauncherapplication.ApplicationActivity.class);

                //Log.d("TEST", softwareApp.toString());
                byte[] b;

                if(softwareApp.getAppIcon().toString().contains("Adaptive")){
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.android1);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    b = baos.toByteArray();

                }else {
                    Bitmap bitmap = ((BitmapDrawable) softwareApp.getAppIcon()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    b = baos.toByteArray();
                }

                //Log.d("TEST", Arrays.toString(b));
                //Log.d("TEST", "" + softwareApp.getAppIcon().toString().contains("Adaptive"));

                intent.putExtra("name", softwareApp.getAppName());
                intent.putExtra("icon", b);
                intent.putExtra("packageName", softwareApp.getPackageName());
                intent.putExtra("category", softwareApp.getCategoryId() + "");
                intent.putExtra("description", softwareApp.getDescription() + "");
                intent.putExtra("lastTimeUsed", softwareApp.getLastTimeUsed() + "");
                intent.putExtra("totalTimeInForeground", softwareApp.getTotalTimeInForeground() + "");
                intent.putExtra("size", softwareApp.getAppSize() + "");
                this.context.startActivity(intent);
            }
        }

        void bind(SoftwareApp sApp) {
            tvName.setText(sApp.getAppName());
            ivIcon.setImageDrawable(sApp.getAppIcon());
            softwareApp = sApp;
        }
    }

    @NonNull
    @Override
    public ApplicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);

        return new ViewHolder(view, this.context);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationAdapter.ViewHolder holder, int position) {

        SoftwareApp softwareApp = softwareApps.get(position);

        holder.bind(softwareApp);

    }

    @Override
    public int getItemCount() {
        return softwareApps.size();
    }
}

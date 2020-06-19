package com.rapido.provider.Transportation.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rapido.provider.R;
import com.squareup.picasso.Picasso;

public class FullImage extends AppCompatActivity {
    ImageView imgFull;
    ImageView imgback;
    TextView txtTitle;
    String title;
    String imgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        title = getIntent().getStringExtra("title");
        imgUrl = getIntent().getStringExtra("url");
        imgFull=findViewById(R.id.imgFull);
        imgback = findViewById(R.id.imgback);
        txtTitle =findViewById(R.id.txtTitle);
        Picasso.get().load(imgUrl).into(imgFull);
        txtTitle.setText(title);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}

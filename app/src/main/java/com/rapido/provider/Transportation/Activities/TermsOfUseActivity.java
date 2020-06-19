package com.rapido.provider.Transportation.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.rapido.provider.R;

import androidx.appcompat.app.AppCompatActivity;


public class TermsOfUseActivity extends AppCompatActivity {

    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
//            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        }
        setContentView(R.layout.activity_terms_of_use);



        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(view -> {
            //SharedHelper.putKey(getApplicationContext(), "password", "");
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            // activity.finish();
        });
    }
}

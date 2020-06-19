package com.rapido.provider.Transportation.WalkThrough;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rapido.provider.R;


public class Splashfragment1 extends Fragment {

    public static ImageView iv_background_1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.scroll_1, container, false);

        iv_background_1 = v.findViewById(R.id.iv_background_1);

        return v;
    }

    public static Splashfragment1 newInstance() {

        Splashfragment1 f = new Splashfragment1();
        return f;
    }
}
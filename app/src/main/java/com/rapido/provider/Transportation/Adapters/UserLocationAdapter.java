package com.rapido.provider.Transportation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rapido.provider.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserLocationAdapter extends RecyclerView.Adapter<UserLocationAdapter.MyViewHolder>{

    Context context;

    public UserLocationAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_list_item_view,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvLocationWork, tvLocationWorkAddress;
        ImageView ivWork;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivWork = itemView.findViewById(R.id.ivWork);
            tvLocationWork = itemView.findViewById(R.id.tvLocationWork);
            tvLocationWorkAddress = itemView.findViewById(R.id.tvLocationWorkAddress);

        }
    }
}

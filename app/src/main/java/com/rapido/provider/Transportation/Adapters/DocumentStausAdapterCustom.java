package com.rapido.provider.Transportation.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapido.provider.R;
import com.rapido.provider.Transportation.Bean.DocumentItem;

import java.util.ArrayList;

public class DocumentStausAdapterCustom  extends ArrayAdapter<DocumentItem> {


    private int resource;
    private ArrayList<DocumentItem> items;
    private Context context;

    public DocumentStausAdapterCustom(Context context, int resource, ArrayList<DocumentItem> items) {
        super(context, resource, items);
        this.context= context;
        this.resource = resource;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LinearLayout itemView;
        final DocumentItem item = items.get(position);
        if (convertView == null) {
            itemView = new LinearLayout(getContext());
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            layoutInflater.inflate(R.layout.custom_list_document_adapter, itemView, true);
        }
        else {
            itemView = (LinearLayout) convertView;
        }

    TextView textViewStatus = (TextView)itemView.findViewById(R.id.txtUploadStatus);
        TextView txtUploadTitle = (TextView)itemView.findViewById(R.id.txtUploadTitle);
        if (item.getStatus().equalsIgnoreCase("ASSESSING")){
            textViewStatus.setText(item.getStatus());
            textViewStatus.setTextColor(context.getResources().getColor(R.color.red));
        }
        else if (item.getStatus().equalsIgnoreCase("ACTIVE")){
            textViewStatus.setText(item.getStatus());
            textViewStatus.setTextColor(context.getResources().getColor(R.color.green));
        }
        else {
            textViewStatus.setText(item.getStatus());
            textViewStatus.setTextColor(context.getResources().getColor(R.color.blue));
        }
        txtUploadTitle.setText(item.getName());





        return itemView;
    }
}

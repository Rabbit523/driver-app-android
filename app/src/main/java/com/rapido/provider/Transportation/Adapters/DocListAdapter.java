package com.rapido.provider.Transportation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rapido.provider.R;
import com.rapido.provider.Transportation.Bean.DocListItemModel;
import com.rapido.provider.Transportation.Utilities.MyButton;
import com.rapido.provider.Transportation.Utilities.MyTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DocListAdapter extends RecyclerView.Adapter<DocListAdapter.MyViewHolder> {
    Context context;
    ArrayList<DocListItemModel> arrayListData;

    public DocListAdapter(Context context, ArrayList<DocListItemModel> arrayListData) {
        this.context = context;
        this.arrayListData = arrayListData;
    }

    public interface UploadEventClick {
        void onViewDetailClick(View v, int position);
    }

    public void setUploadEventClick(UploadEventClick OnUploadEventClick) {
        this.onUploadEventClick = OnUploadEventClick;
    }

    private UploadEventClick onUploadEventClick;





    @NonNull
    @Override
    public DocListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doc_list_adapter_item,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocListAdapter.MyViewHolder holder, int position) {
        holder.tvDocName.setText(arrayListData.get(position).getName());

        if (arrayListData.get(position).getImage() != null)
        {
            Picasso.get().load(arrayListData.get(position)
                    .getImage()).placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into( holder.ivImage);
        }
       /* if (SharedHelper.getKey(context,"ImageURI1").equalsIgnoreCase("ImageURI1")){
            String image1 = SharedHelper.getKey(context,"ImageURI");
            holder.btBrowse.setVisibility(View.GONE);

            Picasso.get().load(image1).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into( holder.ivImage);
        }*/


    }

    @Override
    public int getItemCount() {
        return arrayListData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MyTextView tvDocName;
        MyButton btBrowse;
        ImageView ivImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            btBrowse = itemView.findViewById(R.id.btBrowse);
            tvDocName = itemView.findViewById(R.id.tvDocName);
            btBrowse.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onUploadEventClick != null) {
                onUploadEventClick.onViewDetailClick(v, getLayoutPosition());
            }
        }


    }

    public void updateList(ArrayList<DocListItemModel> arrayList){
        arrayListData = arrayList;

        notifyDataSetChanged();
    }
}

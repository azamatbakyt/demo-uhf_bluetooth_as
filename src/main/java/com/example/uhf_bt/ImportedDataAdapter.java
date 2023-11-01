package com.example.uhf_bt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhf_bt.Models.TagData;

import java.util.List;

public class ImportedDataAdapter extends RecyclerView.Adapter<ImportedDataAdapter.ViewHolder> {

    Context context;
    List<TagData> tagList;

    public ImportedDataAdapter(Context context, List<TagData> tagList) {
        this.context = context;
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (tagList != null && tagList.size() > 0){
            TagData tag = tagList.get(position);
            holder.id1C.setText(tag.getId());
            holder.epc1C.setText(tag.getEpc());
            holder.type1C.setText(tag.getType());
            holder.description1C.setText(tag.getDescription());
            holder.inventoryNumber1C.setText(tag.getInventoryNumber());
            holder.nomenclature1C.setText(tag.getNomenclature());
            holder.amount1C.setText(String.valueOf(tag.getAmount()));
            holder.facility1C.setText(tag.getFacility());
            holder.premise1C.setText(tag.getPremise());
            holder.dateTime.setText(tag.getDateTimeFormatter());
            holder.executor1C.setText(tag.getExecutor());

        } else {
            return;
        }
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView id1C, epc1C,
        type1C, description1C, inventoryNumber1C,
        nomenclature1C, amount1C, facility1C, premise1C,
        dateTime, executor1C;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id1C = itemView.findViewById(R.id.id1C);
            epc1C = itemView.findViewById(R.id.epc1C);
            type1C = itemView.findViewById(R.id.type1C);
            description1C = itemView.findViewById(R.id.description1C);
            inventoryNumber1C = itemView.findViewById(R.id.inventoryNumber1C);
            nomenclature1C = itemView.findViewById(R.id.nomenclature1C);
            amount1C = itemView.findViewById(R.id.amount1C);
            facility1C = itemView.findViewById(R.id.facility1C);
            premise1C = itemView.findViewById(R.id.premise1C);
            dateTime = itemView.findViewById(R.id.dateTime);
            executor1C = itemView.findViewById(R.id.executor1C);





        }
    }
}

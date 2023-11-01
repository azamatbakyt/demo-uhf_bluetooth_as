package com.example.uhf_bt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhf_bt.Models.TagData;
import java.util.*;

public class ResultDataAdapter extends RecyclerView.Adapter<ResultDataAdapter.ViewHolder> {
    private Context context;
    private List<TagData> tagList;

    public ResultDataAdapter(Context context, List<TagData> tagList) {
        this.context = context;
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.result_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultDataAdapter.ViewHolder holder, int position) {
        if (tagList != null && tagList.size() > 0){
            TagData tag = tagList.get(position);
            holder.idResult.setText(tag.getId());
            holder.epcResult.setText(tag.getEpc());
            holder.typeResult.setText(tag.getType());
            holder.descriptionResult.setText(tag.getDescription());
            holder.inventoryNumberResult.setText(tag.getInventoryNumber());
            holder.nomenclatureResult.setText(tag.getNomenclature());
            holder.amountResult.setText(String.valueOf(tag.getAmount()));
            holder.facilityResult.setText(tag.getFacility());
            holder.premiseResult.setText(tag.getPremise());
            holder.dateTime.setText(tag.getDateTimeFormatter());
            holder.executorResult.setText(tag.getExecutor());
        } else{
            return;
        }
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView idResult, epcResult,
                typeResult, descriptionResult, inventoryNumberResult,
                nomenclatureResult, amountResult, facilityResult, premiseResult,
                dateTime, executorResult;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idResult = itemView.findViewById(R.id.idResult);
            epcResult = itemView.findViewById(R.id.epcResult);
            typeResult = itemView.findViewById(R.id.typeResult);
            descriptionResult = itemView.findViewById(R.id.descriptionResult);
            inventoryNumberResult = itemView.findViewById(R.id.inventoryNumberResult);
            nomenclatureResult = itemView.findViewById(R.id.nomenclatureResult);
            amountResult = itemView.findViewById(R.id.amountResult);
            facilityResult = itemView.findViewById(R.id.facilityResult);
            premiseResult = itemView.findViewById(R.id.premiseResult);
            dateTime = itemView.findViewById(R.id.dateTime);
            executorResult = itemView.findViewById(R.id.executorResult);





        }
        
    }
}

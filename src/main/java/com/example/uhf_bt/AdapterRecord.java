package com.example.uhf_bt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhf_bt.Models.TagData;

import java.util.List;

public class AdapterRecord extends RecyclerView.Adapter<AdapterRecord.HolderRecord>{

    private Context context;
    private List<TagData> tagRecordList;

    public AdapterRecord(Context context, List<TagData> tagRecordList) {
        this.context = context;
        this.tagRecordList = tagRecordList;
    }

    @NonNull
    @Override
    public HolderRecord onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_epc, parent, false);

        return new HolderRecord(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecord holder, int position) {
        // get data
         TagData tagData = tagRecordList.get(position);
         String nomenclature = tagData.getNomenclature();
         String epc = tagData.getEpc();
         String description = tagData.getDescription();
         String type = tagData.getType();
         int amount = tagData.getAmount();

         holder.nomenclatureInfo.setText(nomenclature);
         holder.epcInfo.setText(epc);
         holder.descriptionInfo.setText(description);
         holder.typeInfo.setText("Type: " + type);
         holder.amount.setText("Amount: " + amount);

//         // handle item clicks (go to detail record activity)
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        //

    }

    @Override
    public int getItemCount() {
        return tagRecordList.size();
    }


    class HolderRecord extends RecyclerView.ViewHolder {
        ImageView epcImage, moreBtn;
        TextView nomenclatureInfo, epcInfo, descriptionInfo, typeInfo, amount;

        public HolderRecord(@NonNull View itemView) {
            super(itemView);

            // init views
            nomenclatureInfo = itemView.findViewById(R.id.nomenclature_info);
            epcInfo = itemView.findViewById(R.id.epc_info);
            descriptionInfo = itemView.findViewById(R.id.description_info);
            typeInfo = itemView.findViewById(R.id.type_info);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}

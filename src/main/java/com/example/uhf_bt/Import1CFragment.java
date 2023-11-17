package com.example.uhf_bt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhf_bt.Models.TagData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Import1CFragment extends Fragment {

    private MainActivity mContext;

    private DataBase db;
    private ListView importedItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_import1_c, container, false);
        mContext = (MainActivity) getActivity();
        db = new DataBase(mContext);
        importedItems = rootView.findViewById(R.id.importedItems);
        fillList();
        return rootView;
    }

    public void fillList(){
        try {
            List<Map<String, String>> proList = new ArrayList<Map<String, String>>();
            String[] from = {"Id", "EPC", "Type", "Description", "Inv Number", "Nomenclature", "Amount",
                    "Facility", "Premise", "DateTime", "Executor"};
            int[] views = {R.id.id1C, R.id.epc1C, R.id.type1C,
                    R.id.description1C, R.id.invNumber1C, R.id.nomenclature1C,
                    R.id.amount1C, R.id.facility1C, R.id.premise1C, R.id.dateTime1C,
                    R.id.executor1c};
            List<TagData> dataList = db.getDataFrom1C();
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, String> dataNum = new HashMap<String, String>();
                dataNum.put("Id", String.valueOf(dataList.get(i).getId()));
                dataNum.put("EPC", String.valueOf(dataList.get(i).getEpc()));
                dataNum.put("Type", String.valueOf(dataList.get(i).getType()));
                dataNum.put("Description", String.valueOf(dataList.get(i).getDescription()));
                dataNum.put("Inv Number", String.valueOf(dataList.get(i).getInventoryNumber()));
                dataNum.put("Nomenclature", String.valueOf(dataList.get(i).getNomenclature()));
                dataNum.put("Amount", String.valueOf(dataList.get(i).getAmount()));
                dataNum.put("Facility", String.valueOf(dataList.get(i).getFacility()));
                dataNum.put("Premise", String.valueOf(dataList.get(i).getPremise()));
                dataNum.put("DateTime", String.valueOf(dataList.get(i).getDate()));
                dataNum.put("Executor", String.valueOf(dataList.get(i).getExecutor()));
                proList.add(dataNum);
            }

            final SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, proList,
                    R.layout.item_layout, from, views);

            importedItems.setAdapter(simpleAdapter);


        } catch (Exception e){
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }



    }


}
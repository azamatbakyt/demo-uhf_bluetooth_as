package com.example.uhf_bt.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uhf_bt.AdapterRecord;
import com.example.uhf_bt.DataBase;
import com.example.uhf_bt.MainActivity;
import com.example.uhf_bt.Models.TagData;
import com.example.uhf_bt.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryFragment1C extends Fragment {
    private RecyclerView dataRv;
    private DataBase db;
    private AdapterRecord adapterRecord;
    MainActivity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory1c, container, false);
        initFilter(view);
        updateData();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MainActivity) getActivity();
    }

    private void initFilter(View view) {
        dataRv = view.findViewById(R.id.dataRv);
        db = new DataBase(getActivity());
    }

    public void updateData() {
        adapterRecord = new AdapterRecord(getActivity(), db.get1C());
        dataRv.setAdapter(adapterRecord );
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

}
package com.example.uhf_bt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhf_bt.Models.TagData;

import java.util.List;

public class Import1CFragment extends Fragment {

    private MainActivity mContext;

    private DataBase db;
    RecyclerView recyclerView;
    ImportedDataAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_import1_c, container, false);
        mContext = (MainActivity) getActivity();
        recyclerView = rootView.findViewById(R.id.recyclerView1C);
        db = new DataBase(mContext);
        setRecyclerView();
        return rootView;
    }

    public void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new ImportedDataAdapter(mContext, getList());
        recyclerView.setAdapter(adapter);
    }

    private List<TagData> getList(){
        List<TagData> tagList = db.getDataFrom1C();
        return tagList;
    }

    @Override
    public void onResume() {
        super.onResume();
        setRecyclerView();
    }
}
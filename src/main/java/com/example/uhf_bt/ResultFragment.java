package com.example.uhf_bt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uhf_bt.Models.TagData;

import java.util.List;


public class ResultFragment extends Fragment {

    private DataBase db;

    private RecyclerView recyclerView;
    private ResultDataAdapter resultDataAdapter;
    private MainActivity context;
    private Button refresh;

    private Button refreshTable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        context = (MainActivity) getActivity();
        recyclerView = rootView.findViewById(R.id.recyclerViewResult);
        db = new DataBase(context);
        refresh = rootView.findViewById(R.id.refreshButton);
        refreshTable = rootView.findViewById(R.id.refreshResultTable);

        refreshTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.refreshResultTable();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                resultDataAdapter = new ResultDataAdapter(context, getData());
                recyclerView.setAdapter(resultDataAdapter);
            }
        });



        return rootView;
    }



    private List<TagData> getData(){
        return db.getResult();
    }

}
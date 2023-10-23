package com.example.uhf_bt.fragment;

import static com.example.uhf_bt.fragment.BTRenameFragment.isExit_;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.uhf_bt.AdapterRecord;
import com.example.uhf_bt.DataBase;
import com.example.uhf_bt.MainActivity;
import com.example.uhf_bt.Models.TagData;
import com.example.uhf_bt.R;
import com.example.uhf_bt.Utils;
import com.rscja.deviceapi.RFIDWithUHFBLE;
import com.rscja.deviceapi.interfaces.ConnectionStatus;
import com.rscja.deviceapi.interfaces.KeyEventCallback;

import android.os.AsyncTask;

public class BarcodeFragment extends Fragment implements View.OnClickListener {
    private AdapterRecord adapterRecord;
    private DataBase db;
    private List<TagData> tagDataList;
    private RecyclerView barcodeRv;
    private MainActivity mContext;
    private Spinner spingCodingFormat;
    private TextView tvData;
    private Button btnScan, btClear;
    private static final int MAX_DATA_LENGTH = 4096; // Максимальная длина данных в TextView
    private static final String NEWLINE = "\r\n";

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String data = msg.obj.toString();
            appendDataToTextView(data);
            Utils.playSound(1);
            return true;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_barcode, container, false);
        barcodeRv = rootView.findViewById(R.id.barcodeRv);
        tagDataList = new ArrayList<>();
        barcodeRv.setLayoutManager(new LinearLayoutManager(getActivity()));



        tvData = rootView.findViewById(R.id.tvData);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initListeners();
        mContext = (MainActivity) getActivity();
        adapterRecord = new AdapterRecord(mContext, tagDataList);
        db = new DataBase(getActivity());
        tagDataList = new ArrayList<>();

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                String data = msg.obj.toString();
                appendDataToTextView(data);
                Utils.playSound(1);
                return true;
            }
        });

        mContext.uhf.setKeyEventCallback(new KeyEventCallback() {
            @Override
            public void onKeyDown(int keycode) {
                if (!isExit_ && mContext.uhf.getConnectStatus() == ConnectionStatus.CONNECTED) {
                    scan();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isExit_ = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnScan:
                scan();
                break;
            case R.id.btClear:
                clearTextView();
                break;
        }
    }

    private void initViews() {
        btnScan = getView().findViewById(R.id.btnScan);
        btClear = getView().findViewById(R.id.btClear);
        spingCodingFormat = getView().findViewById(R.id.spingCodingFormat);
    }

    private void initListeners() {
        btnScan.setOnClickListener(this);
        btClear.setOnClickListener(this);
    }

    private synchronized void scan() {
        if (!isRuning) {
            isRuning = true;
            new ScanTask().start();
        }
    }

    private boolean isRuning = false;

    private class ScanTask extends Thread {
        @Override
        public void run() {
            String data = null;
            String example = "4820024700016";
            byte[] temp = example.getBytes();
            if (temp != null) {
                if (spingCodingFormat.getSelectedItemPosition() == 1) {
                    try {
                        data = new String(temp, "utf8");

                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            } else if (spingCodingFormat.getSelectedItemPosition() == 2) {
                try {
                    data = new String(temp, "gb2312");
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                data = new String(temp);
            }

            if (data != null && !data.isEmpty()) {
                Log.d("DeviceAPI_setKeyEvent", "data=" + data);
                List<TagData> taglist = db.getDataByEpc(data);
                Message msg = Message.obtain();
                msg.obj = data;
                handler.sendMessage(msg);
//                updateAdapterData(data);
            }

            isRuning = false;
        }
    }

    private void clearTextView() {
        adapterRecord.clearData();
    }

    private void appendDataToTextView(String data) {
        if (data != null) {
            List<TagData> taglist = db.getDataByEpcForInventory(data);
            if (!(taglist.isEmpty())) {
                TagData tagData = new TagData();
                tagData.setNomenclature(taglist.get(0).getNomenclature());
                tagData.setEpc(data);
                tagData.setDescription(taglist.get(0).getDescription());
                tagData.setType("Barcode");
                tagData.setAmount(taglist.get(0).getAmount());
                tagDataList.add(tagData);
                adapterRecord.updateData(tagDataList);
                barcodeRv.setAdapter(adapterRecord);
            } else {
                TagData tagData = new TagData();
                tagData.setNomenclature("Null");
                tagData.setEpc(data);
                tagData.setDescription("Null");
                tagData.setType("Barcode");
                tagData.setAmount(0);
                tagDataList.add(tagData);
                adapterRecord.updateData(tagDataList);
                barcodeRv.setAdapter(adapterRecord);
            }
        }
    }
    
}

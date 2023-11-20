package com.example.uhf_bt;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;

import com.example.uhf_bt.Models.TagData;
import com.example.uhf_bt.fragment.BarcodeFragment;
import com.example.uhf_bt.fragment.SetUser;
import com.example.uhf_bt.fragment.UHFInventoryFragment;
import com.example.uhf_bt.fragment.UHFReadTagFragment;
import com.example.uhf_bt.fragment.UHFSetFragment;
import com.opencsv.CSVReader;
import com.rscja.deviceapi.RFIDWithUHFBLE;
import com.rscja.deviceapi.interfaces.ConnectionStatus;
import com.rscja.deviceapi.interfaces.ConnectionStatusCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import no.nordicsemi.android.dfu.BuildConfig;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String STORAGE_PERMISSION_REQUIRED = "Storage permission required...";
    public boolean isScanning = false;
    public String remoteBTName = "";
    public String remoteBTAdd = "";
    private final static String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_SELECT_DEVICE = 1;

    public BluetoothDevice mDevice = null;
    private FragmentTabHost mTabHost;
    private FragmentManager fm;
    private Button btn_connect, btn_search;
    private TextView tvAddress;
    public BluetoothAdapter mBtAdapter = null;
    public RFIDWithUHFBLE uhf = RFIDWithUHFBLE.getInstance();
    BTStatus btStatus = new BTStatus();

    public static final String SHOW_HISTORY_CONNECTED_LIST = "showHistoryConnectedList";
    public static final String TAG_DATA = "tagData";
    public static final String TAG_EPC = "tagEpc";
    public static final String TAG_TID = "tagTid";
    public static final String TAG_LEN = "tagLen";
    public static final String TAG_COUNT = "tagCount";
    public static final String TAG_RSSI = "tagRssi";

    private boolean mIsActiveDisconnect = true; // 是否主动断开连接
    private static final int RECONNECT_NUM = Integer.MAX_VALUE; // 重连次数
    private int mReConnectCount = RECONNECT_NUM; // 重新连接次数

    private Timer mDisconnectTimer = new Timer();
    private DisconnectTimerTask timerTask;
    private long timeCountCur; // 断开时间选择
    private long period = 1000 * 30; // 隔多少时间更新一次
    private long lastTouchTime = System.currentTimeMillis(); // 上次接触屏幕操作的时间戳
    private DataBase db;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;


    private static final int RUNNING_DISCONNECT_TIMER = 10;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RUNNING_DISCONNECT_TIMER:
                    long time = (long) msg.obj;
                    formatConnectButton(time);
                    break;
            }
        }
    };

    private static final int STORAGE_REQUEST_CODE_EXPORT = 1;
    private static final int STORAGE_REQUEST_CODE_IMPORT = 2;
    private String[] storagePermissions;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (BuildConfig.DEBUG) {
            setTitle(String.format("%s(v%s-debug)", getString(R.string.app_name), BuildConfig.VERSION_NAME));
        } else {
            setTitle(String.format("%s(v%s)", getString(R.string.app_name), BuildConfig.VERSION_NAME));
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        initUI();
        checkLocationEnable();
        uhf.init(getApplicationContext());
        Utils.initSound(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        uhf.free();
        Utils.freeSound();
        connectStatusList.clear();
        cancelDisconnectTimer();
        super.onDestroy();
        android.os.Process.killProcess(Process.myPid());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                if (isScanning) {
                    showToast(R.string.title_stop_read_card);
                } else if (uhf.getConnectStatus() == ConnectionStatus.CONNECTING) {
                    showToast(R.string.connecting);
                } else if (uhf.getConnectStatus() == ConnectionStatus.CONNECTED) {
                    disconnect(true);
                } else {
                    showBluetoothDevice(true);
                }
                break;
            case R.id.btn_search:
                if (isScanning) {
                    showToast(R.string.title_stop_read_card);
                } else if (uhf.getConnectStatus() == ConnectionStatus.CONNECTING) {
                    showToast(R.string.connecting);
                } else {
                    showBluetoothDevice(false);
                }
                break;
        }
    }

    private void formatConnectButton(long disconnectTime) {
        if (uhf.getConnectStatus() == ConnectionStatus.CONNECTED) {
            if (!isScanning && System.currentTimeMillis() - lastTouchTime > 1000 * 30 && timerTask != null) {
                long minute = disconnectTime / 1000 / 60;
                if (minute > 0) {
                    btn_connect.setText(getString(R.string.disConnectForMinute, minute)); //倒计时分
                } else {
                    btn_connect.setText(getString(R.string.disConnectForSecond, disconnectTime / 1000)); // 倒计时秒
                }
            } else {
                btn_connect.setText(R.string.disConnect);
            }
        } else {
            btn_connect.setText(R.string.Connect);
        }
    }

    /**
     * 重置断开时间
     */
    public void resetDisconnectTime() {
        timeCountCur = SPUtils.getInstance(getApplicationContext()).getSPLong(SPUtils.DISCONNECT_TIME, 0);
        if (timeCountCur > 0) {
            formatConnectButton(timeCountCur);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        lastTouchTime = System.currentTimeMillis();
        resetDisconnectTime();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public static final int requestcode = 3;
    ArrayList<HashMap<String, String>> myList;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (!isScanning) {
            if (item.getItemId() == R.id.UHF_Battery) {
                String ver = getString(R.string.action_uhf_bat) + ":" + uhf.getBattery() + "%";
                Utils.alert(MainActivity.this, R.string.action_uhf_bat, ver, R.drawable.webtext);
            } else if (item.getItemId() == R.id.UHF_T) {
                String temp = getString(R.string.title_about_Temperature) + ":" + uhf.getTemperature() + "℃";
                Utils.alert(MainActivity.this, R.string.title_about_Temperature, temp, R.drawable.webtext);
            } else if (item.getItemId() == R.id.UHF_ver) {
                String ver = uhf.getVersion();
                Utils.alert(MainActivity.this, R.string.action_uhf_ver, ver, R.drawable.webtext);
            } else if (item.getItemId() == R.id.ble_ver) {
                HashMap<String, String> versionMap = uhf.getBluetoothVersion();
                if (versionMap != null) {
                    String verMsg = "固件版本：" + versionMap.get(RFIDWithUHFBLE.VERSION_BT_FIRMWARE)
                            + "\n硬件版本：" + versionMap.get(RFIDWithUHFBLE.VERSION_BT_HARDWARE)
                            + "\n软件版本：" + versionMap.get(RFIDWithUHFBLE.VERSION_BT_SOFTWARE);
                    Utils.alert(MainActivity.this, R.string.action_ble_ver, verMsg, R.drawable.webtext);
                }
            } else if (item.getItemId() == R.id.ble_disconnectTime) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_disconnect_time, null);
                final Spinner spDisconnectTime = view.findViewById(R.id.spDisconnectTime);
                int index = SPUtils.getInstance(getApplicationContext()).getSPInt(SPUtils.DISCONNECT_TIME_INDEX, 0);
                spDisconnectTime.setSelection(index);
                Utils.alert(this, R.string.disconnectTime, view, R.drawable.webtext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int index = spDisconnectTime.getSelectedItemPosition();
                        long time = 1000 * 60 * 60 * index;
                        SPUtils.getInstance(getApplicationContext()).setSPInt(SPUtils.DISCONNECT_TIME_INDEX, index);
                        SPUtils.getInstance(getApplicationContext()).setSPLong(SPUtils.DISCONNECT_TIME, time);
                        switch (index) {
                            case 0:
                                cancelDisconnectTimer();
                                break;
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                if (uhf.getConnectStatus() == ConnectionStatus.CONNECTED) {
                                    cancelDisconnectTimer();
                                    startDisconnectTimer(time);
                                }
                                break;
                        }
                    }
                });
            } else if (item.getItemId() == R.id.backup_action) {
                // backup all records to CSV file
                if (checkStoragePermission()) {
                    // permission allowed
                    exportCSV();
                } else {
                    // permission denied
                    requestStoragePermissionExport();
                }
            } else if (item.getItemId() == R.id.restore_action) {
                // restore all records from csv file
                if (checkStoragePermission()) {
                    Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    fileIntent.setType("*/*");
                    fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    try {
                        startActivityForResult(fileIntent, requestcode);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, "No activity", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // permission denied
                    requestStoragePermissionImport();
                }
            }
        } else {
            showToast(R.string.title_stop_read_card);
        }
        return true;
    }


    private boolean checkStoragePermission() {
        //check if storage permission is enabled or not and return true/false;
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissionImport() {
        //request storage  permission for import
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_IMPORT);
    }

    private void requestStoragePermissionExport() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_EXPORT);
    }

    private final static String csvFileName = "SQLite_Backup.csv";

    private void exportCSV() {
        //path of CSV file
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + "SQLiteBackup"); // SQLiteBackup is a folder name
        boolean isFolderCreated = false;
        if (!folder.exists()) {
            isFolderCreated = folder.mkdir();// create folder if not exists
        }
        Log.d("CSC_TAG", "exportCSV: " + isFolderCreated);

        //file name

        //complete path and name
        String filePathAndName = folder.toString() + "/" + csvFileName;

        //get records
        List<TagData> tagList = new ArrayList<>();
        tagList.clear();
        tagList = db.getAll();

        try {
            // write csv file
            FileWriter fw = new FileWriter(filePathAndName);

            fw.append("ID, EPC, Type, Description, InventoryNumber, Nomenclature, Amount, Facility, Premise, DateTime, Executor\n");

            for (int i = 0; i < tagList.size(); i++) {
                fw.append("" + tagList.get(i).getId());// id
                fw.append(",");
                fw.append("" + tagList.get(i).getEpc()); // epc
                fw.append(",");
                fw.append("" + tagList.get(i).getType()); // type
                fw.append(",");
                fw.append("" + tagList.get(i).getDescription()); // description
                fw.append(",");
                fw.append("" + tagList.get(i).getInventoryNumber()); // inventory number
                fw.append(",");
                fw.append("" + tagList.get(i).getNomenclature()); // nomenclature
                fw.append(",");
                fw.append("" + tagList.get(i).getAmount()); // amount
                fw.append(",");
                fw.append("" + tagList.get(i).getFacility()); // facility
                fw.append(",");
                fw.append("" + tagList.get(i).getPremise()); // premise
                fw.append(",");
                fw.append("" + tagList.get(i).getDateTimeFormatter()); // date
                fw.append(",");
                fw.append("" + tagList.get(i).getExecutor()); // executor
                fw.append("\n");

            }
            fw.flush();
            fw.close();

            Toast.makeText(this, "Backup Exported to: " + filePathAndName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void importCSV() {
//        String filePathAndName = Environment.getExternalStorageDirectory() + "/SQLiteBackup/" + csvFileName;
//        File csvFile = new File(filePathAndName);
//
//        // check if exists or not
//        if (csvFile.exists()) {
//
//            try {
//                CSVReader reader = new CSVReader(new FileReader(csvFile.getAbsolutePath()));
//                String[] nextLine;
//                while ((nextLine = reader.readNext()) != null) {
//                    String id = nextLine[0];
//                    String epc = nextLine[1];
//                    String type = nextLine[2];
//                    String description = nextLine[3];
//                    String inventory_number = nextLine[4];
//                    String nomenclature = nextLine[5];
//                    String amount = nextLine[6];
//                    String facility = nextLine[7];
//                    String premise = nextLine[8];
//                    String dateTime = nextLine[9];
//                    String executor = nextLine[10];
//                    TagData tagData = new TagData(
//                            "" + id,
//                            "" + epc,
//                            "" + type,
//                            "" + description,
//                            "" + inventory_number,
//                            "" + nomenclature,
//                            Integer.parseInt(amount),
//                            "" + facility,
//                            "" + premise,
//                            "" + dateTime,
//                            "" + executor
//                    );
//                    if (db.importDataFrom1C(tagData))
//                        Toast.makeText(this, "CSV imported", Toast.LENGTH_LONG).show();
//                }
//
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }

//        } else {
//            // backup doesn't exist
//            Toast.makeText(this, "No backup found...", Toast.LENGTH_LONG).show();
//        }

        db.importCSVToDatabase(this, csvFileName);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        // handle permission result
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_REQUEST_CODE_EXPORT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    exportCSV();
                } else {
                    // permission denied
                    Toast.makeText(this, STORAGE_PERMISSION_REQUIRED, Toast.LENGTH_LONG).show();
                }

            }
            break;
            case STORAGE_REQUEST_CODE_IMPORT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    importCSV();
                } else {
                    // permission denied
                    Toast.makeText(this, STORAGE_PERMISSION_REQUIRED, Toast.LENGTH_LONG).show();
                }

            }
            break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        switch (requestCode) {


            case REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (uhf.getConnectStatus() == ConnectionStatus.CONNECTED) {
                        disconnect(true);
                    }
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    tvAddress.setText(String.format("%s(%s)\nconnecting", mDevice.getName(), deviceAddress));
                    connect(deviceAddress);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    showToast("Bluetooth has turned on ");
                } else {
                    showToast("Problem in BT Turning ON ");
                }
                break;
            case requestcode:
//                String filePath = data.getData().getPath();
//                Log.e("New file path", filePath);
//                if (filePath.contains("/root_path"))
//                    filePath = filePath.replace("/root_path", "");
//                System.out.println(filePath);
//                Log.e("New File path", filePath);
//                db = new DataBase(getApplicationContext());
//                SQLiteDatabase database = db.getWritableDatabase();
//                database.execSQL("DELETE FROM inventory_table_1c");
//                try{
//                    if (resultCode == RESULT_OK){
//                    Log.e("RESULT CODE", "OK");
//                    try{
//                        InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
//                        BufferedReader bfReader = new BufferedReader(new InputStreamReader(inputStream));
//                        ContentValues contentValues = new ContentValues();
//                        String line = "";
//                        database.beginTransaction();
//                        while((line = bfReader.readLine()) != null) {
//                            Log.e("line", line);
//                            String[] str = line.split(",", 11); // defining 11 columns with null or blank field
//                            int id = Integer.parseInt(str[0]);
//                            String epc = str[1];
//                            String type = str[2];
//                            String description = str[3];
//                            String invNumber = str[4];
//                            String nomenclature = str[5];
//                            int amount = Integer.parseInt(str[6]);
//                            String facility = str[7];
//                            String premise = str[8];
//                            String dateTime = str[9];
//                            String executor = str[10];
//
//                            contentValues.put(DataBase.db_table_1c_id, id);
//                            contentValues.put(DataBase.db_table_1c_epc, epc);
//                            contentValues.put(DataBase.db_table_1c_type, type);
//                            contentValues.put(DataBase.db_table_1c_description, description);
//                            contentValues.put(DataBase.db_table_1c_inventory_number, invNumber);
//                            contentValues.put(DataBase.db_table_1c_nomenclature, nomenclature);
//                            contentValues.put(DataBase.db_table_1c_amount, amount);
//                            contentValues.put(DataBase.db_table_1c_facility, facility);
//                            contentValues.put(DataBase.db_table_1c_premise, premise);
//                            contentValues.put(DataBase.db_table_1c_datetime, dateTime);
//                            contentValues.put(DataBase.db_table_1c_executor, executor);
//                            database.insert(DataBase.db_table_inventory_1c, null, contentValues);
//
//                                    Toast.makeText(this, "Successfully imported", Toast.LENGTH_SHORT).show();
//
//                            Log.e("Import: ", "Successfully imported");
//                        }
//
//                        database.setTransactionSuccessful();
//                        database.endTransaction();
//                        } catch (Exception e){
//                        System.out.println(e.getMessage());
//                    }
//                    } else{
//                        Log.e("RESULT CODE", "InValid");
//                        if (database.inTransaction())
//
//                            database.endTransaction();
//                        Toast.makeText(MainActivity.this, "Only CSV files allowed.", Toast.LENGTH_LONG).show();
//
//                    }
//                } catch (Exception e) {
//                    Log.e("Error", e.getMessage().toString());
//                    if (database.inTransaction())
//
//                        database.endTransaction();
//
//                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//                break;
            default:
                break;
        }
    }

    private void showBluetoothDevice(boolean isHistory) {
        if (mBtAdapter == null) {
            showToast("Bluetooth is not available");
            return;
        }
        if (!mBtAdapter.isEnabled()) {
            Log.i(TAG, "onClick - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
            newIntent.putExtra(SHOW_HISTORY_CONNECTED_LIST, isHistory);
            startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
            cancelDisconnectTimer();
        }
    }

    public void connect(String deviceAddress) {
        if (uhf.getConnectStatus() == ConnectionStatus.CONNECTING) {
            showToast(R.string.connecting);
        } else {
            uhf.connect(deviceAddress, btStatus);
        }
    }

    public void disconnect(boolean isActiveDisconnect) {
        cancelDisconnectTimer();
        mIsActiveDisconnect = isActiveDisconnect; // 主动断开为true
        uhf.disconnect();
    }

    /**
     * 重新连接
     *
     * @param deviceAddress
     */
    private void reConnect(String deviceAddress) {
        if (!mIsActiveDisconnect && mReConnectCount > 0) {
            connect(deviceAddress);
            mReConnectCount--;
        }
    }

    /**
     * 应该提示未连接状态
     *
     * @return
     */
    private boolean shouldShowDisconnected() {
        return mIsActiveDisconnect || mReConnectCount == 0;
    }

    class BTStatus implements ConnectionStatusCallback<Object> {
        @Override
        public void getStatus(final ConnectionStatus connectionStatus, final Object device1) {
            runOnUiThread(new Runnable() {
                public void run() {
                    BluetoothDevice device = (BluetoothDevice) device1;
                    remoteBTName = "";
                    remoteBTAdd = "";
                    if (connectionStatus == ConnectionStatus.CONNECTED) {
                        remoteBTName = device.getName();
                        remoteBTAdd = device.getAddress();

                        tvAddress.setText(String.format("%s(%s)\nconnected", remoteBTName, remoteBTAdd));
                        if (shouldShowDisconnected()) {
                            showToast(R.string.connect_success);
                        }

                        timeCountCur = SPUtils.getInstance(getApplicationContext()).getSPLong(SPUtils.DISCONNECT_TIME, 0);
                        if (timeCountCur > 0) {
                            startDisconnectTimer(timeCountCur);
                        } else {
                            formatConnectButton(timeCountCur);
                        }

                        // 保存已链接记录
                        if (!TextUtils.isEmpty(remoteBTAdd)) {
                            saveConnectedDevice(remoteBTAdd, remoteBTName);
                        }


                        mIsActiveDisconnect = false;
                        mReConnectCount = RECONNECT_NUM;
                    } else if (connectionStatus == ConnectionStatus.DISCONNECTED) {
                        cancelDisconnectTimer();
                        formatConnectButton(timeCountCur);
                        if (device != null) {
                            remoteBTName = device.getName();
                            remoteBTAdd = device.getAddress();
//                            if (shouldShowDisconnected())
                            tvAddress.setText(String.format("%s(%s)\ndisconnected", remoteBTName, remoteBTAdd));
                        } else {
//                            if (shouldShowDisconnected())
                            tvAddress.setText("disconnected");
                        }
                        if (shouldShowDisconnected())
                            showToast(R.string.disconnect);

                        boolean reconnect = SPUtils.getInstance(getApplicationContext()).getSPBoolean(SPUtils.AUTO_RECONNECT, false);
                        if (mDevice != null && reconnect) {
                            reConnect(mDevice.getAddress()); // 重连
                        }
                    }

                    for (IConnectStatus iConnectStatus : connectStatusList) {
                        if (iConnectStatus != null) {
                            iConnectStatus.getStatus(connectionStatus);
                        }
                    }
                }
            });
        }
    }

    public void saveConnectedDevice(String address, String name) {
        List<String[]> list = FileUtils.readXmlList();
        for (int k = 0; k < list.size(); k++) {
            if (address.equals(list.get(k)[0])) {
                list.remove(list.get(k));
                break;
            }
        }
        String[] strArr = new String[]{address, name};
        list.add(0, strArr);
        FileUtils.saveXmlList(list);
    }


    protected void initUI() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        db = new DataBase(this);


        tvAddress = (TextView) findViewById(R.id.tvAddress);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(this);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);

        fm = getSupportFragmentManager();
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, fm, R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.title_setUser)).setIndicator(getString(R.string.title_setUser)), SetUser.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.title_marking)).setIndicator(getString(R.string.title_marking)), UHFReadTagFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.title_inventory)).setIndicator(getString(R.string.title_inventory)), UHFInventoryFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.inventory1c)).setIndicator(getString(R.string.inventory1c)), Import1CFragment.class, null);
//        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.title_inventory2)).setIndicator(getString(R.string.title_inventory2)), UHFNewReadTagFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.title_2d_Scan)).setIndicator(getString(R.string.title_2d_Scan)), BarcodeFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.uhf_msg_tab_set)).setIndicator(getString(R.string.uhf_msg_tab_set)), UHFSetFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.result_table)).setIndicator(getString(R.string.result_table)), ResultFragment.class, null);
//        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.uhf_msg_tab_read)).setIndicator(getString(R.string.uhf_msg_tab_read)), UHFReadFragment.class, null);

//        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.uhf_msg_tab_write)).setIndicator(getString(R.string.uhf_msg_tab_write)), UHFWriteFragment.class, null);

//        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.uhf_msg_tab_lock)).setIndicator(getString(R.string.uhf_msg_tab_lock)), UHFLockFragment.class, null);

//        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.uhf_msg_tab_kill)).setIndicator(getString(R.string.uhf_msg_tab_kill)), UHFKillFragment.class, null);
//
//        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.uhf_msg_tab_erase)).setIndicator(getString(R.string.uhf_msg_tab_erase)), UHFEraseFragment.class, null);

//        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.title_update)).setIndicator(getString(R.string.title_update)), UHFUpdataFragment.class, null);

//        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.title_bt_rename)).setIndicator(getString(R.string.title_bt_rename)), BTRenameFragment.class, null);
    }

    public void updateConnectMessage(String oldName, String newName) {
        if (!TextUtils.isEmpty(oldName) && !TextUtils.isEmpty(newName)) {
            tvAddress.setText(tvAddress.getText().toString().replace(oldName, newName));
            remoteBTName = newName;
        }
    }

    //------------连接状态监听-----------------------
    private List<IConnectStatus> connectStatusList = new ArrayList<>();

    public void addConnectStatusNotice(IConnectStatus iConnectStatus) {
        connectStatusList.add(iConnectStatus);
    }

    public void removeConnectStatusNotice(IConnectStatus iConnectStatus) {
        connectStatusList.remove(iConnectStatus);
    }

    public interface IConnectStatus {
        void getStatus(ConnectionStatus connectionStatus);
    }

    //------------------获取定位权限--------------------------------
    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST = 100;
    private static final int REQUEST_ACTION_LOCATION_SETTINGS = 3;

    private void checkLocationEnable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_PERMISSION_REQUEST);
            }
        }
        if (!isLocationEnabled()) {
            Utils.alert(this, R.string.get_location_permission, getString(R.string.tips_open_the_ocation_permission), R.drawable.webtext, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, REQUEST_ACTION_LOCATION_SETTINGS);
                }
            });
        }
    }

    private boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private void startDisconnectTimer(long time) {
        timeCountCur = time;
        timerTask = new DisconnectTimerTask();
        mDisconnectTimer.schedule(timerTask, 0, period);
    }

    public void cancelDisconnectTimer() {
        timeCountCur = 0;
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private class DisconnectTimerTask extends TimerTask {

        @Override
        public void run() {
            Log.e(TAG, "timeCountCur = " + timeCountCur);
            Message msg = mHandler.obtainMessage(RUNNING_DISCONNECT_TIMER, timeCountCur);
            mHandler.sendMessage(msg);
            if (isScanning) {
                resetDisconnectTime();
            } else if (timeCountCur <= 0) {
                disconnect(true);
            }
            timeCountCur -= period;
        }
    }


}

package com.example.uhf_bt.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.uhf_bt.DataBase;
import com.example.uhf_bt.EditExecutor;
import com.example.uhf_bt.EditPremise;
import com.example.uhf_bt.InsertFacility;
import com.example.uhf_bt.InsertPremise;
import com.example.uhf_bt.MainActivity;
import com.example.uhf_bt.Models.Executor;
import com.example.uhf_bt.Models.Facility;
import com.example.uhf_bt.Models.Premise;
import com.example.uhf_bt.R;
import com.example.uhf_bt.SettingsOfUser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SetUser extends Fragment {

    private Spinner spnFacility;
    private Spinner spnPremise;
    private Spinner spnUser;

    private Button setSettingsOfUser, editUser, deleteUser,
            setFacility, editFacility, deleteFacility,
            setPremise, editPremise, deletePremise, btnImport;

    MainActivity context;
    private DataBase db;
    List<Premise> premises;
    ArrayAdapter<Premise> premiseAdapter;
    private List<Facility> facilities;
    private List<Executor> users;
    public static final int requestcode = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for context fragment
        View view = inflater.inflate(R.layout.fragment_set_user, container, false);
        init(view);

        fillSpinner();
        return view;
    }

    private void init(View view) {
        // facility layout
        spnFacility = (Spinner) view.findViewById(R.id.spnFacility);
        setFacility = view.findViewById(R.id.btnSetFacility);
        editFacility = view.findViewById(R.id.editFacility);
        deleteFacility = view.findViewById(R.id.deleteFacility);
        // premise layout
        spnPremise = (Spinner) view.findViewById(R.id.spnPremise);
        setPremise = view.findViewById(R.id.btnSetPremise);
        editPremise = view.findViewById(R.id.editPremise);
        deletePremise = view.findViewById(R.id.deletePremise);
        // user layout
        setSettingsOfUser = view.findViewById(R.id.btnSetNewUser);
        editUser = view.findViewById(R.id.editUser);
        deleteUser = view.findViewById(R.id.deleteUser);
        spnUser = (Spinner) view.findViewById(R.id.spnUser);
        // others
        db = new DataBase(getActivity());
        facilities = db.getFacilities();
        users = db.getUsers();
        btnImport = view.findViewById(R.id.btnImport);

        // User settings
        setSettingsOfUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newUser = new Intent(context, SettingsOfUser.class);
                startActivity(newUser);
            }
        });


        // Facility settings
        setFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newFacility = new Intent(context, InsertFacility.class);
                startActivity(newFacility);
            }
        });

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fileIntent.setType("*/*");
                fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(fileIntent, requestcode);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No activity", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Premise settings
        setPremise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPremise = new Intent(context, InsertPremise.class);
                startActivity(newPremise);
            }
        });

    }

    private class ImportDataTask extends Thread {

        private final Context mContext;
        private final Uri mDataUri;
        private final int mResultCode;



        public ImportDataTask(Context context, Uri dataUri, int resultCode) {
            mContext = context;
            mDataUri = dataUri;
            mResultCode = resultCode;
        }

        @Override
        public void run() {
            super.run();
            String filePath = mDataUri.getPath();
            Log.e("New file path", filePath);
            if (filePath.contains("/root_path"))
                filePath = filePath.replace("/root_path", "");
            System.out.println(filePath);
            Log.e("New File path", filePath);
            db = new DataBase(mContext);
            SQLiteDatabase database = db.getWritableDatabase();
            database.execSQL("DELETE FROM inventory_table_1c");
            try {
                if (mResultCode == RESULT_OK) {
                    Log.e("RESULT CODE", "OK");
                    try {
                        InputStream inputStream = mContext.getContentResolver().openInputStream(mDataUri);
                        BufferedReader bfReader = new BufferedReader(new InputStreamReader(inputStream));
                        ContentValues contentValues = new ContentValues();
                        String line = "";
                        database.beginTransaction();
                        while ((line = bfReader.readLine()) != null) {
                            Log.e("line", line);
                            String[] str = line.split(",", 11); // defining 11 columns with null or blank field
                            int id = Integer.parseInt(str[0]);
                            String epc = str[1];
                            String type = str[2];
                            String description = str[3];
                            String invNumber = str[4];
                            String nomenclature = str[5];
                            int amount = Integer.parseInt(str[6]);
                            String facility = str[7];
                            String premise = str[8];
                            String dateTime = str[9];
                            String executor = str[10];

                            contentValues.put(DataBase.db_table_1c_id, id);
                            contentValues.put(DataBase.db_table_1c_epc, epc);
                            contentValues.put(DataBase.db_table_1c_type, type);
                            contentValues.put(DataBase.db_table_1c_description, description);
                            contentValues.put(DataBase.db_table_1c_inventory_number, invNumber);
                            contentValues.put(DataBase.db_table_1c_nomenclature, nomenclature);
                            contentValues.put(DataBase.db_table_1c_amount, amount);
                            contentValues.put(DataBase.db_table_1c_facility, facility);
                            contentValues.put(DataBase.db_table_1c_premise, premise);
                            contentValues.put(DataBase.db_table_1c_datetime, dateTime);
                            contentValues.put(DataBase.db_table_1c_executor, executor);
                            database.insert(DataBase.db_table_inventory_1c, null, contentValues);

                            Toast.makeText(mContext, "Successfully imported", Toast.LENGTH_SHORT).show();

                            Log.e("Import: ", "Successfully imported");
                        }

                        database.setTransactionSuccessful();
                        database.endTransaction();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    Log.e("RESULT CODE", "InValid");
                    if (database.inTransaction())

                        database.endTransaction();
                    Toast.makeText(mContext, "Only CSV files allowed.", Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage().toString());
                if (database.inTransaction())

                    database.endTransaction();

                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestcode) {
            ImportDataTask importDataTask = new ImportDataTask(context, data.getData(), resultCode);
            importDataTask.run();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MainActivity) getActivity();
    }

    public void fillSpinner() {
        ArrayAdapter<Facility> facilityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, facilities);
        spnFacility.setAdapter(facilityAdapter);

        spnFacility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // When a facility is selected, get the corresponding premises and users
                Facility selectedFacility = (Facility) parentView.getItemAtPosition(position);

                if (selectedFacility != null) {
                    int facility_id = selectedFacility.getId();
                    // Get premises for the selected facility
                    premises = db.getPremisesByFacility(facility_id);
                    premiseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, premises);
                    spnPremise.setAdapter(premiseAdapter);

                    editFacility.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, InsertFacility.class);
                            intent.putExtra("facility_id", selectedFacility.getId());
                            startActivity(intent);
                        }
                    });
                    premiseAdapter.notifyDataSetChanged();


                }


                deleteFacility.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Do you want to delete the facility? ");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                assert selectedFacility != null;
                                if (db.deleteFacility(selectedFacility)) {
                                    Toast.makeText(context, "Facility was deleted", Toast.LENGTH_SHORT).show();
                                    facilities.remove(selectedFacility);
                                    facilityAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "You have cancelled the action", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Deleting the facility");
                        alert.show();

                    }
                });
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(context, "You have to choose at least one item", Toast.LENGTH_LONG).show();
            }
        });

        // Get users

        ArrayAdapter<Executor> executorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, users);
        spnUser.setAdapter(executorAdapter);
        spnUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Executor user = (Executor) parent.getItemAtPosition(position);
                editUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent editUser = new Intent(context, EditExecutor.class);
                        editUser.putExtra("userToEdit", user.getName());
                        startActivity(editUser);
                    }
                });

                deleteUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Do you want to delete the user? ");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (db.deleteUser(user.getName())) {
                                    Toast.makeText(context, "User was deleted", Toast.LENGTH_SHORT).show();
                                    users.remove(user);
                                    executorAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "You have cancelled the action", Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Deleting user");
                        alert.show();

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(context, "You have to choose at least one item", Toast.LENGTH_SHORT).show();
            }
        });

        spnPremise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Premise premise = (Premise) parent.getItemAtPosition(position);

                editPremise.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent editPremise = new Intent(context, EditPremise.class);
                        editPremise.putExtra("premiseName", premise.getName());
                        startActivity(editPremise);
                    }
                });
                deletePremise.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Do you want to delete the premise?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (db.deletePremise(premise)) {
                                    Toast.makeText(context, "Premise was deleted", Toast.LENGTH_SHORT).show();
                                    premises.remove(premise);
                                    premiseAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "You have cancelled the action", Toast.LENGTH_SHORT).show();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.setTitle("Deleting the premise");
                        alert.show();

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        fillSpinner();

        db.getFacilities();
    }
}
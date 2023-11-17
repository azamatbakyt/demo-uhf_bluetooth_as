package com.example.uhf_bt.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.UserHandle;
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

import java.util.ArrayList;
import java.util.List;

public class SetUser extends Fragment {

    private Spinner spnFacility;
    private Spinner spnPremise;
    private Spinner spnUser;

    private Button setSettingsOfUser, editUser, deleteUser,
            setFacility, editFacility, deleteFacility,
            setPremise, editPremise, deletePremise;

    MainActivity context;
    private DataBase db;
    List<Premise> premises;
    ArrayAdapter<Premise> premiseAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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


        // Premise settings
        setPremise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPremise = new Intent(context, InsertPremise.class);
                startActivity(newPremise);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MainActivity) getActivity();
    }

    public void fillSpinner() {
        List<Facility> facilities = db.getFacilities();
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
        List<Executor> executors = db.getUsers();
        ArrayAdapter<Executor> executorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, executors);
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
                                    executors.remove(user);
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
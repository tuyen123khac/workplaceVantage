package com.tuyenvo.tmavantage.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.tuyenvo.tmavantage.R;

import java.util.Calendar;

public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private ImageView dropDownBirthDay, editUsername;
    private TextView dateOfBirth, userName, statusMessage;
    private Spinner genderSpinner;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//Prevent Keyboard push the tab
        dropDownBirthDay = fragmentView.findViewById(R.id.dropDownBirthDay);
        dateOfBirth = fragmentView.findViewById(R.id.dateOfBirth);
        userName = fragmentView.findViewById(R.id.userName);
        editUsername = fragmentView.findViewById(R.id.editUsername);
        statusMessage = fragmentView.findViewById(R.id.statusMessage);

        dropDownBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateOfBirth.setText(dayOfMonth + "/" + month + "/" + year);
            }
        };

        genderSpinner = fragmentView.findViewById(R.id.dropDownGender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.gender, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(this);

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder userNameEditingDialog = new AlertDialog.Builder(getContext());
                userNameEditingDialog.setTitle("Changing Username");

                final EditText weightInput = new EditText(getContext());
                weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
                userNameEditingDialog.setView(weightInput);

                userNameEditingDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userName.setText(weightInput.getText().toString());
                    }
                });

                userNameEditingDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                userNameEditingDialog.show();
            }
        });

        statusMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder userNameEditingDialog = new AlertDialog.Builder(getContext());
                userNameEditingDialog.setTitle("Changing Status");

                final EditText weightInput = new EditText(getContext());
                weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
                userNameEditingDialog.setView(weightInput);

                userNameEditingDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        statusMessage.setText(weightInput.getText().toString());
                    }
                });

                userNameEditingDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                userNameEditingDialog.show();
            }
        });

        return fragmentView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
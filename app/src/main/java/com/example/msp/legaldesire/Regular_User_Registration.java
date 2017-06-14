package com.example.msp.legaldesire;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Regular_User_Registration extends Fragment {
    private DatabaseReference mDatabase;
    Button mSubmit;
    EditText mNameField, mContactField, mCityField, mContact1, mContact2, mContact3, mContact4, mContact5;
    String mEmail, mUserId,mName,mProfilePic;

    public Regular_User_Registration() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.regular__user__registration, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");

        Bundle bundle = this.getArguments();
        mEmail = bundle.getString("Email");
        mUserId = bundle.getString("User ID");
        mName = bundle.getString("Name");
        mProfilePic = bundle.getString("Photo");
        Log.d("checkifname",mEmail +"  "+ mUserId);
        mContactField = (EditText) view.findViewById(R.id.editText32);
        mCityField = (EditText) view.findViewById(R.id.editText42);
        mContact1 = (EditText) view.findViewById(R.id.editText52);
        mContact2 = (EditText) view.findViewById(R.id.editText62);
        mContact3 = (EditText) view.findViewById(R.id.editText72);
        mContact4 = (EditText) view.findViewById(R.id.editText82);
        mContact5 = (EditText) view.findViewById(R.id.editText92);
        mSubmit = (Button) view.findViewById(R.id.btn_lawyer_submit2);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContactField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Enter your Contact Number", Toast.LENGTH_SHORT).show();
                } else if (mCityField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Enter City", Toast.LENGTH_SHORT).show();
                } else if (mContact1.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Enter your Contact 1", Toast.LENGTH_SHORT).show();
                } else if (mContact2.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Enter your Contact 2", Toast.LENGTH_SHORT).show();
                } else if (mContact3.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Enter your Contact 3", Toast.LENGTH_SHORT).show();
                } else if (mContact4.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Enter your Contact 4", Toast.LENGTH_SHORT).show();
                } else if (mContact5.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Enter your Contact 5", Toast.LENGTH_SHORT).show();
                } else {


                    HashMap<String, Object> insertData = new HashMap<String, Object>();
                    insertData.put("User ID", mUserId);
                    insertData.put("Email", mEmail);
                    insertData.put("Name", mName);
                    insertData.put("Profile_Pic",mProfilePic);
                    insertData.put("Contact", mContactField.getText().toString().trim());
                    insertData.put("City", mCityField.getText().toString().trim());
                    insertData.put("Emergency Contact 1", mContact1.getText().toString().trim());
                    insertData.put("Emergency Contact 2", mContact2.getText().toString().trim());
                    insertData.put("Emergency Contact 3", mContact3.getText().toString().trim());
                    insertData.put("Emergency Contact 4", mContact4.getText().toString().trim());
                    insertData.put("Emergency Contact 5", mContact5.getText().toString().trim());

                    mDatabase.child(mUserId).setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                getActivity().startActivity(new Intent(getActivity(), Login.class));

                            } else {
                                Toast.makeText(getContext(), "Failed to Register", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }

            }
        });


        return view;
    }
}

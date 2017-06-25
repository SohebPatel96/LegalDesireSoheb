package com.example.msp.legaldesire;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class My_Appointment extends Fragment {
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Appointment");
    ArrayList<String> mAppointmentDate = new ArrayList<>();
    ArrayList<String> mAppointmentTime = new ArrayList<>();
    ArrayList<String> mAppointmentType = new ArrayList<>();
    ArrayList<String> mLawyerNames = new ArrayList<>();
    ArrayList<String> mOfficeLocation = new ArrayList<>();
    String mLawyerID, mUserID, mUserName, mLawyerName;
    boolean isLawyer = false;

    ListView listView;
    My_Appointment_Regular_Adapter my_appointment_adapter;

    public My_Appointment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mUserID = bundle.getString("User ID");
        isLawyer = bundle.getBoolean("isLawyer");
        ((OnLoginSuccessful)getActivity()).setActionBarTitle("My Appointments");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.my_appointment, container, false);
        listView = (ListView) view.findViewById(R.id.list_appointments);
        if (isLawyer) {
            Log.d("appointment123","inside Lawyer");
            final My_Appointment_Regular_Adapter my_appointment_regular_adapter = new My_Appointment_Regular_Adapter(getContext(), mAppointmentDate, mAppointmentTime, mAppointmentType, mLawyerNames, mOfficeLocation,isLawyer);
            listView.setAdapter(my_appointment_regular_adapter);
            final DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer").child(mUserID).child("Appointment");
            user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        mAppointmentDate.add(postSnapshot.child("Appointment Date").getValue(String.class));
                        mAppointmentTime.add(postSnapshot.child("Appointment Time").getValue(String.class));
                        mAppointmentType.add(postSnapshot.child("Appointment Type").getValue(String.class));
                        mLawyerNames.add(postSnapshot.child("User Name").getValue(String.class));
                        mOfficeLocation.add(postSnapshot.child("Lawyer Office Address").getValue(String.class));
                        my_appointment_regular_adapter.notifyDataSetChanged();
                        Log.d("appointment123","inside loop");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Log.d("appointment123","inside Regular");
            final My_Appointment_Regular_Adapter my_appointment_regular_adapter = new My_Appointment_Regular_Adapter(getContext(), mAppointmentDate, mAppointmentTime, mAppointmentType, mLawyerNames, mOfficeLocation,isLawyer);
            listView.setAdapter(my_appointment_regular_adapter);
            final DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("User").child("Regular").child(mUserID).child("Appointment");
            user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        mAppointmentDate.add(postSnapshot.child("Appointment Date").getValue(String.class));
                        mAppointmentTime.add(postSnapshot.child("Appointment Time").getValue(String.class));
                        mAppointmentType.add(postSnapshot.child("Appointment Type").getValue(String.class));
                        mLawyerNames.add(postSnapshot.child("Lawyer Name").getValue(String.class));
                        mOfficeLocation.add(postSnapshot.child("Lawyer Office Address").getValue(String.class));
                        my_appointment_regular_adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        //my_appointment_adapter = new My_Appointment_Regular_Adapter(getContext());
        return view;
    }

}

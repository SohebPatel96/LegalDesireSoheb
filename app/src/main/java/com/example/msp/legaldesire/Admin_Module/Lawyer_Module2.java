package com.example.msp.legaldesire.Admin_Module;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.msp.legaldesire.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Lawyer_Module2 extends Fragment {
    public static final String TAG = "lawyermodule2";
    ListView mListView;
    Lawyer_Module2_Adapter lawyer_module2_adapter;
    private DatabaseReference mDatabase;

    ArrayList<String> key = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> email = new ArrayList<>();
    ArrayList<String> address = new ArrayList<>();
    ArrayList<Double> rating = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();
    ArrayList<Double> latitude = new ArrayList<>();
    ArrayList<Double> longitude = new ArrayList<>();

    public Lawyer_Module2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.lawyer__module2, container, false);
        mListView = (ListView) view.findViewById(R.id.list_viewdata);
        name.add("sssss");
        lawyer_module2_adapter = new Lawyer_Module2_Adapter(getContext(), name);
        mListView.setAdapter(lawyer_module2_adapter);


        return view;
    }

}

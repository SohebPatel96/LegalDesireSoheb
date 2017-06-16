package com.example.msp.legaldesire;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class Lawyer_User_registration extends Fragment {

    public static String TAG = "lawyermodule123";
    private DatabaseReference mDatabase;
    Button mSubmit;
    EditText mContactField, mCityField, mContact1, mContact2, mContact3, mContact4, mContact5;
    RadioButton mCivil, mCorporate, mCriminal, mAll;
    String mType, mUserId, mEmail,mName,mProfilePic;
    double mLatitude = 0, mLongitude = 0;
    GoogleMap mGoogleMap;
    MapView mMapView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.lawyer__user_registration, container, false);
        //Init all the fields-EditText
        Bundle bundle = this.getArguments();
        mEmail = bundle.getString("Email");
        mUserId = bundle.getString("User ID");
        mName = bundle.getString("Name");
        mProfilePic = bundle.getString("Photo");
        Log.d("checkifname",mEmail +"  "+ mUserId);
        mContactField = (EditText) view.findViewById(R.id.editText3);
        mCityField = (EditText) view.findViewById(R.id.editText4);
        mCivil = (RadioButton) view.findViewById(R.id.radio_civil);
        mCorporate = (RadioButton) view.findViewById(R.id.radio_corporate);
        mCriminal = (RadioButton) view.findViewById(R.id.radio_criminal);
        mAll = (RadioButton) view.findViewById(R.id.radio_all);
        mMapView = (MapView) view.findViewById(R.id.map_View);
        mContact1 = (EditText) view.findViewById(R.id.editText5);
        mContact2 = (EditText) view.findViewById(R.id.editText6);
        mContact3 = (EditText) view.findViewById(R.id.editText7);
        mContact4 = (EditText) view.findViewById(R.id.editText8);
        mContact5 = (EditText) view.findViewById(R.id.editText9);
        mSubmit = (Button) view.findViewById(R.id.btn_lawyer_submit);

        mAll.setChecked(true);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mGoogleMap.clear();
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        mLatitude = latLng.latitude;
                        mLongitude = latLng.longitude;
                        mGoogleMap.addMarker(markerOptions);
                    }
                });
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //User has previously accepted this permission
                    if (ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    //Not in api-23, no need to prompt
                    mGoogleMap.setMyLocationEnabled(true);
                }
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if (mContactField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Enter your Contact Number", Toast.LENGTH_SHORT).show();
                } else if (mCityField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Enter City", Toast.LENGTH_SHORT).show();
                } else if (mLatitude == 0 && mLongitude == 0) {
                    Toast.makeText(getContext(), "Enter your Office Location", Toast.LENGTH_SHORT).show();
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
                    if (mCivil.isChecked())
                        mType = "Civil";
                    else if (mCorporate.isChecked())
                        mType = "Corporate";
                    else if (mCriminal.isChecked())
                        mType = "Criminal";
                    else
                        mType = "--";

                    HashMap<String, Object> insertData = new HashMap<String, Object>();
                    insertData.put("User ID", mUserId);
                    insertData.put("Email", mEmail);
                    insertData.put("Name", mName);
                    insertData.put("Profile_Pic",mProfilePic);
                    insertData.put("Contact", mContactField.getText().toString().trim());
                    insertData.put("City", mCityField.getText().toString().trim());
                    insertData.put("Type", mType);
                    insertData.put("Latitude", mLatitude);
                    insertData.put("Longitude", mLongitude);
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

        //    final URI personPhoto = URI.create(preferences.getString("person_photo", null));




        /*mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> insertData = new HashMap<String, String>();
                insertData.put("Email", mEmailField.getText().toString().trim());
                insertData.put("Name", mNameField.getText().toString().trim());
                insertData.put("Address", mAddressField.getText().toString().trim());
                insertData.put("Rating", mRatingField.getText().toString().trim());
                insertData.put("Type", mTypeField.getSelectedItem().toString().trim());
                insertData.put("Latitude", mLatitudeField.getText().toString().trim());
                insertData.put("Longitude", mLongitudeField.getText().toString().trim());
                insertData.put("profile_pic", personPhoto.toString());


                mDatabase.push().setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                            getActivity().startActivity(new Intent(getActivity(), OnLoginSuccessful.class));

                        } else {
                            Toast.makeText(getContext(), "Failed to Register", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });*/
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //
                    //     Toast.makeText(OnLoginSuccessful.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

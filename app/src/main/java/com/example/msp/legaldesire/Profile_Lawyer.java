package com.example.msp.legaldesire;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_Lawyer extends Fragment {
    final String TAG = "profilelawyer123";
    String mEmail, mName, mCity, mContact, mUserID, mProfileUri, mType;
    Uri mProfilepic;
    ImageView mImageProfilePic;
    TextView mEmailText, mNameText, mCityText, mContactText, mTypeText;
    GoogleMap mGoogleMap;
    MapView mMapView;
    double latitude, longitude;
    DatabaseReference mDatabase;

    public Profile_Lawyer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mUserID = bundle.getString("User ID");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile__lawyer, container, false);
        mImageProfilePic = (ImageView) view.findViewById(R.id.lawyerprofilepic);
        mEmailText = (TextView) view.findViewById(R.id.lawyertxt_email);
        mNameText = (TextView) view.findViewById(R.id.lawyertxt_name);
        mContactText = (TextView) view.findViewById(R.id.lawyertxt_contact);
        mTypeText = (TextView) view.findViewById(R.id.lawyertxt_type);
        mMapView = (MapView) view.findViewById(R.id.map_View2);

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  mProfilepic = Uri.parse(mProfileUri);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
        mDatabase.child(mUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEmail = (String) dataSnapshot.child("Email").getValue();
                mName = (String) dataSnapshot.child("Name").getValue();
                mContact = (String) dataSnapshot.child("Contact").getValue();
                mProfilepic = Uri.parse(dataSnapshot.child("Profile_Pic").getValue(String.class));
                mCity = (String) dataSnapshot.child("City").getValue();
                latitude = dataSnapshot.child("Latitude").getValue(double.class);
                longitude = dataSnapshot.child("Longitude").getValue(double.class);
                mType = (String) dataSnapshot.child("Type").getValue();
                if (mProfilepic.toString().equals("empty")) {
                    mImageProfilePic.setImageResource(R.drawable.empty_profile);
                } else {
                    Picasso.with(getContext()).load(mProfilepic).into(mImageProfilePic);
                }
                mEmailText.setText(mEmail);
                mNameText.setText(mName);
                mContactText.setText(mContact);
                mTypeText.setText(mType);

                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .title(mName));

                        CameraUpdate center=
                                CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude));
                        CameraUpdate zoom= CameraUpdateFactory.zoomTo(15);

                        googleMap.moveCamera(center);
                        googleMap.animateCamera(zoom);
                    }
                });
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}

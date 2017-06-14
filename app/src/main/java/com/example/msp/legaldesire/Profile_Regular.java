package com.example.msp.legaldesire;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class Profile_Regular extends Fragment {
    String mUserID;
    String mEmail, mName, mCity, mContact;
    Uri mProfilepic;
    CircleImageView mImageProfilePic;
    TextView mEmailText, mNameText, mCityText, mContactText;
    DatabaseReference mDatabase;



    public Profile_Regular() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        mUserID = bundle.getString("User ID");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile__regular, container, false);
        mImageProfilePic = (CircleImageView) view.findViewById(R.id.profilepic);
        mEmailText = (TextView) view.findViewById(R.id.txt_email);
        mNameText = (TextView) view.findViewById(R.id.txt_name);
        mCityText = (TextView) view.findViewById(R.id.txt_city);
        mContactText = (TextView) view.findViewById(R.id.txt_contact);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");
        mDatabase.child(mUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEmail = (String) dataSnapshot.child("Email").getValue();
                mName = (String) dataSnapshot.child("Name").getValue();
                mContact = (String) dataSnapshot.child("Contact").getValue();
                mProfilepic = Uri.parse(dataSnapshot.child("Profile_Pic").getValue(String.class));
                if(mProfilepic.toString().equals("empty")){
                    mImageProfilePic.setImageResource(R.drawable.empty_profile);
                }
                else{
                    Picasso.with(getContext()).load(mProfilepic).into(mImageProfilePic);
                }
                mCity = (String) dataSnapshot.child("City").getValue();
                mEmailText.setText(mEmail);
                mNameText.setText(mName);
                mCityText.setText(mCity);
                mContactText.setText(mContact);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}

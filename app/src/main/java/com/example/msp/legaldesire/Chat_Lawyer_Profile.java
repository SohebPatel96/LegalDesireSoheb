package com.example.msp.legaldesire;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat_Lawyer_Profile extends Fragment {

    final String TAG = "profilelawyer123";
    String mEmail, mName, mCity, mContact, mUserID, mLawyerID, mUserName, mProfileUri, mType, lawyer_profile_pic, user_profile_pic;
    Uri mProfilepic;
    boolean isLawyer;
    ImageView mImageProfilePic;
    Button mAddChat;
    boolean mIfChatExist;
    TextView mEmailText, mNameText, mCityText, mContactText, mTypeText;
    double latitude, longitude;
    DatabaseReference mDatabase;


    public Chat_Lawyer_Profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mUserID = bundle.getString("User ID");
        mLawyerID = bundle.getString("Lawyer ID");
        isLawyer = bundle.getBoolean("isLawyer");
        Log.d("checkthis123", mUserID + "  :" + mLawyerID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat__lawyer__profile, container, false);
        mImageProfilePic = (ImageView) view.findViewById(R.id.lawyerprofilepic);
        mEmailText = (TextView) view.findViewById(R.id.lawyertxt_email);
        mNameText = (TextView) view.findViewById(R.id.lawyertxt_name);
        mContactText = (TextView) view.findViewById(R.id.lawyertxt_contact);
        mTypeText = (TextView) view.findViewById(R.id.lawyertxt_type);
        mCityText = (TextView) view.findViewById(R.id.lawyertxt_city);
        mAddChat = (Button) view.findViewById(R.id.btn_addtochat);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
        mDatabase.child(mLawyerID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEmail = (String) dataSnapshot.child("Email").getValue();
                Log.d("checkthis123", mEmail);
                mName = (String) dataSnapshot.child("Name").getValue();
                Log.d("checkthis123", mName);
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
                mCityText.setText(mCity);
                mTypeText.setText(mType);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAddChat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (isLawyer) {
                                                Toast.makeText(getContext(), "Permission Denied:Lawyers cannot add to Chatlist", Toast.LENGTH_SHORT).show();
                                            } else {
                                                final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat");
                                                final DatabaseReference root2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
                                                final DatabaseReference root3 = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");
                                                root2.child(mLawyerID).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        lawyer_profile_pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                root3.child(mUserID).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        user_profile_pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                                                        mUserName = dataSnapshot.child("Name").getValue(String.class);

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                root.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                            String lawyer_id = postSnapshot.child("Lawyer ID").getValue(String.class);
                                                            String user_id = postSnapshot.child("User ID").getValue(String.class);
                                                            if (lawyer_id.equals(mLawyerID) && user_id.equals(mUserID)) {
                                                                mIfChatExist = true;
                                                                Toast.makeText(getContext(), "Lawyer already added to chat list", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                        if (!mIfChatExist) {
                                                            Log.d(TAG, "Chat conversation doesn't exist");
                                                            HashMap<String, Object> Message = new HashMap<String, Object>();
                                                            HashMap<String, Object> map = new HashMap<String, Object>();
                                                            map.put("Lawyer ID", mLawyerID);
                                                            map.put("Lawyer Name", mName);
                                                            map.put("User ID", mUserID);
                                                            map.put("User Name", mUserName);
                                                            map.put("Message", Message);
                                                            map.put("Lawyer Seen", true);
                                                            map.put("User Seen", true);
                                                            map.put("Lawyer profile", lawyer_profile_pic);
                                                            map.put("User profile", user_profile_pic);


                                                            root.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getContext(), "Lawyer added to chat list", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Log.d(TAG, "Failed to add lawyer to chat list, an unknown error occured");
                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }

        );
        return view;
    }

}

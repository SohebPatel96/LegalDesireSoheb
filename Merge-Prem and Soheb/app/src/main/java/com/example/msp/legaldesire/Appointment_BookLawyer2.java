package com.example.msp.legaldesire;


import android.content.Intent;
import android.location.Location;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Appointment_BookLawyer2 extends Fragment {
    public final String TAG = "appointment123";
    public final long ONEMONTH = 2592000;
    public final long ONEDAY = 86400;
    String mUserID, mLawyerID, mUserName, mLawyerName, mUserAddress;
    CircleImageView mImageProfilePic;
    TextView mNameText;
    EditText mEditUserAddress;
    Button mConfirmBooking, mSelectLocation;
    DatePicker datePicker;
    NumberPicker numberPicker;
    double mLatitude = 0, mLongitude = 0;
    GoogleMap mGoogleMap;
    MapView mMapView;

    String mAppointmentDate, mAppointmentTime;

    DatabaseReference mDatabase;

    final DatabaseReference root2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
    final DatabaseReference root3 = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");


    int mDate, mMonth, mYear;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mUserID = bundle.getString("User ID");
        mLawyerID = bundle.getString("Lawyer ID");
        mLatitude = bundle.getDouble("Latitude");
        mLongitude = bundle.getDouble("Longitude");
        Log.d(TAG, "inside oncreate");
        Log.d(TAG, "Longitude,latitude" + mLongitude + "," + mLatitude);
//        ((OnLoginSuccessful) getActivity()).setActionBarTitle("Book Appointment");

    }


    public Appointment_BookLawyer2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.appointment__book_lawyer2, container, false);
        mImageProfilePic = (CircleImageView) view.findViewById(R.id.lawyerprofilepic);
        mNameText = (TextView) view.findViewById(R.id.lawyertxt_name);
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        mSelectLocation = (Button) view.findViewById(R.id.btn_select_location);

        mSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int date = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                Log.d(TAG, "Selected date,month,year:" + date + "," + month + " ," + year);
                mAppointmentDate = date + "/" + month + "/" + year;
                int time = numberPicker.getValue();
                mAppointmentTime = appointmentTime(time);
                Intent intent = new Intent(getActivity(), Select_Location.class);
                intent.putExtra("Appointment date", mAppointmentDate);
                intent.putExtra("Appointment time", mAppointmentTime);
                intent.putExtra("Latitude", mLatitude);
                intent.putExtra("Longitude", mLongitude);
                intent.putExtra("User ID", mUserID);
                intent.putExtra("Lawyer ID", mLawyerID);
                intent.putExtra("User Name", mUserName);
                intent.putExtra("Lawyer Name", mLawyerName);
                startActivity(intent);
            }
        });

/*        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(getContext().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(mLatitude, mLongitude)).title("Lawyer Location");
                mGoogleMap.addMarker(markerOptions);
                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mGoogleMap.clear();
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(mLatitude, mLongitude)).title("Lawyer Location");
                        mGoogleMap.addMarker(markerOptions);



                        MarkerOptions markerOptions2 = new MarkerOptions();
                        markerOptions2.position(latLng).title("My Location");
                        mGoogleMap.addMarker(markerOptions2);

                        Location lawyer_location = new Location("Lawyer Location");
                        lawyer_location.setLatitude(mLatitude);
                        lawyer_location.setLongitude(mLongitude);

                        Location user_location = new Location("User Location");
                        user_location.setLatitude(latLng.latitude);
                        user_location.setLongitude(latLng.longitude);
                        double distance = lawyer_location.distanceTo(user_location);
                        Log.d(TAG,"Distance:" + distance);
                    }
                });
            }
        });
        */

        Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar2.clear();
        long now = System.currentTimeMillis() + 86400000;
        datePicker.setMinDate(now);
        datePicker.setMaxDate(now + (1000 * 60 * 60 * 24 * 7));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(7);
        numberPicker.setDisplayedValues(new String[]{"9 AM", "10AM", "11AM", "12 PM", "1 PM", "2 PM", "3PM", "4PM", "5PM"});

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDate = calendar.get(Calendar.DATE);

        // datePicker.setMaxDate(ONEMONTH);
        // datePicker.setMinDate(ONEDAY);

        datePicker.updateDate(mYear, mMonth, mDate);


        root2.child(mLawyerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLawyerName = dataSnapshot.child("Name").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        root3.child(mUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserName = dataSnapshot.child("Name").getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
        mDatabase.child(mLawyerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Uri profilepic = Uri.parse(dataSnapshot.child("Profile_Pic").getValue(String.class));
                if (profilepic.toString().equals("empty")) {
                    mImageProfilePic.setImageResource(R.drawable.empty_profile);
                } else {
                    Picasso.with(getContext()).load(profilepic).error(R.drawable.empty_profile).into(mImageProfilePic);
                }
                String name = (String) dataSnapshot.child("Name").getValue();
                mNameText.setText(name);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    public String appointmentTime(int n) {
        String str;
        switch (n) {
            case 0:
                str = "9 AM";
                break;
            case 1:
                str = "10 AM";
                break;
            case 2:
                str = "11 AM";
                break;
            case 3:
                str = "12 PM";
                break;
            case 4:
                str = "1 PM";
                break;
            case 5:
                str = "2 PM";
                break;
            case 6:
                str = "3 PM";
                break;
            case 7:
                str = "4 PM";
                break;
            case 8:
                str = "5 PM";
                break;

            default:
                str = "--";
                break;
        }
        return str;

    }

}

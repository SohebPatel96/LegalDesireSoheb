package com.example.msp.legaldesire;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MSP on 6/24/2017.
 */

public class MarkerWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private View view;
    CircleImageView profileImage;
    TextView infoVerify, infoName, infoType;
    private LayoutInflater inflater = null;
    Context context;
    DatabaseReference mDatabase;
    String id, name, type, profileURL;
    Drawable drawable;


    public MarkerWindowAdapter(Context context) {
        this.context = context;


    }

    @Override
    public View getInfoWindow(final Marker marker) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_marker, null);
        infoName = (TextView) view.findViewById(R.id.text_Name);
        infoType = (TextView) view.findViewById(R.id.text_type);
        profileImage = (CircleImageView) view.findViewById(R.id.profilepic);


        infoName.setText(name);
        infoType.setText(type);
        if (drawable != null)
            profileImage.setImageDrawable(drawable);


        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

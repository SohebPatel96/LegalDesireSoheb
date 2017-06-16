package com.example.msp.legaldesire;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_Lawyer extends Fragment {
    final String TAG = "profilelawyer123";
    private int PICK_IMAGE_REQUEST = 1;
    String mEmail, mName, mCity, mContact, mUserID, mProfileUri, mType;
    Uri mProfilepic;
    ImageView mImageProfilePic;
    TextView mEmailText, mNameText, mCityText, mContactText, mTypeText;
    Button mUploadPic;
    double latitude, longitude;
    DatabaseReference mDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance();


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
        mUploadPic = (Button) view.findViewById(R.id.btn_upload_image);
        mEmailText = (TextView) view.findViewById(R.id.lawyertxt_email);
        mNameText = (TextView) view.findViewById(R.id.lawyertxt_name);
        mContactText = (TextView) view.findViewById(R.id.lawyertxt_contact);
        mCityText = (TextView) view.findViewById(R.id.lawyertxt_location);
        mTypeText = (TextView) view.findViewById(R.id.lawyertxt_type);
        TextView status=(TextView)view.findViewById(R.id.registeration);



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
                    Picasso.with(getContext()).load(mProfilepic).error(R.drawable.empty_profile).into(mImageProfilePic);
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

        mUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mProfilepic = data.getData();
            uploadImage();
            //  uploadImage();

           /* try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

    }

    public void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        StorageReference imageRef = storage.getReference();
        final String path = "profilepic/" + UUID.randomUUID().toString() + "/" + mProfilepic.getLastPathSegment();
        StorageReference img = imageRef.child(path);
        img.putFile(mProfilepic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                DatabaseReference mDB2 = mDatabase.child(mUserID);
                Map<String, Object> map = new HashMap<String, Object>();
                Log.d("profilepictureregular",downloadUrl + "");
                map.put("Profile_Pic", downloadUrl.toString());
                mDB2.updateChildren(map);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed to Upload Image", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //calculating progress percentage
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                //displaying percentage in progress dialog
                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
            }
        });
    }


}

package com.example.msp.legaldesire;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class Profile_Regular extends Fragment {
    private int PICK_IMAGE_REQUEST = 1;
    String mUserID;
    String mEmail, mName, mCity, mContact;
    Uri mProfilepic;
    CircleImageView mImageProfilePic;
    TextView mEmailText, mNameText, mCityText, mContactText;
    Button mUploadPic;
    DatabaseReference mDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance();


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
        mUploadPic = (Button) view.findViewById(R.id.btn_upload_image);
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
                if (mProfilepic.toString().equals("empty")) {
                    mImageProfilePic.setImageResource(R.drawable.empty_profile);
                } else {
                    Picasso.with(getContext()).load(mProfilepic).error(R.drawable.empty_profile).into(mImageProfilePic);
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



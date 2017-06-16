package com.example.msp.legaldesire;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterRegular extends AppCompatActivity {
    public final String TAG = "registerregular123";
    private int PICK_IMAGE_REQUEST = 1;

    private DatabaseReference mDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseUser user;

    Button mSubmit, mSelectImage;
    ImageView mImageView;
    EditText mEmailField, mPasswordField, mNameField, mContactField, mCityField, mContact1, mContact2, mContact3, mContact4, mContact5;
    TextView mTextView;
    private FirebaseAuth mAuth;
    String mEmail, mUserId, mName, mProfilePic;
    Uri mImagePath, mProfilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_regular);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");
        mAuth = FirebaseAuth.getInstance();

        mEmailField = (EditText) findViewById(R.id.edit_email_field);
        mPasswordField = (EditText) findViewById(R.id.edit_password_field);
        mNameField = (EditText) findViewById(R.id.editText_name);
        mContactField = (EditText) findViewById(R.id.editText_contact);
        mCityField = (EditText) findViewById(R.id.editText_city);
        mContact1 = (EditText) findViewById(R.id.editText_contact1);
        mContact2 = (EditText) findViewById(R.id.editText_contact2);
        mContact3 = (EditText) findViewById(R.id.editText_contact3);
        mContact4 = (EditText) findViewById(R.id.editText_contact4);
        mContact5 = (EditText) findViewById(R.id.editText_contact5);
        mSubmit = (Button) findViewById(R.id.btn_lawyer_submit_register);
        mTextView = (TextView) findViewById(R.id.txt_domain);
        mTextView.setText("@legaldesire.com");

       /* mSelectImage.setOnClickListener(new View.OnClickListener() {
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
        });*/

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmailField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterRegular.this, "Email field cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (mPasswordField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterRegular.this, "Email field cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (mContactField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterRegular.this, "Enter your Contact Number", Toast.LENGTH_SHORT).show();
                } else if (mCityField.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterRegular.this, "Enter City", Toast.LENGTH_SHORT).show();
                } else if (mContact1.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterRegular.this, "Enter your Contact 1", Toast.LENGTH_SHORT).show();
                } else if (mContact2.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterRegular.this, "Enter your Contact 2", Toast.LENGTH_SHORT).show();
                } else if (mContact3.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterRegular.this, "Enter your Contact 3", Toast.LENGTH_SHORT).show();
                } else if (mContact4.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterRegular.this, "Enter your Contact 4", Toast.LENGTH_SHORT).show();
                } else if (mContact5.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterRegular.this, "Enter your Contact 5", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterRegular.this, "All fields are filled", Toast.LENGTH_SHORT).show();
                    String email = mEmailField.getText().toString() + "@legaldesire.com";
                    String password = mPasswordField.getText().toString();
                    createAccount(email, password);
                }
            }
        });

    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "REgistration successful");
                    FirebaseUser user = mAuth.getCurrentUser();
                    HashMap<String, Object> insertData = new HashMap<String, Object>();
                    insertData.put("User ID", user.getUid());
                    insertData.put("Email", user.getEmail());
                    insertData.put("Name", mNameField.getText().toString().trim());
                    insertData.put("Profile_Pic", "empty");
                    insertData.put("Contact", mContactField.getText().toString().trim());
                    insertData.put("City", mCityField.getText().toString().trim());
                    insertData.put("Emergency Contact 1", mContact1.getText().toString().trim());
                    insertData.put("Emergency Contact 2", mContact2.getText().toString().trim());
                    insertData.put("Emergency Contact 3", mContact3.getText().toString().trim());
                    insertData.put("Emergency Contact 4", mContact4.getText().toString().trim());
                    insertData.put("Emergency Contact 5", mContact5.getText().toString().trim());

                    Log.d(TAG, "id:" + user.getUid() + " email:" + user.getEmail() + " name:" + mNameField.getText());
                    Log.d(TAG, "profile" + mProfilePath + " contact:" + mContactField.getText());
                    Log.d(TAG, " contact1:" + mContact1.getText() + " contact2:" + mContact2.getText() + " contact3:" + mContact3.getText() + " contact4:" + mContact4.getText() + " contact5:" + mContact5.getText());
                    mDatabase.child(user.getUid()).setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RegisterRegular.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterRegular.this, Login.class));

                        }
                    });

                } else {
                    Toast.makeText(RegisterRegular.this, "Registration FAILED:" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

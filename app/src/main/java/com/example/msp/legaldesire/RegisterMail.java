package com.example.msp.legaldesire;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class RegisterMail extends AppCompatActivity {
    EditText mEmail, mPassword;
    TextView mTextView;
    Button mRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mail);
        mEmail = (EditText) findViewById(R.id.edit_register_email);
        mPassword = (EditText) findViewById(R.id.edit_register_password);
        mTextView = (TextView) findViewById(R.id.text_email);
        mRegister = (Button) findViewById(R.id.btn_submit_register);
        mTextView.setText("@legaldesire.com");

        mAuth = FirebaseAuth.getInstance();

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString() + "@legaldesire.com";
                String password = mPassword.getText().toString();
                createAccount(email, password);

            }
        });
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterMail.this, "Registration successful, Login to Continue", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterMail.this, "Registration fail: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}

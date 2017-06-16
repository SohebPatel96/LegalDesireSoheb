package com.example.msp.legaldesire;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class MailAccount extends AppCompatActivity {
    RadioButton mUser, mLawyer;
    Button mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_account);

        mUser = (RadioButton) findViewById(R.id.radio_user2);
        mLawyer = (RadioButton) findViewById(R.id.radio_lawyer2);
        mNext = (Button) findViewById(R.id.btn_next2);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser.isChecked()) {
                    Intent intent = new Intent(MailAccount.this, RegisterRegular.class);
                    startActivity(intent);

                } else if (mLawyer.isChecked()) {
                    Intent intent = new Intent(MailAccount.this, RegisterLawyer.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MailAccount.this, "No type Selected", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

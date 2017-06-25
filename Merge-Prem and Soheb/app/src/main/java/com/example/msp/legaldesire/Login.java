package com.example.msp.legaldesire;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;


import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Login extends FragmentActivity {
    SignInButton mGoogleBtn;
    public static final String TAG = "registration123";
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    LoginButton facebook_login;
    CallbackManager callbackManager;
    String personName;
    String personEmail;
    String uid;
    Uri personPhoto;
    Integer google = 0;

    EditText mEmail, mPassword;
    Button mRegister, mLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     /*   PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.example.msp.legaldesire", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }*/
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        facebook_login = (LoginButton) findViewById(R.id.facebook_login);
        facebook_login.setReadPermissions("email", "public_profile");
        mGoogleBtn = (SignInButton) findViewById(R.id.googleBtn);
        mEmail = (EditText) findViewById(R.id.edit_email);
        mPassword = (EditText) findViewById(R.id.edit_password);
        mLogin = (Button) findViewById(R.id.btn_login);
        mRegister = (Button) findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("fblogin", "Login successful2");

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("fblogin", "Log out 2");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });


        facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("fblogin", "Login successful:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                // App code
                Log.d("fblogin", "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("fblogin", "facebook:onError", exception);
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSignIn();

            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, MailAccount.class);
                startActivity(intent);
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.d(TAG, "Activity change");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Intent i = new Intent(new Intent(Login.this, OnLoginSuccessful.class));
                    personName = user.getDisplayName(); //see here im getting username email and photo directly
                    personEmail = user.getEmail();//this works for google login so should also work for fb
                    personPhoto = user.getPhotoUrl();
                    if (personPhoto == null) {
                        personPhoto = Uri.parse("empty");
                    }
                    uid = user.getUid();
                    Log.d(TAG, "user change:" + personName + " " + personEmail);
                    i.putExtra("person_name", personName);
                    i.putExtra("person_email", personEmail);
                    i.putExtra("person_pic", personPhoto.toString());
                    i.putExtra("uid", uid);
                    Log.d(TAG, "Login success:" + personPhoto);
                    //       i.putExtra("person_photo", personPhoto);
                    i.putExtra("uid", uid);
                    startActivity(i);
                }
            }
        };
        //which is your facebook login button?


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    public void startSignIn() {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(Login.this, "Fields cannot be Empty", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, "Login Failed:" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Login.this, "Login Successful",Toast.LENGTH_SHORT);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Login Failed:" + task.getException(), Toast.LENGTH_SHORT);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        google = 1;
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (google == 0) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (google == 1) {
            super.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount acct = result.getSignInAccount();
                    personName = acct.getDisplayName();
                    personEmail = acct.getEmail();
                    personPhoto = acct.getPhotoUrl();
                    uid = acct.getId();
                    // personId = acct.getId();
                    Log.d(TAG, "Name:" + personName);
                    Log.d(TAG, "Person Email:" + personEmail);

                    firebaseAuthWithGoogle(acct);
                }
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //   updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //     updateUI(null);
                        }

                        // ...
                    }
                });
    }
}

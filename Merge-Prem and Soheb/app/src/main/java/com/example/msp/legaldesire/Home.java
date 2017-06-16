package com.example.msp.legaldesire;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity {

    Button signin,login;
    DatabaseReference mDatabase;
    GoogleApiClient googleApiClient;
    ViewPager viewPager;
    SignInButton mGoogleBtn;
    Button mMailLogin;
    public static final String TAG = "registration123";
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    LoginButton facebook_login;
    CallbackManager callbackManager;
    String personName;
    String personGivenName;
    String personFamilyName;
    String personEmail;
    String uid;
    Uri personPhoto;
    String personId;
    Integer google = 0;

    EditText mEmail, mPassword;
    Button mRegister, mLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        final DatabaseReference child=mDatabase.child("User");
        Timer timer=new Timer();
        mAuth = FirebaseAuth.getInstance();
        timer.scheduleAtFixedRate(new MyTimerClass(),2000,4000);
        Button signin=(Button)findViewById(R.id.button3);
        Button register=(Button)findViewById(R.id.button4);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Login.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Register.class));
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.d(TAG, "Activity change");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Intent i = new Intent(new Intent(Home.this, OnLoginSuccessful.class));
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
if(isFacebooklogged()){
    final AccessToken accessToken=AccessToken.getCurrentAccessToken();
    String uid=accessToken.getUserId();final String  photo_url=  "http://graph.facebook.com/"+ uid+ "/picture?type=large";
    // Toast.makeText(Login.this, loginResult.getAccessToken().getUserId(), Toast.LENGTH_SHORT).show();

    GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

        @Override
        public void onCompleted(JSONObject object, GraphResponse response) {
            Log.i("LoginActivity", response.toString());
            // Get facebook data from login
            String uid=accessToken.getUserId();
            Bundle bFacebookData = getFacebookData(object);
            Intent i = new Intent(new Intent(Home.this, OnLoginSuccessful.class));
            i.putExtra("person_name", bFacebookData.getString("first_name")+bFacebookData.getString("last_name"));
            i.putExtra("person_email", bFacebookData.getString("email"));
            i.putExtra("person_pic", photo_url);
            i.putExtra("uid", uid);
            startActivity(i);
            Toast.makeText(Home.this, bFacebookData.getString("email")+""+bFacebookData.getString("first_name"), Toast.LENGTH_SHORT).show();


        }
    });
    Bundle parameters = new Bundle();
    parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
    request.setParameters(parameters);
    request.executeAsync();



}
    }
    private boolean isFacebooklogged(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

    }
    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            Toast.makeText(Home.this,object.getString("email")+object.getString("last_name")+ "", Toast.LENGTH_SHORT).show();

            return bundle;
        }
        catch(JSONException e) {
            //Log.d(TAG,"Error parsing JSON")
            return null;
        }

    }
    private Boolean exit = false;
    @Override
    public void onBackPressed()
    {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    public class MyTimerClass extends TimerTask {

        @Override
        public void run() {
            Home.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(viewPager.getCurrentItem()==0){
                        viewPager.setCurrentItem(1);
                    }else if(viewPager.getCurrentItem()==1){
                        viewPager.setCurrentItem(2);
                    }else if(viewPager.getCurrentItem()==2){
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
}}

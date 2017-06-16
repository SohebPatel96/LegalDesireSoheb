package com.example.msp.legaldesire;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OnLoginSuccessful extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "registration123";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    boolean isLawyer = false;
    boolean isRegular = false;
    boolean isLawyerSearchFinished = false;
    boolean isRegularSearchFinished = false;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("User");


    String personName;
    String personEmail;
    String personPhoto;
    String mUserID;

    ImageView mPersonPhoto;
    TextView mPersonName;
    TextView mPersonEmail;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_login_successful);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(OnLoginSuccessful.this, Home.class));
                }
            }
        };
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mPersonPhoto = (ImageView) findViewById(R.id.imageView);
        mPersonName = (TextView) findViewById(R.id.text_name);
        mPersonEmail = (TextView) findViewById(R.id.text_email);

        extras = getIntent().getExtras();
        if (extras != null) {
            personName = extras.getString("person_name");
            personEmail = extras.getString("person_email");
            mUserID = extras.getString("uid");
            personPhoto = extras.getString("person_pic");
            Log.d(TAG, "OnLoginSuccessful:" + personEmail + " UID:" + mUserID + "photo:" + personPhoto);
        }


        Log.d(TAG, "AFTER REGISTRATION:" + personName + " " + personEmail + " " + mUserID);
        Bundle bundle = new Bundle();
        //  bundle.putString("personName", personName);
        //  bundle.putString("personEmail", personEmail);
        Chat_Module chat_module = new Chat_Module();
        chat_module.setArguments(bundle);

        Log.d(TAG, "name:" + personName + "," + personEmail);
        final ProgressDialog dialog = new ProgressDialog(OnLoginSuccessful.this);
        dialog.setMessage("Loading..");
        dialog.show();
        mDatabase.child("Lawyer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Inside Lawyer search");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String user_id = postSnapshot.child("User ID").getValue(String.class);
                    if (mUserID.equals(user_id)) {
                        //person has registered before as lawyer, head to lawyer profile
                        personName = postSnapshot.child("Name").getValue(String.class);
                        dialog.dismiss();
                        isLawyer = true;
                        isRegular = false;
                        isLawyerSearchFinished = true;
                        Log.d(TAG, "User logged in is lawyer");
                        Bundle bundle = new Bundle();
                        bundle.putString("User ID", mUserID);
                        Profile_Lawyer profile_lawyer = new Profile_Lawyer();
                        profile_lawyer.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, profile_lawyer).commitAllowingStateLoss();
                        break;
                        //Direct user to his/her profile

                    }
                }
                isLawyerSearchFinished = true;
                if (!isLawyer && isLawyerSearchFinished) {
                    mDatabase.child("Regular").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "Inside Regular search");
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String lemail = postSnapshot.child("Email").getValue(String.class);
                                String user_id = postSnapshot.child("User ID").getValue(String.class);
                                Log.d(TAG, "Regular:" + lemail);
                                if (mUserID.equals(user_id)) {
                                    dialog.dismiss();
                                    personName = postSnapshot.child("Name").getValue(String.class);
                                    //person has registered before as regular user, head to regular user profile
                                    isRegular = true;
                                    isLawyer = false;
                                    isRegularSearchFinished = true;
                                    Log.d(TAG, "User logged is regular");
                                    Bundle bundle = new Bundle();
                                    bundle.putString("User ID", mUserID);
                                    Profile_Regular profile_regular = new Profile_Regular();
                                    profile_regular.setArguments(bundle);
                                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.fragment_container, profile_regular).commitAllowingStateLoss();
                                    break;

                                    //Direct user to his/her profile
                                }
                            }
                            isRegularSearchFinished = true;
                            if (!isLawyer && !isRegular && isLawyerSearchFinished && isRegularSearchFinished) {
                                //User has logged in for the first time, direct user to registration screen
                                dialog.dismiss();
                                disableNavView(drawer);
                                Log.d(TAG, "Sending email and userid:" + personEmail + " " + mUserID);
                                Bundle bundle = new Bundle();
                                bundle.putString("Email", personEmail);
                                bundle.putString("User ID", mUserID);
                                bundle.putString("Name", personName);
                                bundle.putString("Photo", personPhoto);
                                SelectAccount selectAccount = new SelectAccount();
                                selectAccount.setArguments(bundle);
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_container, selectAccount).commit();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void disableNavView(DrawerLayout drawer) {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {


        return super.onCreateView(parent, name, context, attrs);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the FullScreenImage/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Bundle bundle = new Bundle();
        bundle.putString("Email", personEmail);
        bundle.putString("Name", personName);
        bundle.putString("User ID", mUserID);
        bundle.putBoolean("isLawyer",isLawyer);
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (id == R.id.search_lawyer) {
            SearchLawyer searchLawyer = new SearchLawyer();
            searchLawyer.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_container, searchLawyer).commit();
        }  else if (id == R.id.logout) {
            signOut();
        } else if (id == R.id.chat_module) {
            Chat_Module chat_module = new Chat_Module();
            chat_module.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_container, chat_module).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void signOut() {
        if(isloggedin())
        {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            LoginManager.getInstance().logOut();

        }
        // Firebase sign out
        else{
            mAuth.signOut();


        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //      Toast.makeText(OnLoginSuccessful.this, "Sign Out Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }}
    private boolean isloggedin(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

    }

}

package com.example.msp.legaldesire;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.magicretry.MainActivity;
import com.payu.payuui.Activity.PayUBaseActivity;
import com.payu.payuui.SdkuiUtil.SdkUIConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat_Lawyer_Profile extends Fragment {

    final String TAG = "profilelawyer123";
    String mEmail, mName, mCity, mContact, mUserID, mLawyerID, mUserName, mProfileUri, mType, lawyer_profile_pic, user_profile_pic;
    Uri mProfilepic;
    boolean isLawyer;
    ImageView mImageProfilePic;
    Button mAddChat, mVisitLocation, mAppointment;
    boolean mIfChatExist;
    TextView mEmailText, mNameText, mCityText, mContactText, mTypeText;
    double latitude, longitude;
    DatabaseReference mDatabase;
    private String merchantKey, userCredentials;

    // These will hold all the payment parameters
    private PaymentParams mPaymentParams;

    // This sets the configuration
    private PayuConfig payuConfig;

    // Used when generating hash from SDK
    private PayUChecksum checksum;

    public Chat_Lawyer_Profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Payu.setInstance(getContext());
        Bundle bundle = this.getArguments();
        mUserID = bundle.getString("User ID");
        mLawyerID = bundle.getString("Lawyer ID");
        isLawyer = bundle.getBoolean("isLawyer");
        Log.d("checkthis123", mUserID + "  :" + mLawyerID);
        ((OnLoginSuccessful) getActivity()).setActionBarTitle("Profile");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat__lawyer__profile, container, false);
        mImageProfilePic = (ImageView) view.findViewById(R.id.lawyerprofilepic);
        mEmailText = (TextView) view.findViewById(R.id.lawyertxt_email);
        mNameText = (TextView) view.findViewById(R.id.lawyertxt_name);
        mContactText = (TextView) view.findViewById(R.id.lawyertxt_contact);
        mTypeText = (TextView) view.findViewById(R.id.lawyertxt_type);
        mCityText = (TextView) view.findViewById(R.id.lawyertxt_city);
        mAddChat = (Button) view.findViewById(R.id.btn_addtochat);
        mAppointment = (Button) view.findViewById(R.id.btn_hire);
        mVisitLocation = (Button) view.findViewById(R.id.btn_visit_location);

        mAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLawyer) {
                    Toast.makeText(getContext(), "Lawyers cannot make appointments", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Appointment_BookLawyer appointment_bookLawyer = new Appointment_BookLawyer();
                    Bundle bundle = new Bundle();
                    bundle.putString("User ID", mUserID);
                    bundle.putString("Lawyer ID", mLawyerID);
                    appointment_bookLawyer.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, appointment_bookLawyer).commit();
                }
            }
        });

        mVisitLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLawyer) {
                    Toast.makeText(getContext(), "Lawyers cannot make appointments", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Appointment_BookLawyer2 appointment_bookLawyer2 = new Appointment_BookLawyer2();
                    Bundle bundle = new Bundle();
                    bundle.putString("User ID", mUserID);
                    bundle.putString("Lawyer ID", mLawyerID);
                    bundle.putDouble("Latitude",latitude);
                    bundle.putDouble("Longitude",longitude);
                    appointment_bookLawyer2.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_container, appointment_bookLawyer2).commit();
                }
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
        mDatabase.child(mLawyerID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEmail = (String) dataSnapshot.child("Email").getValue();
                Log.d("checkthis123", mEmail);
                mName = (String) dataSnapshot.child("Name").getValue();
                Log.d("checkthis123", mName);
                mContact = (String) dataSnapshot.child("Contact").getValue();
                mProfilepic = Uri.parse(dataSnapshot.child("Profile_Pic").getValue(String.class));
                mCity = (String) dataSnapshot.child("City").getValue();
                latitude = dataSnapshot.child("Latitude").getValue(double.class);
                longitude = dataSnapshot.child("Longitude").getValue(double.class);
                mType = (String) dataSnapshot.child("Type").getValue();
                if (mProfilepic.toString().equals("empty")) {
                    mImageProfilePic.setImageResource(R.drawable.empty_profile);
                } else {
                    Picasso.with(getContext()).load(mProfilepic).into(mImageProfilePic);
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

        mAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLawyer) {
                    Toast.makeText(getContext(), "Permission Denied:Lawyers cannot add to Chatlist", Toast.LENGTH_SHORT).show();
                } else {
                    final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat");
                    final DatabaseReference root2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
                    final DatabaseReference root3 = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");
                    root2.child(mLawyerID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            lawyer_profile_pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    root3.child(mUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user_profile_pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                            mUserName = dataSnapshot.child("Name").getValue(String.class);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    root.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String lawyer_id = postSnapshot.child("Lawyer ID").getValue(String.class);
                                String user_id = postSnapshot.child("User ID").getValue(String.class);
                                if (lawyer_id.equals(mLawyerID) && user_id.equals(mUserID)) {
                                    mIfChatExist = true;
                                    Toast.makeText(getContext(), "Lawyer already added to chat list", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (!mIfChatExist) {
                                navigateToBaseActivity();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        });

       /* mAddChat.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (isLawyer) {
                                                Toast.makeText(getContext(), "Permission Denied:Lawyers cannot add to Chatlist", Toast.LENGTH_SHORT).show();
                                            } else {
                                                final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat");
                                                final DatabaseReference root2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
                                                final DatabaseReference root3 = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");
                                                root2.child(mLawyerID).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        lawyer_profile_pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                root3.child(mUserID).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        user_profile_pic = dataSnapshot.child("Profile_Pic").getValue(String.class);
                                                        mUserName = dataSnapshot.child("Name").getValue(String.class);

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                root.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                            String lawyer_id = postSnapshot.child("Lawyer ID").getValue(String.class);
                                                            String user_id = postSnapshot.child("User ID").getValue(String.class);
                                                            if (lawyer_id.equals(mLawyerID) && user_id.equals(mUserID)) {
                                                                mIfChatExist = true;
                                                                Toast.makeText(getContext(), "Lawyer already added to chat list", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                        if (!mIfChatExist) {
                                                            Log.d(TAG, "Chat conversation doesn't exist");
                                                            HashMap<String, Object> Message = new HashMap<String, Object>();
                                                            HashMap<String, Object> map = new HashMap<String, Object>();
                                                            map.put("Lawyer ID", mLawyerID);
                                                            map.put("Lawyer Name", mName);
                                                            map.put("User ID", mUserID);
                                                            map.put("User Name", mUserName);
                                                            map.put("Message", Message);
                                                            map.put("Lawyer Seen", true);
                                                            map.put("User Seen", true);
                                                            map.put("Lawyer profile", lawyer_profile_pic);
                                                            map.put("User profile", user_profile_pic);


                                                            root.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getContext(), "Lawyer added to chat list", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Log.d(TAG, "Failed to add lawyer to chat list, an unknown error occured");
                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }

        );*/
        return view;
    }

    public void navigateToBaseActivity() {

        merchantKey = "gtKFFx";
        String amount = "50";
        //  String salt = "13p0PXZk";
        String salt = "eCwWELxi";
        String email = "msohebp@gmail.com";

        int environment = PayuConstants.STAGING_ENV;

        //    environment = PayuConstants.PRODUCTION_ENV;

        userCredentials = merchantKey + ":" + email;

        //TODO Below are mandatory params for hash genetation
        mPaymentParams = new PaymentParams();
        /**
         * For Test Environment, merchantKey = "gtKFFx"
         * For Production Environment, merchantKey should be your live key or for testing in live you can use "0MQaQP"
         */
        mPaymentParams.setKey(merchantKey);
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo("Chat Payment");
        mPaymentParams.setFirstName("firstname");
        mPaymentParams.setEmail(email);

        /*
        * Transaction Id should be kept unique for each transaction.
        * */
        mPaymentParams.setTxnId("" + System.currentTimeMillis());

        /**
         * Surl --> Success url is where the transaction response is posted by PayU on successful transaction
         * Furl --> Failre url is where the transaction response is posted by PayU on failed transaction
         */
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");

        /*
         * udf1 to udf5 are options params where you can pass additional information related to transaction.
         * If you don't want to use it, then send them as empty string like, udf1=""
         * */
        mPaymentParams.setUdf1("udf1");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");
        Log.d(TAG, "NavigateToBaseActivity: Params Set");

        /**
         * These are used for store card feature. If you are not using it then user_credentials = "default"
         * user_credentials takes of the form like user_credentials = "merchant_key : user_id"
         * here merchant_key = your merchant key,
         * user_id = unique id related to user like, email, phone number, etc.
         * */
        mPaymentParams.setUserCredentials(userCredentials);

        //TODO Pass this param only if using offer key
        //mPaymentParams.setOfferKey("cardnumber@8370");

        //TODO Sets the payment environment in PayuConfig object
        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(environment);

        Log.d(TAG, "NavigateToBaseActivity: Abt to generate Hash from server");

        //TODO It is recommended to generate hash from server only. Keep your key and salt in server side hash generation code.
        //    generateHashFromServer(mPaymentParams);

        /**
         * Below approach for generating hash is not recommended. However, this approach can be used to test in PRODUCTION_ENV
         * if your server side hash generation code is not completely setup. While going live this approach for hash generation
         * should not be used.
         * */
        generateHashFromSDK(mPaymentParams, salt);

    }

    /******************************
     * Client hash generation
     ***********************************/
    // Do not use this, you may use this only for testing.
    // lets generate hashes.
    // This should be done from server side..
    // Do not keep salt anywhere in app.
    public void generateHashFromSDK(PaymentParams mPaymentParams, String salt) {
        PayuHashes payuHashes = new PayuHashes();
        PostData postData = new PostData();

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }

        // checksum for payemnt related details
        // var1 should be either user credentials or default
        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();
        String key = mPaymentParams.getKey();

        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if ((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if (!var1.contentEquals(PayuConstants.DEFAULT)) {
            // get user card
            if ((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename storedc ard
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if ((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if ((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if ((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if (mPaymentParams.getOfferKey() != null) {
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if (mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        // we have generated all the hases now lest launch sdk's ui
        launchSdkUI(payuHashes);
    }

    // deprecated, should be used only for testing.
    private PostData calculateHash(String key, String command, String var1, String salt) {
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setKey(key);
        checksum.setCommand(command);
        checksum.setVar1(var1);
        checksum.setSalt(salt);
        return checksum.getHash();
    }

    public void generateHashFromServer(PaymentParams mPaymentParams) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.
        Log.d(TAG, "generateHashFromServer: Inside");
        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.PRODUCT_INFO, mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));
        Log.d(TAG, "generateHashFromServer: Params Set");
        // for offer_key
        if (null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        Log.d(TAG, "generateHashFromServer: generate hash from server task");
        getHashesFromServerTask.execute(postParams);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, PayuHashes> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "generateHashFromServerTask: On pre execute");
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected PayuHashes doInBackground(String... postParams) {
            PayuHashes payuHashes = new PayuHashes();
            try {

                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");

                // get the payuConfig first
                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        //TODO Below three hashes are mandatory for payment flow and needs to be generated at merchant server
                        /**
                         * Payment hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating payment_hash -
                         *
                         * sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||SALT)
                         *
                         */
                        case "payment_hash":
                            payuHashes.setPaymentHash(response.getString(key));
                            break;
                        /**
                         * vas_for_mobile_sdk_hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating vas_for_mobile_sdk_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be "default"
                         *
                         */
                        case "vas_for_mobile_sdk_hash":
                            payuHashes.setVasForMobileSdkHash(response.getString(key));
                            break;
                        /**
                         * payment_related_details_for_mobile_sdk_hash is one of the mandatory hashes that needs to be generated from merchant's server side
                         * Below is formula for generating payment_related_details_for_mobile_sdk_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "payment_related_details_for_mobile_sdk_hash":
                            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(response.getString(key));
                            break;

                        //TODO Below hashes only needs to be generated if you are using Store card feature
                        /**
                         * delete_user_card_hash is used while deleting a stored card.
                         * Below is formula for generating delete_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "delete_user_card_hash":
                            payuHashes.setDeleteCardHash(response.getString(key));
                            break;
                        /**
                         * get_user_cards_hash is used while fetching all the cards corresponding to a user.
                         * Below is formula for generating get_user_cards_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "get_user_cards_hash":
                            payuHashes.setStoredCardsHash(response.getString(key));
                            break;
                        /**
                         * edit_user_card_hash is used while editing details of existing stored card.
                         * Below is formula for generating edit_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "edit_user_card_hash":
                            payuHashes.setEditCardHash(response.getString(key));
                            break;
                        /**
                         * save_user_card_hash is used while saving card to the vault
                         * Below is formula for generating save_user_card_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be user credentials. If you are not using user_credentials then use "default"
                         *
                         */
                        case "save_user_card_hash":
                            payuHashes.setSaveCardHash(response.getString(key));
                            break;

                        //TODO This hash needs to be generated if you are using any offer key
                        /**
                         * check_offer_status_hash is used while using check_offer_status api
                         * Below is formula for generating check_offer_status_hash -
                         *
                         * sha512(key|command|var1|salt)
                         *
                         * here, var1 will be Offer Key.
                         *
                         */
                        case "check_offer_status_hash":
                            payuHashes.setCheckOfferStatusHash(response.getString(key));
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return payuHashes;
        }

        @Override
        protected void onPostExecute(PayuHashes payuHashes) {
            super.onPostExecute(payuHashes);
            Log.d(TAG, "generateHashFromServerTask: On post execute");
            progressDialog.dismiss();
            launchSdkUI(payuHashes);
        }
    }


    /**
     * This method adds the Payuhashes and other required params to intent and launches the PayuBaseActivity.java
     *
     * @param payuHashes it contains all the hashes generated from merchant server
     */
    public void launchSdkUI(PayuHashes payuHashes) {

        Intent intent = new Intent(getActivity(), PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);

        //Lets fetch all the one click card tokens first
        //fetchMerchantHashes(intent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {

                /**
                 * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                 * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                 *
                 * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                 * for identifying status of transaction. There are two possible status like, success or failure
                 * */
                new AlertDialog.Builder(getContext())
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();
                final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat");

                HashMap<String, Object> Message = new HashMap<String, Object>();
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("Lawyer ID", mLawyerID);
                map.put("Lawyer Name", mName);
                map.put("User ID", mUserID);
                map.put("User Name", mUserName);
                map.put("Message", Message);
                map.put("Lawyer Seen", true);
                map.put("User Seen", true);
                map.put("Lawyer profile", lawyer_profile_pic);
                map.put("User profile", user_profile_pic);

                root.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Lawyer added to chat list", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Failed to add lawyer to chat list, an error occured:" + task.getException());
                        }
                    }
                });


                String merchantData = data.getStringExtra("result"); // Data received from surl/furl
                String payuData = data.getStringExtra("payu_response"); // Response received from payu
                Log.d("After payement", "MerchantData:" + merchantData + "\n payuData" + payuData);
                String merchantData2 = data.getStringExtra("status"); // Data received from surl/furl
                Log.d("After payement2", "MerchantData:" + merchantData2 + "\n payuData" + payuData);


            } else {
                Toast.makeText(getContext(), getString(R.string.could_not_receive_data), Toast.LENGTH_LONG).show();
            }
        }
    }


}

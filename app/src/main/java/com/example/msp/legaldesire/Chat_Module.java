package com.example.msp.legaldesire;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat_Module extends Fragment {

    public static final String TAG = "chatmodul123";

    private Button add_room;
    private EditText room_name;
    private ListView listview;
    private boolean mIfChatExist = false;
    //  private ArrayAdapter<String> arrayAdapter;
    private Chat_List_Adapter arrayAdapter;
    private boolean isLawyer = false;
    ArrayList<String> storeKeys = new ArrayList<>();
    ArrayList<Uri> profile_pic = new ArrayList<>();
    ArrayList<String> mName = new ArrayList<>();
    ArrayList<Boolean> mNewMessage = new ArrayList<>();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Chat");
    final DatabaseReference root2 = FirebaseDatabase.getInstance().getReference().child("User").child("Lawyer");
    final DatabaseReference root3 = FirebaseDatabase.getInstance().getReference().child("User").child("Regular");

    String mUserID, mLawyerID;
    Bundle bundle;

    public Chat_Module() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = this.getArguments();
        mUserID = this.getArguments().getString("User ID");

      /*  if (bundle != null) {
            mIfChatExist = bundle.getBoolean("chat_exist");
            if (mIfChatExist) {
                Log.d(TAG, "CHAT EXISTS");
            } else {
                Log.d(TAG, "CHAT DOESN'T EXIST");
            }
            mLawyerEmail = bundle.getString("lawyer_email");
            mUserEmail = bundle.getString("user_email");
            Log.d(TAG, "lawyer:" + mLawyerEmail + "," + "User:" + mUserEmail);
            root.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String lawyer_email = postSnapshot.child("Lawyer Email").getValue(String.class);
                        String user_email = postSnapshot.child("User Email").getValue(String.class);
                        Log.d(TAG, "Lawyer name:" + lawyer_email);
                        Log.d(TAG, "User name:" + user_email);
                        Log.d(TAG, email);
                        if (mLawyerEmail.equals(lawyer_email) && mUserEmail.equals(user_email)) {
                            Log.d(TAG, "Checking for emails");
                            String key = postSnapshot.getKey();
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("key", key);
                            Log.d(TAG, "key:" + key);
                            // chat_module.setArguments(bundle);
                            Chat_Room chat_room = new Chat_Room();
                            chat_room.setArguments(bundle2);
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, chat_room).commit();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat__module, container, false);


        listview = (ListView) view.findViewById(R.id.chats);

        //  arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_rooms);
        arrayAdapter = new Chat_List_Adapter(getContext(), profile_pic, mName, mNewMessage);
        listview.setAdapter(arrayAdapter);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String lawyer_id = postSnapshot.child("Lawyer ID").getValue(String.class);
                    final String user_id = postSnapshot.child("User ID").getValue(String.class);
                    if (mUserID.equals(lawyer_id) || mUserID.equals(user_id)) {
                        storeKeys.add(postSnapshot.getKey());
                        if (mUserID.equals(lawyer_id)) {
                            isLawyer = true;
                            mName.add(postSnapshot.child("User Name").getValue(String.class));
                            mNewMessage.add(postSnapshot.child("Lawyer Seen").getValue(Boolean.class));
                            profile_pic.add(Uri.parse(postSnapshot.child("User profile").getValue(String.class)));
                            //    profile_pic.add(Uri.parse(postSnapshot.child("User profile").getValue(String.class)));
                          /*  root3.child(user_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String str = dataSnapshot.child("Profile_Pic").getValue(String.class);
                                    Log.d(TAG, "inside regular:" + user_id);
                                    Log.d(TAG, str);
                                    profile_pic.add(Uri.parse(str));
                                    arrayAdapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            */
                        } else {
                            isLawyer = false;
                            mName.add(postSnapshot.child("Lawyer Name").getValue(String.class));
                            mNewMessage.add(postSnapshot.child("User Seen").getValue(Boolean.class));
                            profile_pic.add(Uri.parse(postSnapshot.child("Lawyer profile").getValue(String.class)));
                            //    profile_pic.add(Uri.parse(postSnapshot.child("Lawyer profile").getValue(String.class)));
                            Log.d(TAG, "lawyerprofiel:" + profile_pic);
/*
                            root2.child(lawyer_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d(TAG, "inside lawyer:" + lawyer_id);
                                    String str = dataSnapshot.child("Profile_Pic").getValue(String.class);
                                    Log.d(TAG, str);
                                    profile_pic.add(Uri.parse(str));
                                    arrayAdapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
*/
                        }

                        arrayAdapter.notifyDataSetChanged();

                    }
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
//                bundle.putString("room_name", ((TextView) view).getText().toString());
                if (isLawyer) {
                    root.child(storeKeys.get(i)).child("Lawyer Seen").setValue(true);
                } else {
                    root.child(storeKeys.get(i)).child("User Seen").setValue(true);
                }
                bundle.putString("User ID", mUserID);
                bundle.putString("key", storeKeys.get(i));
                bundle.putBoolean("isLawyer", isLawyer);
                Log.d(TAG, "Clicked:" + storeKeys.get(i));
                Chat_Room chat_room = new Chat_Room();
                chat_room.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, chat_room).commit();
            }
        });
        return view;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}

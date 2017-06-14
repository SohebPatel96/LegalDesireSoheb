package com.example.msp.legaldesire;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.msp.legaldesire.Chat_Room_Adapter;
import com.example.msp.legaldesire.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MSP on 5/17/2017.
 */

public class Chat_List_Adapter extends ArrayAdapter<String> {
    public static String TAG = "chatlistadapter";
    Context context;
    LayoutInflater inflater;

    ArrayList<Uri> uri;
    ArrayList<String> chat_list;
    ArrayList<Boolean> newMsg;

    public Chat_List_Adapter(Context context, ArrayList<Uri> uri, ArrayList<String> chat_list, ArrayList<Boolean> newMsg) {
        super(context, R.layout.chat_list_adapter, chat_list);
        this.context = context;
        this.uri = uri;
        this.chat_list = chat_list;
        this.newMsg = newMsg;

    }

    public class ViewHolder {
        CircleImageView mCircleImageView;
        TextView mChat;
        TextView mNewMsg;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_list_adapter, null);
        }

        final ViewHolder holder = new ViewHolder();
        holder.mCircleImageView = (CircleImageView) convertView.findViewById(R.id.profile_image);
        holder.mChat = (TextView) convertView.findViewById(R.id.chat_name);
        holder.mNewMsg = (TextView) convertView.findViewById(R.id.new_msg);
        holder.mChat.setText(chat_list.get(position));
        Picasso.with(getContext()).load(uri.get(position)).error(R.drawable.empty_profile).into(holder.mCircleImageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
            //    holder.mCircleImageView.setImageDrawable(R.drawable.empty_profile);
            }
        });

        if (newMsg.get(position)) {
            Log.d(TAG, "view is gone");
            holder.mNewMsg.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "view is not gone");
        }

        return convertView;
    }
}

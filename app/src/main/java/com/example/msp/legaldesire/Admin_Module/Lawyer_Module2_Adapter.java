package com.example.msp.legaldesire.Admin_Module;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.msp.legaldesire.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Created by MSP on 5/8/2017.
 */

public class Lawyer_Module2_Adapter extends ArrayAdapter<String> {
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> email = new ArrayList<>();
    ArrayList<String> address = new ArrayList<>();
    ArrayList<Double> rating = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();
    ArrayList<Double> latitude = new ArrayList<>();
    ArrayList<Double> longitude = new ArrayList<>();
    Context context;
    LayoutInflater inflater;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Lawyers");
    ;


    public Lawyer_Module2_Adapter(Context context, ArrayList<String> name) {
        super(context, R.layout.lawyer_module2_adapter, name);
        this.context = context;
        this.name = name;

    }

    public class ViewHolder {
        RelativeLayout relativeLayout;
        ImageView img;
        Button btn;

    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lawyer_module2_adapter, null);
        }

        final ViewHolder holder = new ViewHolder();
        holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativelayout4);

        //  holder.img = (ImageView) convertView.findViewById(R.id.img_display2);
        holder.btn = (Button) convertView.findViewById(R.id.btn_download_image2);
      //  holder.img.setVisibility(View.GONE);
        holder.relativeLayout.setBackground(getContext().getDrawable(R.drawable.bubble_right_green));



        return convertView;
    }
}


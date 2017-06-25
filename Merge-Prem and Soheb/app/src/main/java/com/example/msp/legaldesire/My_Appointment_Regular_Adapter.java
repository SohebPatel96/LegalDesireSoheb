package com.example.msp.legaldesire;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MSP on 6/19/2017.
 */

public class My_Appointment_Regular_Adapter extends ArrayAdapter<String> {
    Context context;
    public final String OFFICE_APPOINTMENT = "Office Appointment";
    public final String NON_OFFICE_APPOINTMENT = "Non Office Appointment";
    LayoutInflater inflater;
    ArrayList<String> mAppointmentDate, mAppointmentTime, mAppointmentType, mLawyerName, mLocationAddress;
    boolean isLawyer;

    public My_Appointment_Regular_Adapter(Context context, ArrayList<String> mAppointmentDate, ArrayList<String> mAppointmentTime, ArrayList<String> mAppointmentType, ArrayList<String> mLawyerName, ArrayList<String> mLocationAddress,boolean isLawyer) {
        super(context, R.layout.my_appointment_adapter, mAppointmentDate);
        this.context = context;
        this.mAppointmentDate = mAppointmentDate;
        this.mAppointmentTime = mAppointmentTime;
        this.mAppointmentType = mAppointmentType;
        this.mLawyerName = mLawyerName;
        this.mLocationAddress = mLocationAddress;
        this.isLawyer = isLawyer;
    }

    public class ViewHolder {
        TextView mTxt;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.my_appointment_adapter, null);
        }

        final ViewHolder holder = new ViewHolder();
        holder.mTxt = (TextView) convertView.findViewById(R.id.txt_appointments);
        if(isLawyer){
            if (mAppointmentType.get(position).equals(OFFICE_APPOINTMENT))
                holder.mTxt.setText("You have an Office Appointment:\nClient Name:" + mLawyerName.get(position) + "\nLocation:" + mLocationAddress.get(position) + "\nBooking Date:" + mAppointmentDate.get(position) + "\nTime:" + mAppointmentTime.get(position));
            else if (mAppointmentType.get(position).equals(NON_OFFICE_APPOINTMENT))
                holder.mTxt.setText("You have Lawyer Appointment at your Location:\nLawyer Name:" + mLawyerName.get(position) + "\nLocation:" + mLocationAddress.get(position) + "\nBooking Date:" + mAppointmentDate.get(position) + "\nTime:" + mAppointmentTime.get(position));

        }
        else{
            if (mAppointmentType.get(position).equals(OFFICE_APPOINTMENT))
                holder.mTxt.setText("You have an Office Appointment:\nLawyer Name:" + mLawyerName.get(position) + "\nLocation:" + mLocationAddress.get(position) + "\nBooking Date:" + mAppointmentDate.get(position) + "\nTime:" + mAppointmentTime.get(position));
            else if (mAppointmentType.get(position).equals(NON_OFFICE_APPOINTMENT))
                holder.mTxt.setText("You have Lawyer Appointment at your Location:\nLawyer Name:" + mLawyerName.get(position) + "\nLocation:" + mLocationAddress.get(position) + "\nBooking Date:" + mAppointmentDate.get(position) + "\nTime:" + mAppointmentTime.get(position));

        }

        return convertView;
    }
}

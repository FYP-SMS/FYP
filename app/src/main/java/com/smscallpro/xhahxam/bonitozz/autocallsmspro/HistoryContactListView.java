package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HistoryContactListView   extends RecyclerView.Adapter<HistoryContactListView.MyViewHoldero>{


    private Context context;
    private ProfileHistory profileHistory;

     HistoryContactListView(Context context, ProfileHistory profileHistory) {

        this.context = context;
        this.profileHistory = profileHistory;

    }



    @NonNull
    @Override
    public MyViewHoldero onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HistoryContactListView.MyViewHoldero(LayoutInflater.from(context).inflate(R.layout.history_contact_report,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoldero myViewHoldero, int i) {



        myViewHoldero.contactName.setText(profileHistory.getNames()[i]);















    }

    @Override
    public int getItemCount() {
        return profileHistory.getNames().length;
    }

    class MyViewHoldero extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView contactName;
        ImageView sendNowImage;

         MyViewHoldero(@NonNull View itemView) {
            super(itemView);

           contactName= itemView.findViewById(R.id.phoneNumber);
           sendNowImage=itemView.findViewById(R.id.imagesendnow);

           sendNowImage.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            SmsManager sms = SmsManager.getDefault();

            if(profileHistory.getMessage().length()>160){



                ArrayList<String> messageParts = sms.divideMessage(profileHistory.getMessage());
















                sms.sendMultipartTextMessage(profileHistory.getPhoneNumber()[getAdapterPosition()], null, messageParts, null, null);

            }
            else {



                sms.sendTextMessage(profileHistory.getPhoneNumber()[getAdapterPosition()], null, profileHistory.getMessage(), null, null);


            }

            Toast.makeText(context, R.string.sending,Toast.LENGTH_SHORT).show();








        }
    }






}

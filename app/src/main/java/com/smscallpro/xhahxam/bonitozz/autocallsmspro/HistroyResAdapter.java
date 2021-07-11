package com.smscallpro.xhahxam.bonitozz.autocallsmspro;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HistroyResAdapter  extends RecyclerView.Adapter<HistroyResAdapter.MainViewHolder> {


    private Context context;

 private    ArrayList<ProfileHistory> profileHistories;


     HistroyResAdapter(Context context, ArrayList<ProfileHistory> profileHistories) {
        this.context = context;

        this.profileHistories = profileHistories;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HistroyResAdapter.MainViewHolder(LayoutInflater.from(context).inflate(R.layout.outsms_rescycler,viewGroup,false));
}

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder mainViewHolder, int i) {

        try {

            mainViewHolder.profileName.setText(profileHistories.get(i).getProfileName().replace(ProcessData.HISTORY_LOGO,"").toUpperCase());

            String contatNames = "> ";
            String[] profilesCollexction = profileHistories.get(i).getNames();
            for (int ii = 0; ii < profilesCollexction.length; ii++) {

                contatNames = contatNames + profilesCollexction[ii] + ",";
                if (ii == 6) {
                    break;
                }
            }


           boolean checksendFail= ProcessData.checkReportHistory(profileHistories.get(i).getProfileName(),context);



            if(checksendFail)
                mainViewHolder.editButton.setImageResource(R.drawable.ic_close);
                else
                    mainViewHolder.editButton.setImageResource(R.drawable.ic_done);















            mainViewHolder.contactsNames.setText(contatNames);



                Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(profileHistories.get(i).getSavingDate());


            String time=ProcessData.convertTimeFormate(calendar,context);

            SimpleDateFormat    form = new SimpleDateFormat("EEE MMM dd yyyy", Locale.getDefault());
            String error=context.getString(R.string.errorTapForDetails);

            if(checksendFail)
            mainViewHolder.dates.setText(String.format("%s%s%s %s", context.getString(R.string.sentAt), time, form.format(calendar.getTime()), error));
            else
            mainViewHolder.dates.setText(String.format("%s%s%s", context.getString(R.string.sentAt), time, form.format(calendar.getTime())));


            mainViewHolder.messagex.setText(String.format("%s%s", context.getString(R.string.message), profileHistories.get(i).getMessage()));



        }catch (Exception e){

               Log.e("taliException",e.getLocalizedMessage());

        }

    }

    @Override
    public int getItemCount() {
        return profileHistories.size();
    }

    class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView profileName;
        TextView contactsNames;
        TextView dates;
        TextView messagex;
        ImageView repetition;
        ImageView editButton;
         MainViewHolder(@NonNull View itemView) {
            super(itemView);

            profileName=itemView.findViewById(R.id.textNameOfProfile);
            contactsNames=itemView.findViewById(R.id.textContactNumberz);
            dates=itemView.findViewById(R.id.textScheduleDate);
            messagex=itemView.findViewById(R.id.textMessageContacts);
            repetition=itemView.findViewById(R.id.imageRepetionSign);
            editButton=itemView.findViewById(R.id.imageViewEdit);
            repetition.setOnClickListener(this);
            profileName.setOnClickListener(this);
            contactsNames.setOnClickListener(this);
            editButton.setOnClickListener(this);
            dates.setOnClickListener(this);
            messagex.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch(view.getId()){
                case R.id.imageRepetionSign:
                    ProcessData.deleteProfile(profileHistories.get(getAdapterPosition()).getProfileName(),context,ProcessData.HISTORY_FILE_NAMES);
                    profileHistories.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    break;

                    default:


                        Log.e("talihistory",profileHistories.get(getAdapterPosition()).getProfileName());
                        Intent intent=new Intent(context,HistoryContactsReports.class);
                        intent.putExtra("profile",profileHistories.get(getAdapterPosition()).getProfileName());
                        context.startActivity(intent);







            }



        }
    }



    public void historyRefresh(){

        profileHistories.clear();
        notifyDataSetChanged();





    }
}

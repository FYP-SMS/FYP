package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainOutGoingRecyclerView extends RecyclerView.Adapter<MainOutGoingRecyclerView.MainViewHolder> {

    Context context;
    private ArrayList<ProfileHistory> profileHistories;

    MainOutGoingRecyclerView(Context context, ArrayList<ProfileHistory> profileHistories) {
        this.context = context;
        this.profileHistories = new ArrayList<>(profileHistories);
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MainViewHolder(LayoutInflater.from(context).inflate(R.layout.outsms_rescycler,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder mainViewHolder, int i) {

        try {
            mainViewHolder.profileName.setText(profileHistories.get(i).getProfileName().toUpperCase());

            StringBuilder contatNames = new StringBuilder("> ");
            String[] profilesCollexction = profileHistories.get(i).getNames();
            for (int ii = 0; ii < profilesCollexction.length; ii++) {

                contatNames.append(profilesCollexction[ii]).append(",");
                if (ii == 6) {
                    break;
                }
            }
            mainViewHolder.contactsNames.setText(contatNames.toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(profileHistories.get(i).getDate());

            String time=ProcessData.convertTimeFormate(calendar,context);


            String repeated=ProcessData.getRepeated(profileHistories.get(i).getProfileName(),context);
            if(repeated==null)
                repeated=" ";
            SimpleDateFormat form;
            if(repeated.contains("no")) {
                form = new SimpleDateFormat("EEE MMM dd yyyy");
                mainViewHolder.dates.setText(String.format("%s%s", time, form.format(calendar.getTime())));



            }else if(repeated.contains("daily")){


                mainViewHolder.dates.setText(String.format("%s :%s", context.getString(R.string.daliy), time));



            }else if(repeated.contains("weekly")){

                String re="";
                if(repeated.contains("1"))
                    re=re+context.getResources().getStringArray(R.array.weekdays)[0]+",";
                    if(repeated.contains("2"))
                        re=re+context.getResources().getStringArray(R.array.weekdays)[1]+",";
                        if(repeated.contains("3"))
                            re=re+context.getResources().getStringArray(R.array.weekdays)[2]+",";
                            if(repeated.contains("4"))
                                re=re+context.getResources().getStringArray(R.array.weekdays)[3]+",";
                                if(repeated.contains("5"))
                                    re=re+context.getResources().getStringArray(R.array.weekdays)[4]+",";
                                    if(repeated.contains("6"))
                                        re=re+context.getResources().getStringArray(R.array.weekdays)[5]+",";
                                        if(repeated.contains("7"))
                                            re=re+context.getResources().getStringArray(R.array.weekdays)[6]+",";



                mainViewHolder.dates.setText(String.format("%s: %s %s", context.getString(R.string.every), re, time));




            }
            else if(repeated.contains("custom")){

               String []po= repeated.split("=");

                form = new SimpleDateFormat("MMM dd yyyy");
                mainViewHolder.dates.setText(String.format("%s%s %s %s%s %s%s %s%s", time, form.format(calendar.getTime()), context.getString(R.string.repeat), po[4], context.getString(R.string.days), po[3], context.getString(R.string.hours), po[2], context.getString(R.string.minutes)));






            }


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
            editButton.setOnClickListener(this);
            contactsNames.setOnClickListener(this);
            messagex.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();

            switch (view.getId()) {
                case R.id.imageRepetionSign:

                    alertDialog.setTitle(context.getString(R.string.alert));
                    alertDialog.setMessage(context.getString(R.string.sureYouWantTODelete));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                  ProcessData.cancelAlarm(context,profileHistories.get(getAdapterPosition()).getProfileName(),profileHistories.get(getAdapterPosition()).getRequestCode());

                                    ProcessData.deleteProfile(profileHistories.get(getAdapterPosition()).getProfileName(), context, ProcessData.OUTGOING_FILE_NAMES);
                                    profileHistories.remove(getAdapterPosition());
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.show();
                    break;


                case R.id.imageViewEdit:



                    alertDialog.setTitle(context.getString(R.string.alert));
                    alertDialog.setMessage(context.getString(R.string.sureYouWantToEdit));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent i=new Intent(context,AddOutGoingSms.class);
                                    i.putExtra("edit","yes");

                                    i.putExtra("profile",profileHistories.get(getAdapterPosition()).getProfileName());



                                    context.startActivity(i);
                                    OutGoingSMS.refrenece=getAdapterPosition();
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.show();







                    break;

                case R.id.textMessageContacts:

                    alertDialog.setTitle(context.getString(R.string.yourMessage));
                    alertDialog.setMessage(profileHistories.get(getAdapterPosition()).getMessage());
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.cancelOption),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {



                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();


                    break;
                case R.id.textContactNumberz:

                    ArrayAdapter<String> stringArrayAdapter=new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,profileHistories.get(getAdapterPosition()).getNames());
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.selectedContacts));

                    builder.setAdapter(stringArrayAdapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {




                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.cancelOption),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {



                                    dialog.dismiss();
                                }
                            });
                    alert.show();


                    break;

            }



        }
    }

    public void outGoingResFilter(ProfileHistory profileHistories1){

       profileHistories.add(0,profileHistories1);
        notifyDataSetChanged();

    }

    public void outGoingResFilter(ProfileHistory profileHistories1,int index){

        if(index>=0) {
            profileHistories.add(index,profileHistories1);
            profileHistories.remove(index+1);

            notifyDataSetChanged();
        }
        OutGoingSMS.refrenece=-1;
        OutGoingSMS.profileNames="null";
    }



}

package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ModeRescyclerVIew  extends RecyclerView.Adapter<ModeRescyclerVIew.ModeHolder> {

     ModeRescyclerVIew(Context context, ArrayList<ProfileHistory> profileHistories) {
        this.context = context;
        this.profileHistories = profileHistories;
    }

    private Context context;
   private AudioManager mobilemode=null;
    private ArrayList<ProfileHistory> profileHistories;

    @NonNull
    @Override
    public ModeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ModeRescyclerVIew.ModeHolder(LayoutInflater.from(context).inflate(R.layout.mode_rescycler_dummy,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ModeHolder modeHolder, int i) {


        try {

            String name = profileHistories.get(i).getProfileName();
            modeHolder.nameMOde.setText(name.replace(ProcessData.MODE_NAME_LOGO, "").toUpperCase());


            if(profileHistories.get(i).getMessage().length()>60)
                modeHolder.messageMode.setText(String.format("%s...", profileHistories.get(i).getMessage().substring(0, 60)));
            else
                modeHolder.messageMode.setText(profileHistories.get(i).getMessage());




            if (profileHistories.get(i).getDate() == -1) {

                modeHolder.aSwitch.setVisibility(View.VISIBLE);
                modeHolder.dateTimeStatus.setText(R.string.Manual);
                modeHolder.aSwitch.setChecked(ProcessData.loadStatusReport(name,context));
                if(modeHolder.aSwitch.isChecked()){

                    modeHolder.activatesMode.setImageResource(R.drawable.ic_done);
                   modeHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.LightBlue));

                }else {

                    modeHolder.activatesMode.setImageResource(R.drawable.ic_close);
                    modeHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.White));


                }


            } else {


                modeHolder.aSwitch.setVisibility(View.GONE);


                String repeats = ProcessData.getRepeated(name, context);

                Calendar cFrom = Calendar.getInstance();
                Calendar cTo = Calendar.getInstance();
                cFrom.setTimeInMillis(profileHistories.get(i).getDate());
                cTo.setTimeInMillis(profileHistories.get(i).getSavingDate());

                if (repeats.contains("no")) {







                        SimpleDateFormat form = new SimpleDateFormat("MMM dd yyyy");
                        modeHolder.dateTimeStatus.setText(String.format("%s%s "+ context.getString(R.string.To) +" %s%s", ProcessData.convertTimeFormate(cFrom, context), form.format(cFrom.getTime()), ProcessData.convertTimeFormate(cTo, context), form.format(cTo.getTime()))

                        );

                        if (Calendar.getInstance().after(cFrom) && Calendar.getInstance().before(cTo)) {

                            modeHolder.activatesMode.setImageResource(R.drawable.ic_done);
                            modeHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.LightBlue));

                        } else {


                            modeHolder.activatesMode.setImageResource(R.drawable.ic_close);
                            modeHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.White));
                        }


                } else if (repeats.contains("weekly")) {
                    String re=context.getString(R.string.every)+" ";
                    if(repeats.contains("1"))
                        re=re+context.getResources().getStringArray(R.array.weekdays)[0]+",";
                    if(repeats.contains("2"))
                        re=re+context.getResources().getStringArray(R.array.weekdays)[1]+",";
                    if(repeats.contains("3"))
                        re=re+context.getResources().getStringArray(R.array.weekdays)[2]+",";
                    if(repeats.contains("4"))
                        re=re+context.getResources().getStringArray(R.array.weekdays)[3]+",";
                    if(repeats.contains("5"))
                        re=re+context.getResources().getStringArray(R.array.weekdays)[4]+",";
                    if(repeats.contains("6"))
                        re=re+context.getResources().getStringArray(R.array.weekdays)[5]+",";
                    if(repeats.contains("7"))
                        re=re+context.getResources().getStringArray(R.array.weekdays)[6]+",";


                    if (repeats.contains(Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)))) {

                   Calendar cfom1=Calendar.getInstance();
                    Calendar cto2=Calendar.getInstance();
                     cfom1.set(Calendar.MINUTE,cFrom.get(Calendar.MINUTE));
                        cfom1.set(Calendar.HOUR_OF_DAY,cFrom.get(Calendar.HOUR_OF_DAY));
                        cto2.set(Calendar.MINUTE,cTo.get(Calendar.MINUTE));
                        cto2.set(Calendar.HOUR_OF_DAY,cTo.get(Calendar.HOUR_OF_DAY));
                        Log.e("tali111","sdsdsd");

                        if (Calendar.getInstance().after(cfom1) && Calendar.getInstance().before(cto2)) {

                            Log.e("tali111","sdghghghghghghhgsdsd");
                            modeHolder.activatesMode.setImageResource(R.drawable.ic_done);
                           modeHolder. cardView.setCardBackgroundColor(context.getResources().getColor(R.color.LightBlue));

                        } else {


                            modeHolder.activatesMode.setImageResource(R.drawable.ic_close);
                            modeHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.White));
                        }

                    } else {

                        modeHolder.activatesMode.setImageResource(R.drawable.ic_close);
                       modeHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.White));
                    }

                    modeHolder.dateTimeStatus.setText(String.format("%s %s "+context.getString(R.string.To)+" %s",re, ProcessData.convertTimeFormate(cFrom, context), ProcessData.convertTimeFormate(cTo, context)));

                }

            }

        }catch (Exception e){

            Log.e("taliException",e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return profileHistories.size();
    }




    class ModeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{



        TextView messageMode,nameMOde,dateTimeStatus;
        ImageView editMode,deleteMOde,activatesMode;
        Switch aSwitch;
        CardView cardView;



       ModeHolder(@NonNull final View itemView) {
            super(itemView);
            messageMode=itemView.findViewById(R.id.textMeesageModeRes);
            nameMOde=itemView.findViewById(R.id.textModeNameRes);
            editMode=itemView.findViewById(R.id.imageEditModeRes);
            deleteMOde=itemView.findViewById(R.id.imageDeleteModeRes);
            activatesMode=itemView.findViewById(R.id.imageActivatesRes);
            dateTimeStatus=itemView.findViewById(R.id.textModeDateStatus);
            aSwitch=itemView.findViewById(R.id.switchRes);
            cardView=itemView.findViewById(R.id.cardViewx);
            deleteMOde.setOnClickListener(this);
            editMode.setOnClickListener(this);

           if(ProcessData.loadSettingsData("silentdatamode",context)==0)
               mobilemode = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    Log.e("tali121",b+"");
                    if(b){

                        ProcessData.deleteContactRepeating(profileHistories.get(getAdapterPosition()).getProfileName(),context);
                        activatesMode.setImageResource(R.drawable.ic_done);
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.LightBlue));


                        if(mobilemode!=null)
                            mobilemode.setStreamVolume(AudioManager.STREAM_RING,0,0);

                    }
                    else {

                        activatesMode.setImageResource(R.drawable.ic_close);
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.White));
                        if(mobilemode!=null)
                        mobilemode.setStreamVolume(AudioManager.STREAM_RING,mobilemode.getStreamMaxVolume(AudioManager.STREAM_RING),0);
                    }
                    ProcessData.saveStatusReport(profileHistories.get(getAdapterPosition()).getProfileName(),context,b);
                }
            });


        }

        @Override
        public void onClick(View view) {

           final int adapterPostion=getAdapterPosition();
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
           switch (view.getId()){

               case R.id.imageDeleteModeRes:

                   alertDialog.setTitle(context.getString(R.string.alert));
                   alertDialog.setMessage(context.getString(R.string.sureYouWantTODelete));
                   alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes),
                           new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {

                                 int requestCode = profileHistories.get(adapterPostion).getRequestCode();


                                   if(requestCode!=-22){



                                       ProcessData.cancelSpeakingAlarm(context,requestCode);
                                   }
                                   ProcessData.deleteProfile(profileHistories.get(adapterPostion).getProfileName(), context, ProcessData.MODE_FILE_NAMES);
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

                   case R.id.imageEditModeRes:

                       alertDialog.setTitle(context.getString(R.string.alert));
                       alertDialog.setMessage(context.getString(R.string.sureYouWantToEdit));
                       alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes),
                               new DialogInterface.OnClickListener() {
                                   public void onClick(DialogInterface dialog, int which) {

                                       Intent i=new Intent(context,Mode_Entry.class);
                                       i.putExtra("edit","yes");

                                       i.putExtra("profile",profileHistories.get(getAdapterPosition()).getProfileName());



                                       context.startActivity(i);
                                      MoodSMS.modeProfileHistoryIndex=getAdapterPosition();
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




           }



        }
    }


    public void filterAddProfile(ProfileHistory profileHistory,int index){


        if(profileHistory!=null) {
            if(index==-1)
            profileHistories.add(0, profileHistory);

            else {

                profileHistories.add(index,profileHistory);
                profileHistories.remove(index+1);
            }
            notifyDataSetChanged();
        }
    }
}

package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class IncomingRecyclerView extends RecyclerView.Adapter<IncomingRecyclerView.InComingHolder>{

    private Context context;
    private ArrayList<ProfileHistory> profileHistories;


     IncomingRecyclerView(Context context, ArrayList<ProfileHistory> profileHistories) {
        this.context = context;
        this.profileHistories = profileHistories;
    }

    @NonNull

    @Override
    public InComingHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new IncomingRecyclerView.InComingHolder(LayoutInflater.from(context).inflate(R.layout.mode_rescycler_dummy,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InComingHolder inComingHolder, int i) {

        try {
            String name = profileHistories.get(i).getProfileName();
            inComingHolder.nameMOde.setText(name.replace(ProcessData.INCOMING_LOGO, "").toUpperCase());
            if(profileHistories.get(i).getMessage().length()>60)
                inComingHolder.messageMode.setText(String.format("%s...", profileHistories.get(i).getMessage().substring(0, 60)));
            else
            inComingHolder.messageMode.setText(profileHistories.get(i).getMessage());
            inComingHolder.aSwitch.setChecked(ProcessData.loadStatusReport(name,context));

            if(inComingHolder.aSwitch.isChecked()){

                inComingHolder.activatesMode.setImageResource(R.drawable.ic_done);
                inComingHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.LightBlue));

            }else {

                inComingHolder.activatesMode.setImageResource(R.drawable.ic_close);
                inComingHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.White));


            }


            inComingHolder.dateTimeStatus.setText(R.string.TapHereToCheckContacts);

        }catch (Exception e){

            Log.e("taliException",e.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return profileHistories.size();
    }

    class InComingHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{


        TextView messageMode,nameMOde,dateTimeStatus;
        ImageView editMode,deleteMOde,activatesMode;
        Switch aSwitch;
        CardView cardView;

         InComingHolder(@NonNull View itemView) {
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
            dateTimeStatus.setOnClickListener(this);
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                    if(b){

                        activatesMode.setImageResource(R.drawable.ic_done);
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.LightBlue));
                    }
                    else {

                        activatesMode.setImageResource(R.drawable.ic_close);
                        cardView.setCardBackgroundColor(context.getResources().getColor(R.color.White));
                    }
                    ProcessData.saveStatusReport(profileHistories.get(getAdapterPosition()).getProfileName(),context,b);
                }
            });


        }

        @Override
        public void onClick(View view) {

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            switch (view.getId()){

                case R.id.imageDeleteModeRes:

                    alertDialog.setTitle(context.getString(R.string.alert));
                    alertDialog.setMessage(context.getString(R.string.sureYouWantTODelete));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {


                                    ProcessData.deleteProfile(profileHistories.get(getAdapterPosition()).getProfileName(), context, ProcessData.INCOMOING_FILE_NAMES);
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

                                    Intent i=new Intent(context,IncomingCallsSchedule.class);
                                    i.putExtra("edit","yes");

                                    i.putExtra("profile",profileHistories.get(getAdapterPosition()).getProfileName());



                                    context.startActivity(i);
                                    IncomingSMS.modeProfileHistoryIndex=getAdapterPosition();
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




                case R.id.textModeDateStatus:

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

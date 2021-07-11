package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SettingRescyclerView extends RecyclerView.Adapter<SettingRescyclerView.SettingHolder> {
Context context;


private String []settingNames;
    private int selection;

    private boolean enterSave=false;
    private AlertDialog dialog;



    private int upperLimit;
private  String []settingDescriptions;

     SettingRescyclerView(Context context) {
        this.context = context;
        settingNames= context.getResources().getStringArray(R.array.settingTitles);

        settingDescriptions=context.getResources().getStringArray(R.array.settingDescription);






    }

    @NonNull
    @Override
    public SettingHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SettingRescyclerView.SettingHolder(LayoutInflater.from(context).inflate(R.layout.settings_receycler_view,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SettingHolder settingHolder, int i) {


        if(i==0||i==2||i==6){


            settingHolder.settingCategory.setVisibility(View.VISIBLE);
            switch (i){
                case 0:
                  settingHolder.settingCategory.setText(R.string.General);
                    break;


                case 2:
                    settingHolder.settingCategory.setText(R.string.modes);
                    break;

                case 6:
                    settingHolder.settingCategory.setText(R.string.voice);
                    break;



            }
        }

        else
            settingHolder.settingCategory.setVisibility(View.GONE);

        settingHolder.settingName.setText(settingNames[i]);
        settingHolder.settingDescription.setText(settingDescriptions[i]);



    }

    @Override
    public int getItemCount() {
        return settingNames.length;
    }

    class SettingHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView settingName,settingCategory,settingDescription;

         SettingHolder(@NonNull View itemView) {
            super(itemView);

            settingName=itemView.findViewById(R.id.textSettingName);
            settingDescription=itemView.findViewById(R.id.textSettingDescription);
            settingCategory=itemView.findViewById(R.id.textViewCategory);
            itemView.setOnClickListener(this);



        }

        @Override
        public void onClick(View view) {



            AlertDialog.Builder alertDialogBuilder;
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            switch (getAdapterPosition()){


                case 0:


                    if(!takePermission(Manifest.permission.READ_PHONE_STATE,ProcessData.READ_CALLS_PERMISSION)){

                        Toast.makeText(context, R.string.settingsPermissionx,Toast.LENGTH_LONG).show();
                        return;
                    }





                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                            SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
                            if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {

                                List localList = localSubscriptionManager.getActiveSubscriptionInfoList();


                                SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(0);
                                SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);


                                //default
                                final CharSequence sim[] = new String[]{"Default", simInfo1.getDisplayName() + "(" + simInfo1.getNumber() + ")"
                                        , simInfo2.getDisplayName() + "(" + simInfo2.getNumber() + ")"
                                };

                                selection = ProcessData.loadSettingsData("dualsim", context);

                                Log.e("tali", "dsdselection" + selection);

                                dialog = new AlertDialog.Builder(context)
                                        .setTitle(R.string.selectSim)


                                        .setSingleChoiceItems(sim, selection, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                Log.e("tali", "dsds" + i);
                                                selection = i;


                                            }


                                        }).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {

                                                ProcessData.saveSettingsData("dualsim", selection, context);


                                            }
                                        }).setNegativeButton(R.string.cancelOption, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })

                                        .create();
                                dialog.show();


                            } else {


                                alertDialog.setTitle(context.getString(R.string.alert));
                                alertDialog.setMessage(context.getString(R.string.youHaveOneSim));
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {


                                            }
                                        });
                                alertDialog.show();


                            }


                        } else {


                            alertDialog.setTitle(context.getString(R.string.alert));
                            alertDialog.setMessage(context.getString(R.string.featureNotSupportedVersion));
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    });
                            alertDialog.show();

                        }


                    break;

                case 2:


                    if(!takePermission(Manifest.permission.READ_CONTACTS,ProcessData.READ_CONTACTS_PERMISSION)){

                        Toast.makeText(context, R.string.settingsPermissionx,Toast.LENGTH_LONG).show();
                        return;
                    }



                    selection=ProcessData.loadSettingsData("contactsettings",context);

                    dialog = new AlertDialog.Builder(context)
                            .setTitle(R.string.sendMessageTo)


                            .setSingleChoiceItems(new String[]{context.getString(R.string.AllIncomingNumber)
                                    ,context.getString(R.string.onlyToMyContacts),context.getString(R.string.onlyUnknownNumbers)}, selection, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    selection=i;


                                }


                            }).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.e("tali",selection+" select");

                                    ProcessData.saveSettingsData("contactsettings", selection, context);


                                }
                            }).setNegativeButton(R.string.cancelOption, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })

                            .create();
                    dialog.show();



                    break;

                case 1:






                    selection=ProcessData.loadSettingsData("replcallssms",context);

                    dialog = new AlertDialog.Builder(context)
                            .setTitle(R.string.reply)


                            .setSingleChoiceItems(new String[]{context.getString(R.string.bothIncomingCallSms)
                                    ,context.getString(R.string.onlyToSms),
                                    context.getString(R.string.onlyToCalls)
                            }, selection, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    selection=i;


                                }


                            }).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.e("tali",selection+" select");

                                    ProcessData.saveSettingsData("replcallssms", selection, context);


                                }
                            }).setNegativeButton(R.string.cancelOption, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })

                            .create();
                    dialog.show();


                    break;

                case 3:

                    enterSave=false;
                    View limitPickerView= LayoutInflater.from(context).inflate(R.layout.lower_upper_limit,null);
                    final NumberPicker lower=limitPickerView.findViewById(R.id.numberPickerlower);
                    final NumberPicker upper=limitPickerView.findViewById(R.id.numberPickerUpper);


                    lower.setMinValue(1);
                    lower.setMaxValue(30);
                    upper.setMinValue(1);
                    upper.setMaxValue(30);
                   selection=ProcessData.loadSettingsData("lowerlimitnumber",context);
                    upperLimit=ProcessData.loadSettingsData("upperlimitnumber",context);


                    if(selection==0)
                        selection=1;

                    if(upperLimit==0)
                        upperLimit=20;


                    lower.setValue(selection);
                    upper.setValue(upperLimit);









                  alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle(
                            R.string.selectLengthHelp);
                    alertDialogBuilder.setView(limitPickerView);
                    alertDialogBuilder

                            .setPositiveButton(R.string.save,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {


                                            selection=lower.getValue();
                                            upperLimit=upper.getValue();


                                            if(selection<=upperLimit){
                                                ProcessData.saveSettingsData("lowerlimitnumber",selection,context);
                                                ProcessData.saveSettingsData("upperlimitnumber",upperLimit,context);



                                            }



                                        }
                                    })
                            .setNegativeButton(R.string.cancelOption,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                  alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    break;










                case 4:


                         enterSave=false;

                    selection=ProcessData.loadSettingsData("repeatingminutes",context);

                    if(selection>0)
                        selection=2;

                    else if(selection==-1)
                        selection=1;


                    dialog = new AlertDialog.Builder(context)
                            .setTitle(R.string.replyToSecondCallSmsSameContact)


                            .setSingleChoiceItems(new String[]{context.getString(R.string.everyTime)
                                    ,context.getString(R.string.onlyOneTime),
                                    context.getString(R.string.selectMinutes)}, selection, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    enterSave=true;

                                    selection=i;



                                    if(i==2) {
                                        dialog.dismiss();
                                        saveMinute();



                                    }

                                }


                            }).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {


                                    if(selection==1)
                                        ProcessData.saveSettingsData("repeatingminutes",-1,context);
                                    else if(selection==0)
                                        ProcessData.saveSettingsData("repeatingminutes",0,context);


                                }
                            }).setNegativeButton(R.string.cancelOption, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })

                            .create();
                    dialog.show();


                    Log.e("tali",selection+"   addddd    "+enterSave);







                    break;

                case 5:


                    selection=ProcessData.loadSettingsData("silentdatamode",context);


                    dialog = new AlertDialog.Builder(context)
                            .setTitle(R.string.silentDuringMode)


                            .setSingleChoiceItems(new String[]{context.getString(R.string.ON)
                                    ,context.getString(R.string.OFF)
                            }, selection, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {



                                    selection=i;




                                }


                            }).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {



                                        ProcessData.saveSettingsData("silentdatamode",selection,context);




                                }
                            }).setNegativeButton(R.string.cancelOption, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })

                            .create();
                    dialog.show();



                    break;

                case 6:

                    context.startActivity(new Intent(context,PhraseEdit.class));
                    break;

                case 7:
                    context.startActivity(new Intent(context,PhraseOff.class));
                    break;

                case 8:
                    break;

                case 9:
                    break;










            }

        }


        private void saveMinute(){


            Log.e("tali","dsdsdalooooo");

            final View minutePicker= LayoutInflater.from(context).inflate(R.layout.minut_picker_view,null);

            final NumberPicker minute1=minutePicker.findViewById(R.id.numbePickerMinute);



            selection=ProcessData.loadSettingsData("repeatingminutes",context);

            minute1.setMinValue(1);
            minute1.setMaxValue(5000);
            if(selection<=0)
                selection=1;
            minute1.setValue(selection);


















            AlertDialog.Builder alertDialogBuilder;   alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(R.string.sendMessageAfterMinute);
            alertDialogBuilder.setView(minutePicker);
            alertDialogBuilder

                    .setPositiveButton(R.string.save,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {






                                    selection=minute1.getValue();
                                    ProcessData.saveSettingsData("repeatingminutes",selection,context);







                                }
                            })
                    .setNegativeButton(R.string.cancelOption,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            dialog = alertDialogBuilder.create();
            dialog.show();






        }



         boolean takePermission(String permission,int requestCOde)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ActivityCompat.checkSelfPermission(context,permission)== PackageManager.PERMISSION_DENIED)
                {
/*
if(!shouldShowRequestPermissionRationale(permission)&&ASKING_AGAIN_PERMISSION) {




                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent,MICROPHONE_PERMISSION_ACTIIVTY);
                    return false;
                }
*/





                    ActivityCompat.requestPermissions((Activity) context,new String[]{permission},requestCOde);
                    return false;



                }



                else
                    return true;

            }
            return true;


        }




    }
}

package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

import static com.smscallpro.xhahxam.bonitozz.autocallsmspro.ProcessData.ALREADY_EXISTS;
import static com.smscallpro.xhahxam.bonitozz.autocallsmspro.ProcessData.ANDROID9_READ_CALL_PERMISSION;


public class IncomingCallsSchedule extends AppCompatActivity {

   private EditText profileNameIncoming,messageIncoming;
    private final int MICROPHONE_ACTIIVTY=112;

  private   AutoCompleteTextView autoCompleteIncoming;
    private Toolbar toolbar;
   private TextView selectedContactsIncoming;
 private   Handler handler;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_calls_schedule);
        handler=new Handler();
        initializeView();

        ProcessData.contactsPersons=new ArrayList<>();
        if(ProcessData.selectedContacs==null)
            ProcessData.selectedContacs=new ArrayList<>();
        else
            ProcessData.selectedContacs.clear();
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setTitle(R.string.inComing);
        }catch (Exception e){
            Log.e("taliException",e.getLocalizedMessage());
        }

        autoCompleteIncoming.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                ProcessData.selectedContacs.add(ProcessData.contactsPersons.get(i));





                autoCompleteIncoming.setText("");
            }
        });
        editActivity();







    }

    private void initializeView() {


        profileNameIncoming=findViewById(R.id.editIncomingProfileName);
        messageIncoming=findViewById(R.id.messageIncomingBox);
        autoCompleteIncoming=findViewById(R.id.editIncomingPhoneNo);
        selectedContactsIncoming=findViewById(R.id.textIncomingSelectedShower);

        toolbar=findViewById(R.id.toolbar);
        context=this;
      loadContacts();



    }

    private void loadContacts(){



        new Thread(new Runnable() {
            @Override
            public void run() {


                try {

                    if (ProcessData.contactsPersons.isEmpty()) {

                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

                        while (Objects.requireNonNull(phones).moveToNext()) {
                            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            ProcessData.contactsPersons.add(new ContactsPersons(name.trim(), phoneNumber.trim()));



                        }
                        phones.close();

                    }


                    Collections.sort(ProcessData.contactsPersons, new Comparator<ContactsPersons>() {
                        @Override
                        public int compare(ContactsPersons t1, ContactsPersons t2) {

                            return t1.getNameContact().toLowerCase().compareTo(t2.getNameContact().toLowerCase());

                        }
                    });




                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            AutoCompleteTextAdapter autoCompleteTextAdapter = new AutoCompleteTextAdapter(context, ProcessData.contactsPersons);


                            autoCompleteIncoming.setAdapter(autoCompleteTextAdapter);



                        }
                    });


                } catch (Exception exption) {


                    Log.e("taliException", exption.getMessage());

                }

            }
        }).start();




    }

    public void incomingClick(View view) {

        switch (view.getId()){

            case R.id.imageIncomingButtonTemplates:
                onLoadTemplates();
                break;

            case R.id.imageIncmoingMic:
                speechToTextOnClick();
                break;

            case R.id.textIncomingSelectedShower:
                showSendableOnClick();
                break;

            case R.id.imageIncomingButton:
                openContactOnClick();
                break;








        }

    }

    private void onLoadTemplates() {


        String[] mama=this.getResources().getStringArray(R.array.templates);
        final ArrayList<String> listOfTempaltes=new ArrayList<>();
        listOfTempaltes.addAll(Arrays.asList(mama));
        String []temp=ProcessData.loadProfileNames(this,ProcessData.TEMPLATES_FILE);



        if(temp!=null&&temp.length>0) {


            listOfTempaltes.addAll(Arrays.asList(temp));
        }

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfTempaltes);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.selectTemplate);

        builder.setAdapter(itemsAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                messageIncoming.append(" "+listOfTempaltes.get(item));


            }
        });
        AlertDialog alert = builder.create();
        alert.show();






    }


    public void speechToTextOnClick() {

        if(takePermission(Manifest.permission.RECORD_AUDIO,ProcessData.AUDIO_PERMISSION))
            startSpeechRecognition();



    }

    private void startSpeechRecognition()
    {


        Intent intent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        // intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);


        if(intent.resolveActivity(getPackageManager())!=null)
            startActivityForResult(intent,MICROPHONE_ACTIIVTY);
        else
            alertDialogShow(getString(R.string.yourDevicenotsupportMicroPhone));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {

            case MICROPHONE_ACTIIVTY:
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> recordMessage = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (!recordMessage.isEmpty())
                        messageIncoming.append(" " + recordMessage.get(0));


                }


                break;




        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void showSendableOnClick() {


        if(ProcessData.selectedContacs.isEmpty()){

            alertDialogShow(this.getResources().getString(R.string.noContactSeleted));


            return;
        }

        final ArrayList<ContactsPersons> removeIndexes=new ArrayList<>();

        String []select=new String[ProcessData.selectedContacs.size()];
        for(int i=0;i<ProcessData.selectedContacs.size();i++){
            select[i]=(ProcessData.selectedContacs.get(i).getNameContact());
        }


        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.selectedContacts)





                .setMultiChoiceItems(select, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            removeIndexes.add(ProcessData.selectedContacs.get(indexSelected));
                        } else  {
                            removeIndexes.remove(ProcessData.selectedContacs.get(indexSelected));

                        }
                    }
                }).setPositiveButton(getString(R.string.deleteOption), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                        ProcessData.selectedContacs.removeAll(removeIndexes);



                    }
                }).setNegativeButton(getString(R.string.cancelOption), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {



                    }
                }).create();
        dialog.show();





    }






    public void openContactOnClick() {



        if(takePermission(Manifest.permission.READ_CONTACTS,ProcessData.READ_CONTACTS_PERMISSION)){
            Intent intent=new Intent(this,ContactListSelector.class);
            startActivity(intent);
        }




    }




    //permission check funtion
    public boolean takePermission(String permission,int requestCOde)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(permission)== PackageManager.PERMISSION_DENIED)
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






                requestPermissions(new String[]{permission},requestCOde);
                return false;



            }



            else
                return true;

        }
        return true;


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {

            case ProcessData.AUDIO_PERMISSION:
                if (permissions.length > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED)
                            alertDialogShow(getString(R.string.permissionRecordStatment));
                        else
                            startSpeechRecognition();
                    }


                }

                break;


            case ProcessData.READ_CONTACTS_PERMISSION:
                if (permissions.length > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED)
                            alertDialogShow(getString(R.string.permissionContactStatment));
                        else {
                            loadContacts();
                            //startActivity(new Intent(this,ContactListSelector.class));

                        }

                    }




                }
                break;



            case ProcessData.SEND_SMS_PERMISSION:

                if (permissions.length > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED)
                            alertDialogShow(getString(R.string.permissionSMSstatment));
                        else if(takePermission(Manifest.permission.RECEIVE_SMS,ProcessData.READ_SMS_PERMISSION)&&takePermission(Manifest.permission.READ_PHONE_STATE,ProcessData.READ_CALLS_PERMISSION)){
                            if (takePermission(Manifest.permission.READ_CALL_LOG,ProcessData.ANDROID9_READ_CALL_PERMISSION)&&checkUserEnteries())
                            {



                                        saveProfile();




                            }
                        }



                    }




                }

                break;

            case ProcessData.READ_SMS_PERMISSION:
                if (permissions.length > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED)
                            alertDialogShow(getString(R.string.permissionSMSstatment));
                        else if(takePermission(Manifest.permission.SEND_SMS,ProcessData.SEND_SMS_PERMISSION)&&takePermission(Manifest.permission.READ_PHONE_STATE,ProcessData.READ_CALLS_PERMISSION)) {
                            if (takePermission(Manifest.permission.READ_CALL_LOG,ProcessData.ANDROID9_READ_CALL_PERMISSION)&&checkUserEnteries()) {



                                    saveProfile();






                            }
                            }



                    }




                }

                break;


            case ANDROID9_READ_CALL_PERMISSION:
                if (permissions.length > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED)
                            alertDialogShow(getString(R.string.permissionSMSstatment));
                        else if (takePermission(Manifest.permission.READ_PHONE_STATE, ProcessData.READ_CALLS_PERMISSION) && takePermission(Manifest.permission.SEND_SMS, ProcessData.SEND_SMS_PERMISSION) && takePermission(Manifest.permission.RECEIVE_SMS, ProcessData.READ_SMS_PERMISSION)) {
                            if (checkUserEnteries()) {
                                saveProfile();
                            }
                        }

                    }
                }
                            break;


            case ProcessData.READ_CALLS_PERMISSION:
                if (permissions.length > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED)
                            alertDialogShow(getString(R.string.permissionSMSstatment));
                        else if(takePermission(Manifest.permission.SEND_SMS,ProcessData.SEND_SMS_PERMISSION)&&takePermission(Manifest.permission.RECEIVE_SMS,ProcessData.READ_SMS_PERMISSION)) {
                            if (takePermission(Manifest.permission.READ_CALL_LOG,ProcessData.ANDROID9_READ_CALL_PERMISSION)&&checkUserEnteries())
                            {


                                        saveProfile();




                            }
                        }
                    }


                }

                break;

        }
    }

    private void alertDialogShow(String alert){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(this.getString(R.string.alert));
        alertDialog.setMessage(alert);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
            if (getIntent().getStringExtra("edit").contains("yes"))
                menu.findItem(R.id.menuSave).setTitle(R.string.update);

        }catch (Exception e){

            Log.e("taliException",e.getMessage());
        }

        return  true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menuSave:
                if(takePermission(Manifest.permission.SEND_SMS,ProcessData.SEND_SMS_PERMISSION)&&takePermission(Manifest.permission.RECEIVE_SMS,ProcessData.READ_SMS_PERMISSION)
                       &&takePermission(Manifest.permission.READ_PHONE_STATE,ProcessData.READ_CALLS_PERMISSION)
                        ){

                    if(checkUserEnteries()) {
                        saveProfile();

                    }

                }

                break;





        }


        return super.onOptionsItemSelected(item);
    }

    private void passDataBack(String profileN){

        Intent intent = new Intent();
        try {


            intent.putExtra("profilename", profileN);
            if (getIntent().getStringExtra("edit").equals("yes"))
                IncomingSMS.modeProfileHistoryName=profileN;


        }catch (Exception e){

            Log.e("taliException",e.getMessage());
        }
        setResult(RESULT_OK, intent);
        ProcessData.selectedContacs.clear();
        ProcessData.contactsPersons=null;
        finish();



    }


    private boolean checkUserEnteries() {


        String profileN = "";
        String message = "";
        profileN = profileNameIncoming.getText().toString().trim().toLowerCase();
        message = messageIncoming.getText().toString().trim();




        if (profileN == null || profileN.matches("")) {

            alertDialogShow(getString(R.string.pleaseEnterCorrectProfile));
            return false;
        }
        if (message == null || message.matches("")) {

            alertDialogShow(getString(R.string.EnterValidMessage));
            return false;
        }





        if (ProcessData.selectedContacs.isEmpty()) {

            alertDialogShow(getString(R.string.noContactSeleted));
            return false;

        }




        profileN= profileN+ProcessData.INCOMING_LOGO;
        SharedPreferences pref = getApplicationContext().getSharedPreferences(profileN, MODE_PRIVATE);
        if (pref.getBoolean(ALREADY_EXISTS, false)) {

            String profileFromIntent=getIntent().getStringExtra("profile");
            String fromIntent=getIntent().getStringExtra("edit");

            if(profileFromIntent!=null&&fromIntent!=null) {
                if (profileFromIntent.equals(profileN) && fromIntent.equals("yes")) {
                    return true;
                }
            }

            else {

                alertDialogShow(getString(R.string.tryanother));
                return false;
            }



        }


        return true;

    }



    private void saveProfile(){


        ProfileHistory profileHistory;

        String repeat="no";








      /*  if(deactivateMode.isChecked())
        {
            timeFrom=-1;
            timeTo=-1;

        }*/
/*        else if(weeklyMode.isChecked()){

            repeat="weekly";
            for(int i=0;i<selectedWeekDays.size();i++){
                if(selectedWeekDays.get(i)){

                    repeat=repeat+"="+(i+1);

                }}





        }*/

    String namesOfContacts[];
        String phoneOfContacts[];


     /*   if(selectAll.isChecked()){


            namesOfContacts=null;
            phoneOfContacts=null;


        }else {
*/

            namesOfContacts=ProcessData.returnContactNames(ProcessData.selectedContacs);
            phoneOfContacts=ProcessData.returnPhoneNumber(ProcessData.selectedContacs);




       // }

        profileHistory=new ProfileHistory((profileNameIncoming.getText().toString().toLowerCase())+ProcessData.INCOMING_LOGO, messageIncoming.getText().toString(),
                -22, phoneOfContacts, namesOfContacts,
                -1,-1);


        String oldProfile=getIntent().getStringExtra("profile");
        if(oldProfile!=null)
            ProcessData.deleteProfile(oldProfile,getApplicationContext(),ProcessData.INCOMOING_FILE_NAMES);

        ProcessData.saveProfile(profileHistory,repeat,this,ProcessData.INCOMOING_FILE_NAMES);
        ProcessData.saveStatusReport(profileHistory.getProfileName(),context,true);
        passDataBack(profileHistory.getProfileName());





    }



    @Override
    public void onBackPressed() {

        try {
            if (profileNameIncoming.getText().toString().trim().length() > 0 || messageIncoming.getText().toString().length() > 0 || !ProcessData.selectedContacs.isEmpty()) {

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(getString(R.string.alert));
                alertDialog.setMessage(getString(R.string.goBackStatment));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                ProcessData.selectedContacs.clear();
                                ProcessData.calendarMain=null;
                                ProcessData.contactsPersons=null;
                              //  IncomingCallsSchedule.super.onBackPressed();


                                dialog.dismiss();
                                finish();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();

            }
            else {
                ProcessData.contactsPersons=null;
                finish();
             //   super.onBackPressed();
            }

        }catch (Exception e){
          //  super.onBackPressed();

            Log.e("taliException",e.getMessage());
            finish();
        }


    }

    private void editActivity(){


        try {
            if (getIntent().getStringExtra("edit").equals("yes")) {


                String profileFromIntent = getIntent().getStringExtra("profile");

                final ProfileHistory pro = ProcessData.loadProfile(profileFromIntent, getApplicationContext());
                if (pro == null)
                    return;






              /*  final String[] repeated = Objects.requireNonNull(ProcessData.getRepeated(profileFromIntent, getApplicationContext())).split("=");
                if (repeated[0].equals("weekly"))
                    for (int i = 1; i < repeated.length; i++) {
                        int index = Integer.parseInt(repeated[i]) - (1);
                        selectedWeekDays.add(index, true);
                        selectedWeekDays.remove(index + 1);

                    }*/

                if(pro.getNames()!=null&&pro.getNames().length!=0)
                    for (int i = 0; i < pro.getNames().length; i++) {

                        ProcessData.selectedContacs.add(new ContactsPersons(pro.getNames()[i], pro.getPhoneNumber()[i]));

                    }


                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    /*    switch (repeated[0]) {

                            case "no":

                                if(pro.getDate()==-1) {
                                    deactivateMode.setChecked(true);

                                }
                                else{

                                    calendarFrom.setTimeInMillis(pro.getDate());
                                    calendarTo.setTimeInMillis(pro.getSavingDate());
                                    dateTimeMode.setChecked(true);
                                    buttonFrom.setText(String.format("%s: %s %s", getApplicationContext().getString(R.string.time), ProcessData.convertTimeFormate(calendarFrom, getApplicationContext()), form.format(pro.getDate())));

                                    buttonTo.setText(String.format("%s: %s %s", getApplicationContext().getString(R.string.time), ProcessData.convertTimeFormate(calendarTo, getApplicationContext()), form.format(pro.getSavingDate())));

                                    setVisibilityOfFromAndTo(View.VISIBLE);
                                }

                                break;





                            case "weekly":

                                weeklyMode.setChecked(true);

                                Calendar from=Calendar.getInstance();
                                from.setTimeInMillis(pro.getDate());
                                Calendar too=Calendar.getInstance();
                                too.setTimeInMillis(pro.getSavingDate());
                                calendarFrom.set(Calendar.HOUR_OF_DAY,from.get(Calendar.HOUR_OF_DAY));
                                calendarFrom.set(Calendar.MINUTE,from.get(Calendar.MINUTE));

                                calendarTo.set(Calendar.HOUR_OF_DAY,too.get(Calendar.HOUR_OF_DAY));
                                calendarTo.set(Calendar.MINUTE,too.get(Calendar.MINUTE));


                                buttonFrom.setText(String.format("%s: %s", getApplicationContext().getString(R.string.time), ProcessData.convertTimeFormate(calendarFrom, getApplicationContext())));

                                buttonTo.setText(String.format("%s: %s", getApplicationContext().getString(R.string.time), ProcessData.convertTimeFormate(calendarTo, getApplicationContext())));


                                setVisibilityOfFromAndTo(View.VISIBLE);
                                break;



                        }


                        if(pro.getNames()!=null&&pro.getNames().length!=0){

                            selectExcept.setChecked(true);
                            textContatedSelected.setVisibility(View.VISIBLE);
                        }
                        else {
                            selectAll.setChecked(true);

                        }*/


                        profileNameIncoming.setText((pro.getProfileName().replaceAll(ProcessData.INCOMING_LOGO,"")).toUpperCase());

                        messageIncoming.setText(pro.getMessage());

                    }
                });


            }


        }catch (Exception e){


            Log.e("taliException",e.getMessage());

        }
    }


}

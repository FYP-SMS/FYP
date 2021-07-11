package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import static  com.smscallpro.xhahxam.bonitozz.autocallsmspro.ProcessData.ALREADY_EXISTS;
import static com.smscallpro.xhahxam.bonitozz.autocallsmspro.ProcessData.calendarMain;
import static com.smscallpro.xhahxam.bonitozz.autocallsmspro.ProcessData.contactsPersons;
import static com.smscallpro.xhahxam.bonitozz.autocallsmspro.ProcessData.selectedContacs;

public class AddOutGoingSms extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    private static final int  MICROPHONE_ACTIIVTY=21;
    private static final int CONTACT_LIST_AVTIVITY=10;
    Handler handler;
    private  ArrayList<Boolean> selectedWeekDays;
    private boolean checkChangedPreventer=true;
    private Context context;
    private int[] customeCheckProcess={-1,1,0,0};
    private EditText messageBox;
    private int oldRequestCode=0;
    private TextView contactListShower;
    private AutoCompleteTextView phoneNumberText;
    private RadioButton radioWeekly;
    private RadioButton radioDaily;
    private RadioButton radioSpecific;
    private RadioButton radioCustom;
    private EditText editProfileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goingsms);
        handler=new Handler();
        Toolbar toolbar = findViewById(R.id.toolbar);
editProfileName=findViewById(R.id.editProfileName);


       if(calendarMain!=null)
           calendarMain=null;



            calendarMain= Calendar.getInstance();




        messageBox=findViewById(R.id.messageBox);
        context=this;

        radioWeekly=findViewById(R.id.radioWeekly);
        radioDaily=findViewById(R.id.radioDaily);
        radioCustom=findViewById(R.id.radioCustom);

        radioSpecific=findViewById(R.id.radioSpecfic);

        phoneNumberText=findViewById(R.id.editPhoneNo);


        if(contactsPersons==null)
            contactsPersons=new ArrayList<>();
            if(selectedContacs==null)
                selectedContacs=new ArrayList<>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED)
                    loadContacts();
            }
            else
                loadContacts();



        phoneNumberText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



                selectedContacs.add(contactsPersons.get(i));

                refreshList();


                phoneNumberText.setText("");
            }
        });





        selectedWeekDays=new ArrayList<>();


        for(int i=0;i<8;i++)
            selectedWeekDays.add(false);



        String time=ProcessData.convertTimeFormate(calendarMain,context);

        Button b= findViewById(R.id.imageButton6);
        b.setText(String.format("%s%s", getString(R.string.time), time));


        contactListShower=findViewById(R.id.TextSelectedShower);


            if (selectedContacs.isEmpty())
                contactListShower.setVisibility(View.GONE);


        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setTitle(R.string.outgoing);
        }catch (Exception e){
            Log.e("taliException",e.getLocalizedMessage());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                editActivity();
            }
        }).start();





    }


    private void editActivity(){

        try {
            if (getIntent().getStringExtra("edit").equals("yes")) {

                String profileFromIntent = getIntent().getStringExtra("profile");

                final ProfileHistory oldProfileHistory = ProcessData.loadProfile(profileFromIntent, context);
                if (oldProfileHistory == null)
                    return;

                oldRequestCode = oldProfileHistory.getRequestCode();
                ProcessData.calendarMain.setTimeInMillis(oldProfileHistory.getDate());

                for (int i = 0; i < oldProfileHistory.getNames().length; i++) {
                    selectedContacs.add(new ContactsPersons(oldProfileHistory.getNames()[i], oldProfileHistory.getPhoneNumber()[i]));
                }


                final String[] repeated = ProcessData.getRepeated(profileFromIntent, context).split("=");
                if (repeated[0].equals("weekly"))
                    for (int i = 1; i < repeated.length; i++) {
                        int index = Integer.parseInt(repeated[i]) - (1);
                        selectedWeekDays.add(index, true);
                        selectedWeekDays.remove(index + 1);
                    }

                else if(repeated[0].equals("custom")){
                        customeCheckProcess[0]   = Integer.parseInt(repeated[1]);
                        customeCheckProcess[1] = Integer.parseInt(repeated[2]);
                        customeCheckProcess[2] = Integer.parseInt(repeated[3]);
                        customeCheckProcess[3] = Integer.parseInt(repeated[4]);
                    }

                checkChangedPreventer = false;
                handler.post(new Runnable() {
                        @Override
                        public void run() {

                            switch (repeated[0]) {

                                case "no":
                                    radioSpecific.setChecked(true);
                                    break;

                                case "daily":
                                    radioDaily.setChecked(true);
                                    break;

                                case "weekly":
                                    radioWeekly.setChecked(true);
                                    break;

                                case "custom":
                                    radioCustom.setChecked(true);
                                    break;

                            }

                            checkChangedPreventer = true;

                            String time=ProcessData.convertTimeFormate(calendarMain,context);

                            Button b= findViewById(R.id.imageButton6);
                            b.setText(String.format("%s%s", getString(R.string.time), time));

                            editProfileName.setText(oldProfileHistory.getProfileName().toUpperCase());
                            refreshList();
                            messageBox.setText(oldProfileHistory.getMessage());

                        }
                });

            }


        }
        catch ( Exception e){

            Log.e("taliException",e.getLocalizedMessage());

        }

    }

    private void loadContacts(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    if (contactsPersons.isEmpty()) {

                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

                        while (phones.moveToNext()) {
                            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactsPersons.add(new ContactsPersons(name, phoneNumber.trim().replaceAll(" ","")));

                        }
                        phones.close();

                    }

                    Collections.sort(contactsPersons, new Comparator<ContactsPersons>() {
                        @Override
                        public int compare(ContactsPersons t1, ContactsPersons t2) {

                            return t1.getNameContact().toLowerCase().compareTo(t2.getNameContact().toLowerCase());

                        }
                    });

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            AutoCompleteTextAdapter autoCompleteTextAdapter = new AutoCompleteTextAdapter(context, contactsPersons);
                            phoneNumberText.setAdapter(autoCompleteTextAdapter);

                        }
                    });

                }
                catch (Exception exption) {

                    Log.e("taliException", exption.getMessage());

                }

            }
        }).start();

    }

    public void speechToTextOnClick(View view) {

        if(takePermission(Manifest.permission.RECORD_AUDIO,ProcessData.AUDIO_PERMISSION))
            startSpeechRecognition();

    }

    private void startSpeechRecognition() {

        Intent intent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
       // intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);


        if(intent.resolveActivity(getPackageManager())!=null)
            startActivityForResult(intent,MICROPHONE_ACTIIVTY);
        else
            alertDialogShow(getString(R.string.yourDevicenotsupportMicroPhone));
    }

    private void alertDialogShow(String alert){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(context.getString(R.string.alert));
        alertDialog.setMessage(alert);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        alertDialog.show();

    }

//permission check funtion
    public boolean takePermission(String permission,int requestCOde) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(permission)==PackageManager.PERMISSION_DENIED) {
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

                        }

                    }




                }
                break;



            case ProcessData.SEND_SMS_PERMISSION:
                if (permissions.length > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED)
                            alertDialogShow(getString(R.string.permissionSMSstatment));
                        else
                            checkSending();


                    }




                }
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {

            case MICROPHONE_ACTIIVTY:
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> recordMessage = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (!recordMessage.isEmpty())
                        messageBox.append(" " + recordMessage.get(0));


                }


                break;
            case CONTACT_LIST_AVTIVITY:


                refreshList();

                break;



        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void openContactOnClick(View view) {



        if(takePermission(Manifest.permission.READ_CONTACTS,ProcessData.READ_CONTACTS_PERMISSION)){
            openContactList();
        }




    }


    private void refreshList(){

        if (!selectedContacs.isEmpty()) {

            StringBuilder names=new StringBuilder();
            names.append("");


            for (int i = 0; i < selectedContacs.size(); i++) {

                if(i==0)
                    names.append(selectedContacs.get(i).getNameContact());

                else
                names.append(", ").append(selectedContacs.get(i).getNameContact());



                if (i == 8) {


                    break;
                }
            }



            contactListShower.setVisibility(View.VISIBLE);
            contactListShower.setText(names.toString());





        }

        else
            contactListShower.setVisibility(View.GONE);





    }

    private void  openContactList() {




        Intent intent=new Intent(this,ContactListSelector.class);
        startActivityForResult(intent,CONTACT_LIST_AVTIVITY);





    }

    @Override
    public void onBackPressed() {

        String extra=getIntent().getStringExtra("edit");
        if(extra==null)
            extra=" ";

        if((messageBox.getText().toString().length()>1||!selectedContacs.isEmpty()||radioSpecific.isChecked()||
                radioDaily.isChecked()||radioWeekly.isChecked()||radioCustom.isChecked())&&!extra.contains("yes")){

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.alert));
            alertDialog.setMessage(getString(R.string.goBackStatment));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            selectedContacs.clear();

                            //AddOutGoingSms.super.onBackPressed();


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

        else{

            selectedContacs.clear();


            finish();
            //super.onBackPressed();
        }
    }

    public void setDateOnClick() {


                 DialogFragment datePickerFragment=new DatePickerFragment();


                        datePickerFragment.show(getSupportFragmentManager(),"DATE PICKER");





    }

    public void setTimeOnClick(View view) {

        DialogFragment timePickerDialog=new TimePickerFragment();
        timePickerDialog.show(getSupportFragmentManager(),"TIME PICKER");


    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        Calendar dummyC=Calendar.getInstance();

        if(i<dummyC.get(Calendar.YEAR)){

            alertDialogShow(getString(R.string.lessThanCurrentDate));
            return;
        }

        if(i==dummyC.get(Calendar.YEAR)&&i1<dummyC.get(Calendar.MONTH)){
            alertDialogShow(getString(R.string.lessThanCurrentDate));
            return;
        }
        if(i==dummyC.get(Calendar.YEAR)&&i1==dummyC.get(Calendar.MONTH)&&i2<dummyC.get(Calendar.DAY_OF_MONTH)){
            alertDialogShow(getString(R.string.lessThanCurrentDate));
            return;
        }


        calendarMain.set(Calendar.YEAR,i);
        calendarMain.set(Calendar.MONTH,i1);
        calendarMain.set(Calendar.DAY_OF_MONTH,i2);

        if(radioCustom.isChecked())
            radioCustomeDialog();




    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

        calendarMain.set(Calendar.HOUR_OF_DAY,i);
        calendarMain.set(Calendar.MINUTE,i1);
        calendarMain.set(Calendar.SECOND,0);

        Button b= findViewById(R.id.imageButton6);
        String time=ProcessData.convertTimeFormate(calendarMain,context);
        b.setText(String.format("%s%s", context.getString(R.string.time), time));

    }

    public void showSendableOnClick(View view) {


        final ArrayList<ContactsPersons> removeIndexes=new ArrayList<>();

        String []select=new String[selectedContacs.size()];
        for(int i=0;i<selectedContacs.size();i++){
            select[i]=(selectedContacs.get(i).getNameContact());
        }


        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.selectedContacts)





               .setMultiChoiceItems(select, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                          removeIndexes.add(selectedContacs.get(indexSelected));
                        } else  {
                            removeIndexes.remove(selectedContacs.get(indexSelected));

                        }
                    }
                }).setPositiveButton(getString(R.string.deleteOption), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                       selectedContacs.removeAll(removeIndexes);

                       refreshList();



                    }
                }).setNegativeButton(getString(R.string.cancelOption), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        refreshList();

                    }
                }).create();
        dialog.show();





    }

    private void radioCustomeDialog(){



        View view= LayoutInflater.from(this).inflate(R.layout.custom_for_dialog,null);
        final NumberPicker dates=view.findViewById(R.id.numberPicker);
        final NumberPicker hours=view.findViewById(R.id.numberPicker2);
        final NumberPicker mints=view.findViewById(R.id.numberPicker3);
        dates.setMaxValue(31);
        dates.setMinValue(0);
        hours.setMaxValue(23);
        hours.setMinValue(0);
        mints.setMaxValue(59);
        mints.setMinValue(1);

        mints.setValue(customeCheckProcess[1]);
        hours.setValue(customeCheckProcess[2]);
        dates.setValue(customeCheckProcess[3]);


        Spinner spinner=view.findViewById(R.id.spinnerID);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                customeCheckProcess[0]=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinneItem, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        if(customeCheckProcess[0]==-1)
       customeCheckProcess[0]=0;

            spinner.setSelection(customeCheckProcess[0]);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.setRepeatingTime);
        alertDialogBuilder.setView(view);
        alertDialogBuilder

                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {



                                customeCheckProcess[1]=mints.getValue();
                                customeCheckProcess[2]=hours.getValue();
                                customeCheckProcess[3]=dates.getValue();

                            }
                        })
                .setNegativeButton(R.string.cancelOption,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();








    }

    public void weekSelectDialog() {



        boolean []checkItems={selectedWeekDays.get(0), selectedWeekDays.get(1), selectedWeekDays.get(2), selectedWeekDays.get(3),
                selectedWeekDays.get(4), selectedWeekDays.get(5), selectedWeekDays.get(6)};
        final CharSequence[] items = getApplicationContext().getResources().getStringArray(R.array.weekdays);



        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.selectWeeks)


                .setMultiChoiceItems(items, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items

                            selectedWeekDays.add(indexSelected,true);
                            selectedWeekDays.remove(indexSelected+1);
                        } else  {
                            selectedWeekDays.add(indexSelected,false);
                            selectedWeekDays.remove(indexSelected+1);

                        }
                    }
                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                    }
                }).create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try {
            getMenuInflater().inflate(R.menu.edit_menu, menu);


            if (getIntent().getStringExtra("edit").contains("yes"))
                menu.findItem(R.id.menuSave).setTitle(R.string.update);
        }catch (Exception e){

            Log.e("taliException",e.getLocalizedMessage());

        }


        return  true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menuSave:
                sendSMS();


                break;





        }


        return super.onOptionsItemSelected(item);
    }

    private void sendSMS(){

        if(takePermission(Manifest.permission.SEND_SMS,ProcessData.SEND_SMS_PERMISSION)) {
            checkSending();
        }



    }

    private void checkSending(){




        try{

       EditText profileName=findViewById(R.id.editProfileName);
        String profileN=profileName.getText().toString();
        profileN=profileN.trim().toLowerCase();


            messageBox.getText().toString().trim();







        if(profileN==null||profileN.matches("")){

            alertDialogShow(getString(R.string.pleaseEnterCorrectProfile));
            return;
        }


        if(selectedContacs.isEmpty()){

            alertDialogShow(getString(R.string.noContactSeleted));
            return;

        }

        if(radioSpecific.isChecked()||radioCustom.isChecked()){


            if(calendarMain.before(Calendar.getInstance())){

                alertDialogShow(getString(R.string.inExactScheduleTimeMustBeGreaterThan));
                return;


            }

        }

        if(!(selectedWeekDays.contains(true))&&radioWeekly.isChecked()){

            alertDialogShow(getString(R.string.noWeeksDaysSelected));
            return;

        }

        if(radioCustom.isChecked()&&customeCheckProcess[0]==0){

            alertDialogShow(getString(R.string.selectSendOnly));
            return;

        }

        if(!radioCustom.isChecked()&&!radioSpecific.isChecked()&&!radioWeekly.isChecked()&&!radioDaily.isChecked()){

            alertDialogShow(getString(R.string.selectCheckBox));
            return;

        }



        SharedPreferences pref = getApplicationContext().getSharedPreferences(profileN, MODE_PRIVATE);
        if (pref.getBoolean(ALREADY_EXISTS, false)){


            String profileFromIntent=getIntent().getStringExtra("profile");
            String fromIntent=getIntent().getStringExtra("edit");

            if(profileFromIntent!=null&&fromIntent!=null) {
                if (profileFromIntent.equals(profileN) && fromIntent.equals("yes")) ;
            }
            else {

                alertDialogShow(getString(R.string.tryanother));
                return;
            }
        }






        setAlarm(profileN);


        }catch (Exception e){

            alertDialogShow(getString(R.string.error));


        }



    }

    @Override
    protected void onDestroy() {



        Runtime.getRuntime().gc();
        phoneNumberText.setAdapter(null);

        contactsPersons=null;
        selectedContacs.clear();

        super.onDestroy();
    }

    private void setAlarm(String profileN){






        if(radioSpecific.isChecked()){


        ProfileHistory profileHistory=new ProfileHistory(profileN, messageBox.getText().toString(),
                ProcessData.getRequestCode(this), ProcessData.returnPhoneNumber(selectedContacs), ProcessData.returnContactNames(selectedContacs),
                calendarMain.getTimeInMillis(),Calendar.getInstance().getTimeInMillis());


        String oldProfile=getIntent().getStringExtra("profile");
        if(oldProfile!=null) {
            ProcessData.cancelAlarm(context,oldProfile,oldRequestCode);
            ProcessData.deleteProfile(oldProfile, context, ProcessData.OUTGOING_FILE_NAMES);

        }
        ProcessData.saveProfile(profileHistory,"no",this,ProcessData.OUTGOING_FILE_NAMES);





            Intent i = new Intent(this, OnAlarmReciver.class);
            i.putExtra("profile",profileN);
            PendingIntent pi = PendingIntent.getBroadcast(this, profileHistory.getRequestCode(), i, 0);
            ProcessData.setAlarm(pi,calendarMain.getTimeInMillis(),context);


            passDataBack(profileN);




        }

         else    if(radioDaily.isChecked()){



            Calendar calendar = Calendar.getInstance();






            calendar.set(Calendar.HOUR_OF_DAY,calendarMain.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendarMain.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if(calendar.before(Calendar.getInstance())){


                calendar.add( Calendar.DATE,1);


            }

            ProfileHistory profileHistory=new ProfileHistory(profileN, messageBox.getText().toString(),
                    ProcessData.getRequestCode(this), ProcessData.returnPhoneNumber(selectedContacs), ProcessData.returnContactNames(selectedContacs),
                    calendar.getTimeInMillis(),Calendar.getInstance().getTimeInMillis());




            String oldProfile=getIntent().getStringExtra("profile");
            if(oldProfile!=null) {
                ProcessData.cancelAlarm(context,oldProfile,oldRequestCode);
                ProcessData.deleteProfile(oldProfile, context, ProcessData.OUTGOING_FILE_NAMES);

            }

            ProcessData.saveProfile(profileHistory,"daily",this,ProcessData.OUTGOING_FILE_NAMES);


            Intent i = new Intent(this, OnAlarmReciver.class);
            i.putExtra("profile",profileN);
            PendingIntent pi = PendingIntent.getBroadcast(this, profileHistory.getRequestCode(), i, 0);
            ProcessData.setAlarm(pi,calendar.getTimeInMillis(),context);


            passDataBack(profileN);










            }
          else   if(radioWeekly.isChecked()){




            Calendar calendar = Calendar.getInstance();






            calendar.set(Calendar.HOUR_OF_DAY,calendarMain.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendarMain.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND, 0);

            if(calendar.before(Calendar.getInstance())){


                Log.e("tali121","exex");
                calendar.add( Calendar.DATE,1);

            }


            String repeating="weekly";
            for(int i=0;i<selectedWeekDays.size();i++){
                if(selectedWeekDays.get(i)){

                repeating=repeating+"="+(i+1);
                    Log.e("tali121",repeating);
            }}












            ProfileHistory profileHistory=new ProfileHistory(profileN, messageBox.getText().toString(),
                    ProcessData.getRequestCode(this), ProcessData.returnPhoneNumber(selectedContacs), ProcessData.returnContactNames(selectedContacs),
                    calendar.getTimeInMillis(),Calendar.getInstance().getTimeInMillis());


            String oldProfile=getIntent().getStringExtra("profile");
            if(oldProfile!=null) {
                ProcessData.cancelAlarm(context,oldProfile,oldRequestCode);
                ProcessData.deleteProfile(oldProfile, context, ProcessData.OUTGOING_FILE_NAMES);

            }
            ProcessData.saveProfile(profileHistory,repeating,this,ProcessData.OUTGOING_FILE_NAMES);

            Intent i = new Intent(this, OnAlarmReciver.class);
            i.putExtra("profile",profileN);
            PendingIntent pi = PendingIntent.getBroadcast(this, profileHistory.getRequestCode(), i, 0);
            ProcessData.setAlarm(pi,calendar.getTimeInMillis(),context);

            passDataBack(profileN);









            }
         else    if(radioCustom.isChecked()){












            String repeatings="custom="+customeCheckProcess[0]+"="+customeCheckProcess[1]+"="+customeCheckProcess[2]+"="+customeCheckProcess[3];
            ProfileHistory profileHistory=new ProfileHistory(profileN, messageBox.getText().toString(),
                    ProcessData.getRequestCode(this), ProcessData.returnPhoneNumber(selectedContacs), ProcessData.returnContactNames(selectedContacs),
                    calendarMain.getTimeInMillis(),Calendar.getInstance().getTimeInMillis());


            String oldProfile=getIntent().getStringExtra("profile");
            if(oldProfile!=null) {
                ProcessData.cancelAlarm(context,oldProfile,oldRequestCode);
                ProcessData.deleteProfile(oldProfile, context, ProcessData.OUTGOING_FILE_NAMES);

            }
             ProcessData.saveProfile(profileHistory,repeatings,this,ProcessData.OUTGOING_FILE_NAMES);

            Intent i = new Intent(this, OnAlarmReciver.class);
            i.putExtra("profile",profileN);
            PendingIntent pi = PendingIntent.getBroadcast(this, profileHistory.getRequestCode(), i, 0);
            ProcessData.setAlarm(pi,calendarMain.getTimeInMillis(),context);
            passDataBack(profileN);

        }

    }

    private void passDataBack(String profileN){
        Intent intent = new Intent();
        try {

            intent.putExtra("profilename", profileN);
            if (getIntent().getStringExtra("edit").equals("yes"))
             OutGoingSMS.profileNames=profileN;


        }catch (Exception e){

Log.e("taliException",e.getLocalizedMessage()) ;}
        setResult(RESULT_OK, intent);
        finish();


    }

    public void OnRadioButtonsClick(View view) {


        if(!checkChangedPreventer)
            return;


        switch (view.getId()) {


            case R.id.radioDaily:


                break;

            case R.id.radioWeekly:

                weekSelectDialog();

                break;


            case R.id.radioSpecfic:
                setDateOnClick();
                break;

            case R.id.radioCustom:
                setDateOnClick();
                break;


        }
    }

    public void onLoadTemplates(View view) {


        String[] mama=context.getResources().getStringArray(R.array.templates);
        final ArrayList<String> listOfTempaltes = new ArrayList<>();
        listOfTempaltes.addAll(Arrays.asList(mama));
        String []temp=ProcessData.loadProfileNames(context,ProcessData.TEMPLATES_FILE);



        if(temp!=null&&temp.length>0) {


            listOfTempaltes.addAll(Arrays.asList(temp));
        }

       ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, listOfTempaltes);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.selectTemplate);

        builder.setAdapter(itemsAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                messageBox.append(" "+listOfTempaltes.get(item));


            }
        });
        AlertDialog alert = builder.create();
        alert.show();






    }

}

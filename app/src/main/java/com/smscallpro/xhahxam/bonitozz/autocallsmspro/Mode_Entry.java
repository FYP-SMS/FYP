package com.smscallpro.xhahxam.bonitozz.autocallsmspro;



import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.RadioButton;

import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.smscallpro.xhahxam.bonitozz.autocallsmspro.ProcessData.ALREADY_EXISTS;
import static com.smscallpro.xhahxam.bonitozz.autocallsmspro.ProcessData.ANDROID9_READ_CALL_PERMISSION;


public class Mode_Entry extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {


    private final int MICROPHONE_ACTIIVTY=112;
    private  ArrayList<Boolean> selectedWeekDays;

    private EditText modeName, modeMessage;
  private   Handler handler;
    private Toolbar toolbar;
    private TextView to;

    private int oldRequest=-22;
    private RadioButton selectAll, selectExcept,deactivateMode,dateTimeMode,weeklyMode;

    private Button buttonFrom, buttonTo, textContatedSelected;


    private Calendar calendarTo, calendarFrom;
    private SimpleDateFormat form;


    private boolean conflictToFrom=true;  //true for from





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode__entry);

        handler=new Handler();
        initializeView();
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle(getResources().getString(R.string.mode));
        }catch (Exception e){
            Log.e("taliException",e.getLocalizedMessage());
        }
        ProcessData.selectedContacs.clear();
        editActivity();




    }

    private void setVisibilityOfFromAndTo(int visible) {

        buttonFrom.setVisibility(visible);
        buttonTo.setVisibility(visible);
        to.setVisibility(visible);


    }


    private void initializeView() {

        if(ProcessData.calendarMain==null)
            ProcessData.calendarMain=Calendar.getInstance();
        modeName = this.findViewById(R.id.textModeName);
        modeMessage = this.findViewById(R.id.textModeMessage);
        textContatedSelected = this.findViewById(R.id.textSelectedContacts);
        to = this.findViewById(R.id.to);


        selectAll = this.findViewById(R.id.replyAll);
        selectExcept = this.findViewById(R.id.replyExcept);

        buttonFrom = this.findViewById(R.id.buttonFrom);
        buttonTo = this.findViewById(R.id.buttonTo);


        deactivateMode = this.findViewById(R.id.radioIDeactivate);
        dateTimeMode = this.findViewById(R.id.radioDateTime);
        weeklyMode = this.findViewById(R.id.radioByWeek);

        toolbar=findViewById(R.id.toolbar);
        calendarFrom = Calendar.getInstance();
        calendarTo = Calendar.getInstance();
        form = new SimpleDateFormat("MMM dd yyyy");
        buttonFrom.setText(form.format(calendarFrom.getTime()));
        buttonTo.setText(form.format(calendarFrom.getTime()));

        ProcessData.selectedContacs.clear();

            textContatedSelected.setVisibility(View.GONE);
        setVisibilityOfFromAndTo(View.GONE);

        selectedWeekDays=new ArrayList<>();
        for(int i=0;i<8;i++)
            selectedWeekDays.add(false);


    }

    public void setDateOnClick() {


        if(conflictToFrom)
            ProcessData.calendarMain.setTimeInMillis(calendarFrom.getTimeInMillis());
        else
            ProcessData.calendarMain.setTimeInMillis(calendarTo.getTimeInMillis());


        DialogFragment datePickerFragment = new DatePickerFragment();


        datePickerFragment.show(getSupportFragmentManager(), "DATE PICKER");


    }

    public void setTimeOnClick() {


        if(conflictToFrom)
            ProcessData.calendarMain.setTimeInMillis(calendarFrom.getTimeInMillis());
        else
            ProcessData.calendarMain.setTimeInMillis(calendarTo.getTimeInMillis());

        DialogFragment timePickerDialog = new TimePickerFragment();
        timePickerDialog.show(getSupportFragmentManager(), "TIME PICKER");


    }








    private boolean checkUserEnteries() {


        String profileN = "";
        String message = "";
        profileN = modeName.getText().toString().trim().toLowerCase();
        message = modeMessage.getText().toString().trim();




        if (profileN == null || profileN.matches("")) {

            alertDialogShow(getString(R.string.pleaseEnterCorrectProfile));
            return false;
        }
        if (message == null || message.matches("")) {

            alertDialogShow(getString(R.string.EnterValidMessage));
            return false;
        }





        if (selectExcept.isChecked() && ProcessData.selectedContacs.isEmpty()) {

            alertDialogShow(getString(R.string.noContactSeleted));
            return false;

        }

        if(!(selectedWeekDays.contains(true))&&weeklyMode.isChecked()){

            alertDialogShow(getString(R.string.noWeeksDaysSelected));
            return false;

        }


        if (dateTimeMode.isChecked()||weeklyMode.isChecked()) {



            if (calendarTo.before(calendarFrom)) {

                alertDialogShow(getString(R.string.SetAproptTimeInterval));
                return false;


            }

        }


       profileN= profileN+ProcessData.MODE_NAME_LOGO;
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



        long timeFrom,timeTo;
        timeFrom=calendarFrom.getTimeInMillis();
        timeTo=calendarTo.getTimeInMillis();





        if(deactivateMode.isChecked())
        {
          timeFrom=-1;
          timeTo=-1;

        }
        else if(weeklyMode.isChecked()){

           repeat="weekly";
            for(int i=0;i<selectedWeekDays.size();i++){
                if(selectedWeekDays.get(i)){

                    repeat=repeat+"="+(i+1);

                }}





        }

        String namesOfContacts[];
        String phoneOfContacts[];


        if(selectAll.isChecked()){


           namesOfContacts=null;
           phoneOfContacts=null;


        }else {


            namesOfContacts=ProcessData.returnContactNames(ProcessData.selectedContacs);
            phoneOfContacts=ProcessData.returnPhoneNumber(ProcessData.selectedContacs);




        }

       profileHistory=new ProfileHistory(modeName.getText().toString().toLowerCase()+ProcessData.MODE_NAME_LOGO, modeMessage.getText().toString(),
                -22, phoneOfContacts, namesOfContacts,
                timeFrom,timeTo);



        String oldProfile=getIntent().getStringExtra("profile");
        if(oldProfile!=null) {
            if(oldRequest!=-22)
                ProcessData.cancelSpeakingAlarm(this,oldRequest);
            ProcessData.deleteProfile(oldProfile, getApplicationContext(), ProcessData.MODE_FILE_NAMES);

        }
        ProcessData.saveProfile(profileHistory,repeat,this,ProcessData.MODE_FILE_NAMES);
        passDataBack(profileHistory.getProfileName());





    }



    public void onRadioModeClick(View view) {

        switch (view.getId()){




            case R.id.radioByWeek:
                weekSelectDialog();
                setVisibilityOfFromAndTo(View.VISIBLE);



                break;
            case R.id.radioDateTime:
                setVisibilityOfFromAndTo(View.VISIBLE);
                break;
            case R.id.radioIDeactivate:

                setVisibilityOfFromAndTo(View.GONE);
                break;

            case R.id.replyAll:
                textContatedSelected.setVisibility(View.GONE);
                break;
            case R.id.replyExcept:

               startContactActivity();
                textContatedSelected.setVisibility(View.VISIBLE);

                break;


















        }


    }


    private void startContactActivity(){





      if(takePermission(  Manifest.permission.READ_CONTACTS,ProcessData.READ_CONTACTS_PERMISSION)){



         startActivity(new Intent(this,ContactListSelector.class));



      }




    }






    public void onModeButtonsClick(View view) {

        switch (view.getId()) {

            case R.id.buttonFrom:
                conflictToFrom=true;
                if(weeklyMode.isChecked())
                    setTimeOnClick();
                else
                setDateOnClick();
                break;

            case R.id.buttonTo:
                conflictToFrom=false;
                if(weeklyMode.isChecked())
                    setTimeOnClick();
                else
                    setDateOnClick();
                break;

            case R.id.imageMicButton:
                speechToTextOnClick();
                break;

            case R.id.imageTemplateButtonx:

                onLoadTemplates();
                break;

            case R.id.textSelectedContacts:
                showSendableOnClick();
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

                    modeMessage.append(" "+listOfTempaltes.get(item));


                }
            });
            AlertDialog alert = builder.create();
            alert.show();






        }


    @Override
    public void onBackPressed() {

        try {
            if (modeMessage.getText().toString().trim().length() > 0 || modeMessage.getText().toString().length() > 0 || !ProcessData.selectedContacs.isEmpty()) {

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(getString(R.string.alert));
                alertDialog.setMessage(getString(R.string.goBackStatment));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                ProcessData.selectedContacs.clear();
                                ProcessData.calendarMain=null;
                               // Mode_Entry.super.onBackPressed();


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
              //  super.onBackPressed();
                finish();
            }

        }catch (Exception e){
           // super.onBackPressed();
            finish();
            Log.e("taliException",e.getMessage());
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




                if(shouldShowRequestPermissionRationale(permission)){
                   // ASKING_AGAIN_PERMISSION=true;


                }

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
                            startActivity(new Intent(this,ContactListSelector.class));

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

                            if(checkUserEnteries()) {
                                saveProfile();
                            }
                        }

                    }
                }
                break;



            case ProcessData.SEND_SMS_PERMISSION:

                if (permissions.length > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED)
                            alertDialogShow(getString(R.string.permissionSMSstatment));
                        else if(takePermission(Manifest.permission.RECEIVE_SMS,ProcessData.READ_SMS_PERMISSION)
                                &&takePermission(Manifest.permission.READ_PHONE_STATE,ProcessData.READ_CALLS_PERMISSION)
                                ){
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
                        else if(takePermission(Manifest.permission.SEND_SMS,ProcessData.SEND_SMS_PERMISSION)
                                &&takePermission(Manifest.permission.READ_PHONE_STATE,ProcessData.READ_CALLS_PERMISSION)
                                ) {


                            if (takePermission(Manifest.permission.READ_CALL_LOG,ProcessData.ANDROID9_READ_CALL_PERMISSION)&&checkUserEnteries())
                            {


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
                        else if(takePermission(Manifest.permission.SEND_SMS,ProcessData.SEND_SMS_PERMISSION)
                                &&takePermission(Manifest.permission.RECEIVE_SMS,ProcessData.READ_SMS_PERMISSION)
                                ) {
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
                        modeMessage.append(" " + recordMessage.get(0));


                }


                break;




        }
        super.onActivityResult(requestCode, resultCode, data);

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

        if(conflictToFrom) {

            calendarFrom.set(Calendar.YEAR, i);
            calendarFrom.set(Calendar.MONTH, i1);
            calendarFrom.set(Calendar.DAY_OF_MONTH, i2);
        }
        else {
            calendarTo.set(Calendar.YEAR, i);
            calendarTo.set(Calendar.MONTH, i1);
            calendarTo.set(Calendar.DAY_OF_MONTH, i2);

        }

        setTimeOnClick();






    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {



        if(conflictToFrom) {

            calendarFrom.set(Calendar.HOUR_OF_DAY, i);
            calendarFrom.set(Calendar.MINUTE, i1);
            calendarFrom.set(Calendar.SECOND, 0);
            calendarFrom.set(Calendar.MILLISECOND, 0);


            String time=ProcessData.convertTimeFormate(calendarFrom,this);

            if(calendarFrom.before(Calendar.getInstance())&&!weeklyMode.isChecked()){

                alertDialogShow(getString(R.string.LessThanCurrentTime));


            }
            else {


                if(weeklyMode.isChecked())
                    buttonFrom.setText(String.format("%s%s", this.getString(R.string.time), time));
                else
                buttonFrom.setText(String.format("%s%s%s", this.getString(R.string.time), time, form.format(calendarFrom.getTimeInMillis())));



            }
        }
        else {
            calendarTo.set(Calendar.HOUR_OF_DAY, i);
            calendarTo.set(Calendar.MINUTE, i1);
            calendarTo.set(Calendar.SECOND, 0);
            calendarTo.set(Calendar.MILLISECOND, 0);

            String time=ProcessData.convertTimeFormate(calendarTo,this);
            if(calendarTo.before(Calendar.getInstance())&&!weeklyMode.isChecked()){

                alertDialogShow(getString(R.string.LessThanCurrentTime));
            }
            else {

                if(weeklyMode.isChecked())
                    buttonTo.setText(String.format("%s%s", this.getString(R.string.time), time));

                    else
                buttonTo.setText(String.format("%s%s%s", this.getString(R.string.time), time, form.format(calendarTo.getTimeInMillis())));

            }

        }


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
                MoodSMS.modeProfileHistoryName=profileN;


        }catch (Exception e){

Log.e("taliException",e.getMessage());
        }

        ProcessData.selectedContacs.clear();
        setResult(RESULT_OK, intent);
        finish();



    }

    private void editActivity(){


        try {
            if (getIntent().getStringExtra("edit").equals("yes")) {


                String profileFromIntent = getIntent().getStringExtra("profile");

                final ProfileHistory pro = ProcessData.loadProfile(profileFromIntent, getApplicationContext());
                if (pro == null)
                    return;






                oldRequest=pro.getRequestCode();

                final String[] repeated = Objects.requireNonNull(ProcessData.getRepeated(profileFromIntent, getApplicationContext())).split("=");
                if (repeated[0].equals("weekly"))
                    for (int i = 1; i < repeated.length; i++) {
                        int index = Integer.parseInt(repeated[i]) - (1);
                        selectedWeekDays.add(index, true);
                        selectedWeekDays.remove(index + 1);

                    }

                if(pro.getNames()!=null&&pro.getNames().length!=0)
                    for (int i = 0; i < pro.getNames().length; i++) {

                        ProcessData.selectedContacs.add(new ContactsPersons(pro.getNames()[i], pro.getPhoneNumber()[i]));

                    }


                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        switch (repeated[0]) {

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

                        }


                        modeName.setText(pro.getProfileName().replaceAll(ProcessData.MODE_NAME_LOGO,"").toUpperCase());

                        modeMessage.setText(pro.getMessage());

                    }
                });


            }


        }catch (Exception e){


            Log.e("taliException",e.getMessage());

        }
    }



}


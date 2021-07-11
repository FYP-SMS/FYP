package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class SpeakActivity extends AppCompatActivity implements RecognitionListener,TextToSpeech.OnInitListener {


    private boolean status=false;


    private TextToSpeech mTTS;
   private TextView speakText,replyText;
    private SpeechRecognizer speech = null;
    private AudioManager mobilemode=null;



    private Intent recognizerIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak);





       if(ProcessData.loadSettingsData("silentdatamode",this)==0)
           mobilemode = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);




        speakText=findViewById(R.id.textSpeak);
        replyText=findViewById(R.id.textReplySpeak);
        mTTS = new TextToSpeech(this, this);



        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,getString(R.string.LanguageForCountrySpecifc));

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,700);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);






    }




    private void askQuestion(String question){



        mTTS.setPitch(1F);
        mTTS.setSpeechRate(1F);



        mTTS.speak(question, TextToSpeech.QUEUE_FLUSH, null);








    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {

        speakText.setText(R.string.error);



    }

    @Override
    public void onResults(Bundle bundle) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            speakText.setTextColor(getResources().getColor(R.color.Black,null));
        }
        speakText.setTextColor(getResources().getColor(R.color.Black));


        ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if(matches!=null)

            if(matches.size()>0)
            speakText.setText(matches.get(0));
            for (String result : matches) {



                processNLP(result);
                break;


            }



    }


    private void deactiveStatus(String result) {




        String []fileNames=ProcessData.loadProfileNames(this,ProcessData.MODE_FILE_NAMES);


        if(fileNames!=null) {
            if (fileNames.length > 0) {


                for (String names : fileNames) {
                    Log.e("tali11", "1");


                    //split into arraylist and then ask


                    ProcessData.saveStatusReport(names, this, false);


                }


                if (mobilemode != null)
                    mobilemode.setStreamVolume(AudioManager.STREAM_RING, mobilemode.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                replyText.setText(R.string.deactivateManualStatus);
                askQuestion(getString(R.string.deactivateManualStatus));

            }
        }
        else {

            replyText.setText(R.string.iHaveNotSeenAnyManualMode);
            askQuestion(getString(R.string.iHaveNotSeenAnyManualMode));

        }



    }

    private void processNLP(String result) {

        result=result.toLowerCase().trim();












        if(turnOnModeStatus(result)){



            if(mobilemode!=null)
            mobilemode.setStreamVolume(AudioManager.STREAM_RING,0,0);


            return;


        }


//nulll correxct
        ArrayList<String> phrasesx=new ArrayList<>();
        ArrayList<String> phrasesMessages=new ArrayList<>();

        String []na=ProcessData.loadProfileNames(this, ProcessData.MODE_PHRASE_ON);
        String []ms=ProcessData.loadProfileNames(this, ProcessData.MODE_PHRASE_MESSAGE);

        if(na!=null)
            phrasesx.addAll(Arrays.asList(na));

        if(ms!=null)
            phrasesMessages.addAll(Arrays.asList(ms));










        phrasesx.addAll(Arrays.asList(getResources().getStringArray(R.array.phraseModeOn)));
        phrasesMessages.addAll(Arrays.asList(getResources().getStringArray(R.array.phraseMessages)));

        for(int i=0;i<phrasesx.size();i++){


            if(result.contains(phrasesx.get(i))){

                makeStatus(result,phrasesx.get(i),phrasesMessages.get(i));

                return;

            }

        }



        ArrayList<String> fileNames =new ArrayList<>();

        String []temp=ProcessData.loadProfileNames(this,ProcessData.MODE_PHRASE_OFF);
        if(temp!=null)
            fileNames.addAll(Arrays.asList(temp));
       fileNames.addAll(Arrays.asList(getResources().getStringArray(R.array.phraseModeOff)));



        for(int i=0;i<fileNames.size();i++){


            if(result.contains(fileNames.get(i).toLowerCase().trim())){

                deactiveStatus(result);

                return;

            }

        }



        replyText.setText(R.string.sorryInotUnderstandUSiad);
        askQuestion(getString(R.string.sorryInotUnderstandUSiad));








    }



    private void makeStatus(String result,String action,String message) {



        Log.e("talihoursssss",result);




        result=" "+result+" ";

        if(result.contains(getString(R.string.Hoursx))||result.contains(getString(R.string.Hour))||result.contains(getString(R.string.Minutesx))||result.contains(getString(R.string.Minute))) {



            Log.e("talix","sasasasasaszzz");



                String containsx[] = {getString(R.string.ONEx), getString(R.string.TWO), getString(R.string.THREE), getString(R.string.FOUR), getString(R.string.FIVE)};

            Log.e("talix","sasasasasascccc");

            String num[] = {"1", "2", "3", "4", "5"};





            for (int i = 0; i < containsx.length; i++) {


                result=result.replaceAll(" "+containsx[i]+" "," "+num[i]+" ");


            }

            Log.e("talix","sasasasasas");



            result=result.trim();

        }


        String proResult=result.replaceAll("[^\\d.]", "").trim();
        Log.e("tali","pro"+proResult+"abx");
        if(proResult.length()==0){


            ProcessData.saveProfile(new ProfileHistory(action+ProcessData.MODE_NAME_LOGO,message, -22,null,null,-1,-1),"no",this,ProcessData.MODE_FILE_NAMES);
            ProcessData.saveStatusReport(action+ProcessData.MODE_NAME_LOGO,this,true);
            if(mobilemode!=null)
                mobilemode.setStreamVolume(AudioManager.STREAM_RING,0,0);


            replyText.setText(R.string.okIwillTellUrContacts);
            askQuestion(getString(R.string.okIwillTellUrContacts));
            return;


        }


            boolean b1=false;
            boolean b2=false;
            boolean multipleOfTen=false;
            Calendar c=Calendar.getInstance();

            Log.e("talipro pro",proResult);

            try {

                int index=0;

                if(result.contains(getString(R.string.Hour))||result.contains(getString(R.string.Hoursx))){

                    Log.e("taliprox","dsdsd");
                    b1=true;
                    int dummyIndex=result.indexOf(proResult.charAt(index));
                    for(;index<proResult.length();){
                        try{

                            int hours=Integer.parseInt(String.valueOf(result.charAt(dummyIndex+1)));
                            dummyIndex++;
                            index++;


                        }catch (Exception e){
                            if(String.valueOf((result.charAt(dummyIndex+1))).equals("0")){
                                multipleOfTen=true;
                            }
                            break;

                        }

                    }
                    int hours= Integer.parseInt(proResult.substring(0,index+1));


                    if(multipleOfTen)
                        hours=hours*10;

                    multipleOfTen=false;
                    Log.e("talipro",hours+" hours");
                    c.set(Calendar.HOUR_OF_DAY,c.get(Calendar.HOUR_OF_DAY)+hours);


                    index++;







                }

                if(result.contains(getString(R.string.Minutesx))||result.contains(getString(R.string.Minute))){

                    int minutes=0;
                    b2=true;

                    try{

                        minutes= Integer.parseInt(proResult.substring(index,proResult.length()));


                    }catch (Exception e){

                        Log.e("taliException",e.getMessage());


                    }




                    Log.e("talipro",minutes+" min");
                    c.set(Calendar.MINUTE,c.get(Calendar.MINUTE)+minutes);




                }




                if(b1||b2){

                    setModeCalender(action,message,c);
                    return;
                }








            }catch (Exception e){


                Log.e("taliException",e.getLocalizedMessage());

            }

        replyText.setText(R.string.sorryInotUnderstandUSiad);
        askQuestion(getString(R.string.sorryInotUnderstandUSiad));


        }






    private void setModeCalender(String action,String message,Calendar c){


        int requestCode=ProcessData.getRequestCode(this);
        Log.e("talirequestcode",requestCode+" 1");
        ProcessData.saveProfile(new ProfileHistory(
                        action+ProcessData.MODE_NAME_LOGO,message, requestCode,null
                        ,null, Calendar.getInstance().getTimeInMillis(),c.getTimeInMillis()),
                "no",this,ProcessData.MODE_FILE_NAMES);
        ProcessData.saveStatusReport(action+ProcessData.MODE_NAME_LOGO,this,true);
        if(mobilemode!=null) {
            mobilemode.setStreamVolume(AudioManager.STREAM_RING, 0, 0);



            ProcessData.makeSpeakingalarm(this,c.getTimeInMillis(),requestCode);
        }

        replyText.setText(R.string.iWillInformYourContactsBetween);

        askQuestion(getString(R.string.iWillInformYourContactsBetween));



    }

    private boolean turnOnModeStatus(String result) {


        Log.e("tali11",result);
        String []fileNames=ProcessData.loadProfileNames(this,ProcessData.MODE_FILE_NAMES);

        if(fileNames!=null&&fileNames.length>0){


            for(String names:fileNames) {
                Log.e("tali11","1");
                String dName=names.replace(ProcessData.MODE_NAME_LOGO,"").trim();

                //split into arraylist and then ask

                if(result.contains(dName)){

                    Log.e("tali111",dName);
                    ProfileHistory p=ProcessData.loadProfile(names,this);
                    if(p!=null) {
                        if (p.getDate() != -1)
                            continue;

                    }


                    replyText.setText(String.format("%s: %s %s", getString(R.string.mode), dName, getString(R.string.modeIsActivatedUWillNotGetDisturbed)));
                    askQuestion(getString(R.string.mode)+": "+dName+" "+getString(R.string.modeIsActivatedUWillNotGetDisturbed));
                    ProcessData.saveStatusReport(names,this,true);
                    return true;

                }




            }
        }

        return false;

    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    public void onInit(int i) {

        if (i == TextToSpeech.SUCCESS) {
            Locale loc = new Locale (getString(R.string.LanguageForCountrySpecifc));
            int result = mTTS.setLanguage(loc);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {

                status=true;

               // mTTS.speak("Hy", TextToSpeech.QUEUE_FLUSH, null);
                Log.e("tali",""+mTTS.getDefaultVoice().getQuality());

            }
            else {


                alertDialogShow(getString(R.string.LanguageadataIsMissingOrLanguageNotSupported));

            }
        }

    }




    //for android
    public void speakButtonClick(View view) {



        if (takePermission(Manifest.permission.SEND_SMS,ProcessData.SEND_SMS_PERMISSION) &&takePermission(Manifest.permission.RECEIVE_SMS,ProcessData.SEND_SMS_PERMISSION)&&takePermission(Manifest.permission.READ_PHONE_STATE,ProcessData
        .READ_CALLS_PERMISSION)
                &&takePermission(Manifest.permission.RECORD_AUDIO,ProcessData.AUDIO_PERMISSION


        )
                &&takePermission(Manifest.permission.READ_CALL_LOG,ProcessData.ANDROID9_READ_CALL_PERMISSION)
                ) {



            startNLPrecognitionx();
        }

    }




    private void startNLPrecognitionx(){


        if(mTTS!=null) {
            if (mTTS.isSpeaking())
                mTTS.stop();
        }

            speakText.setText(R.string.Listeneing);
            replyText.setText("...");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                speakText.setTextColor(getResources().getColor(R.color.Red, null));
            }
            speakText.setTextColor(getResources().getColor(R.color.Red));

            speech.startListening(recognizerIntent);







    }



    @Override
    protected void onStop() {


        finish();

        super.onStop();
    }


    private void alertDialogShow(String alert){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(alert);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    public boolean takePermission(String permission,int requestCOde)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_DENIED)
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





                ActivityCompat.requestPermissions(this,new String[]{permission},requestCOde);
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
                        else if (takePermission(Manifest.permission.SEND_SMS, ProcessData.SEND_SMS_PERMISSION)
                                && takePermission(Manifest.permission.READ_PHONE_STATE, ProcessData.READ_CALLS_PERMISSION)
                                && takePermission(Manifest.permission.RECEIVE_SMS, ProcessData.SEND_SMS_PERMISSION)

                                && (takePermission(Manifest.permission.READ_CALL_LOG,
                                ProcessData.ANDROID9_READ_CALL_PERMISSION))

                                ) {


                            startNLPrecognitionx();

                        }


                    }
                }

                break;






            case ProcessData.SEND_SMS_PERMISSION:

                if (permissions.length > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_DENIED)
                            alertDialogShow(getString(R.string.permissionSMSstatment));
                        else if(takePermission(Manifest.permission.RECORD_AUDIO,ProcessData.AUDIO_PERMISSION)
                                &&takePermission(Manifest.permission.READ_PHONE_STATE,ProcessData.READ_CALLS_PERMISSION)
                                &&takePermission(Manifest.permission.RECEIVE_SMS,ProcessData.SEND_SMS_PERMISSION)
                                &&(takePermission(Manifest.permission.READ_CALL_LOG,
                                ProcessData.ANDROID9_READ_CALL_PERMISSION))
                                ){




                                    startNLPrecognitionx();



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
                                &&takePermission(Manifest.permission.RECORD_AUDIO,ProcessData.AUDIO_PERMISSION)
                                &&takePermission(Manifest.permission.RECEIVE_SMS,ProcessData.SEND_SMS_PERMISSION)


                            &&(takePermission(Manifest.permission.READ_CALL_LOG,
                                        ProcessData.ANDROID9_READ_CALL_PERMISSION)
                        )){

                            startNLPrecognitionx();

                        }



                    }




                }

                break;


                case ProcessData.ANDROID9_READ_CALL_PERMISSION:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED) {
                            if(takePermission(Manifest.permission.SEND_SMS,ProcessData.SEND_SMS_PERMISSION)
                                    &&takePermission(Manifest.permission.RECORD_AUDIO,ProcessData.AUDIO_PERMISSION)
                                    &&takePermission(Manifest.permission.RECEIVE_SMS,ProcessData.SEND_SMS_PERMISSION)
                                    &&takePermission(Manifest.permission.READ_PHONE_STATE,ProcessData.READ_CALLS_PERMISSION)
                                    )

                            startNLPrecognitionx();
                        }
                    }

                    break;


        }








   }

}

package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class PhraseOff extends AppCompatActivity {



    private Context context;
    private ListView listView;
    private ArrayList<String> listOfPhrases=new ArrayList<>();
    private ArrayAdapter<String> itemsAdapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase_off);
        context=this;
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setTitle(getString(R.string.addPhrase));
        }catch (Exception e){
            Log.e("taliException",e.getLocalizedMessage());
        }
        listView=findViewById(R.id.phraseOffList);

        findViewById(R.id.fabPhraseOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(listOfPhrases!=null&&listOfPhrases.size()>0)
                    showDialog(null,-1);

            }
        });

        laodTempaltes();
    }



    private void showDialog(final String message, final int i){

        View viewd= LayoutInflater.from(context).inflate(R.layout.edittext_dialog_template,null);
        final EditText editTemplates=viewd.findViewById(R.id.editTemplate);
        editTemplates.setHint(R.string.phraseToTurnOffAllModes);
        if(message!=null||i==-1)
            editTemplates.setText(message);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(R.string.addPhraseToOffVoice);
        alertDialogBuilder.setView(viewd);
        alertDialogBuilder

                .setPositiveButton(R.string.add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                String tem=editTemplates.getText().toString().trim();
                                if(tem.length()>0){

                                    if(message==null)
                                        listOfPhrases.add(tem);
                                    else {


                                        listOfPhrases.add(i,tem);
                                        listOfPhrases.remove(i+1);
                                        ProcessData.removeNameOfProfile(message,context,ProcessData.MODE_PHRASE_OFF);

                                    }



                                    ProcessData.saveProfileName(tem,context,ProcessData.MODE_PHRASE_OFF);


                                    if(itemsAdapter!=null)
                                        itemsAdapter.notifyDataSetChanged();
                                    dialog.dismiss();

                                }




                            }
                        })
                .setNegativeButton(context.getString(R.string.cancelOption),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();




    }


    private void laodTempaltes(){

        String[] mama=context.getResources().getStringArray(R.array.phraseModeOff);

        listOfPhrases.addAll(Arrays.asList(mama));
        String []temp=ProcessData.loadProfileNames(context,ProcessData.MODE_PHRASE_OFF);



        if(temp!=null&&temp.length>0) {


            listOfPhrases.addAll(Arrays.asList(temp));
        }

        itemsAdapter =
                new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, listOfPhrases);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {


                if(i<3){

                    Toast.makeText(context, getString(R.string.cannotModifyThisPhrase),Toast.LENGTH_SHORT).show();
                }
                else {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle(context.getString(R.string.alert));
                    alertDialogBuilder.setMessage(R.string.whatDoUWant);
                    alertDialogBuilder

                            .setPositiveButton(R.string.edit,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {



                                            dialog.dismiss();
                                            showDialog(listOfPhrases.get(i),i);




                                        }
                                    })
                            .setNegativeButton(context.getString(R.string.deleteOption),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {

                                            ProcessData.removeNameOfProfile(listOfPhrases.remove(i),context,ProcessData.MODE_PHRASE_OFF);
                                            if(itemsAdapter!=null)
                                                itemsAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        }
                                    }).setNeutralButton(context.getString(R.string.cancelOption),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {



                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                }}
        });



















    }

}

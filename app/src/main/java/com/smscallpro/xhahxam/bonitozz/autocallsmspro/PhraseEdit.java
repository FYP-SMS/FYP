package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.Context;
import android.content.DialogInterface;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class PhraseEdit extends AppCompatActivity {

  private   RecyclerView recyclerView;
  private PhaseEditRecyclerView phaseEditRecyclerView;
  private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase_edit);


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle(R.string.addPhrase);
        }catch (Exception e){
            Log.e("taliException",e.getLocalizedMessage());
        }

        context=this;

        recyclerView=findViewById(R.id.resPhrase);


         phaseEditRecyclerView =new PhaseEditRecyclerView(this);

        recyclerView.setAdapter(phaseEditRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        (findViewById(R.id.fabPhrase)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View editTexts= LayoutInflater.from(context).inflate(R.layout.add_phrase_message,null);
                final EditText phraseName=editTexts.findViewById(R.id.editPhrase);
                final EditText phraseMessage=editTexts.findViewById(R.id.editPhraseMessage);













                AlertDialog.Builder alertDialogBuilder;
                AlertDialog alertDialog;

                alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle(getString(R.string.addPhraseToMakeMode));
                alertDialogBuilder.setView(editTexts);
                alertDialogBuilder

                        .setPositiveButton(getString(R.string.save),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {


                                   String name=phraseName.getText().toString().trim().toLowerCase();
                                   String message=phraseMessage.getText().toString().trim();

                                   if(name.length()>0&&message.length()>0)
                                   {
                                         ProcessData.saveProfileName(name,context,ProcessData.MODE_PHRASE_ON);

                                       ProcessData.saveProfileName(message,context,ProcessData.MODE_PHRASE_MESSAGE);
                                       phaseEditRecyclerView.filterListner(name,message);


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

            }
        });

    }
}

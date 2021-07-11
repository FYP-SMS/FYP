package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class PhaseEditRecyclerView extends RecyclerView.Adapter<PhaseEditRecyclerView.PhraseViewHolder> {

    private Context context;
    private ArrayList<String> phrasesx=new ArrayList<>();
    private ArrayList<String> phrasesMessages=new ArrayList<>();

     PhaseEditRecyclerView(Context context) {
        this.context = context;


        phrasesx.addAll(Arrays.asList(context.getResources().getStringArray(R.array.phraseModeOn)));
        phrasesMessages.addAll(Arrays.asList(context.getResources().getStringArray(R.array.phraseMessages)));
        String []na=ProcessData.loadProfileNames(context, ProcessData.MODE_PHRASE_ON);
        String []ms=ProcessData.loadProfileNames(context, ProcessData.MODE_PHRASE_MESSAGE);

        if(na!=null)
        phrasesx.addAll(Arrays.asList(na));

        if(ms!=null)
        phrasesMessages.addAll(Arrays.asList(ms));

    }

    @NonNull
    @Override
    public PhraseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PhaseEditRecyclerView.PhraseViewHolder(LayoutInflater.from(context).inflate(R.layout.voice_settings,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhraseViewHolder phraseViewHolder, int i) {

        try {
            phraseViewHolder.message.setText(phrasesMessages.get(i));
            phraseViewHolder.phrase.setText(phrasesx.get(i).toUpperCase());
        }catch (Exception e){


            Log.e("taliException",e.getLocalizedMessage());
        }
    }

    @Override
    public int getItemCount() {
        return phrasesx.size();
    }

    class PhraseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView phrase,message;

         PhraseViewHolder(@NonNull View itemView) {
            super(itemView);
            phrase=itemView.findViewById(R.id.textViewPhrase);
            message=itemView.findViewById(R.id.textViewPhraseMessage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
          final int position=getAdapterPosition();

          if(position>2){




              AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
              alertDialogBuilder.setTitle(R.string.template);
              alertDialogBuilder.setMessage(R.string.whatDoUWant);
              alertDialogBuilder

                      .setPositiveButton(R.string.edit,
                              new DialogInterface.OnClickListener() {
                                  public void onClick(DialogInterface dialog,
                                                      int id) {



                                      dialog.dismiss();
                                      showDialog(position);




                                  }
                              })
                      .setNegativeButton(context.getString(R.string.deleteOption),
                              new DialogInterface.OnClickListener() {
                                  public void onClick(DialogInterface dialog,
                                                      int id) {

                                      ProcessData.removeNameOfProfile(phrasesx.remove(position),context,ProcessData.MODE_PHRASE_ON);
                                      ProcessData.removeNameOfProfile(phrasesMessages.remove(position),context,ProcessData.MODE_PHRASE_MESSAGE);

                                          notifyDataSetChanged();

                                  }
                              }).setNeutralButton(context.getString(R.string.cancelOption),
                      new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog,
                                              int id) {



                          }
                      });
              AlertDialog alertDialog = alertDialogBuilder.create();
              alertDialog.show();










          }else {


              Toast.makeText(context, R.string.cannotModifyThisPhrase,Toast.LENGTH_SHORT).show();
          }







        }



        private void showDialog(final int position){




            View editTexts= LayoutInflater.from(context).inflate(R.layout.add_phrase_message,null);
            final EditText phraseName=editTexts.findViewById(R.id.editPhrase);
            final EditText phraseMessage=editTexts.findViewById(R.id.editPhraseMessage);



phraseName.setText(phrasesx.get(position));
phraseMessage.setText(phrasesMessages.get(position));









            AlertDialog.Builder alertDialogBuilder;
            AlertDialog alertDialog;

            alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(R.string.addPhraseToMakeMode);
            alertDialogBuilder.setView(editTexts);
            alertDialogBuilder

                    .setPositiveButton(R.string.save,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {


                                    String name=phraseName.getText().toString().trim().toLowerCase();
                                    String message=phraseMessage.getText().toString().trim();

                                    if(name.length()>0&&message.length()>0)
                                    {
                                        ProcessData.removeNameOfProfile(phrasesx.remove(position),context,ProcessData.MODE_PHRASE_ON);
                                        ProcessData.removeNameOfProfile(phrasesMessages.remove(position),context,ProcessData.MODE_PHRASE_MESSAGE);

                                        ProcessData.saveProfileName(name,context,ProcessData.MODE_PHRASE_ON);

                                        ProcessData.saveProfileName(message,context,ProcessData.MODE_PHRASE_MESSAGE);

                                        phrasesx.add(name);
                                        phrasesMessages.add(message);
                                        notifyDataSetChanged();



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

    }


    public void filterListner(String name,String message){

        phrasesx.add(name);
        phrasesMessages.add(message);
        notifyDataSetChanged();



    }
}

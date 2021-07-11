package com.smscallpro.xhahxam.bonitozz.autocallsmspro;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TemplateList extends Fragment {


    List<String> listOfTempaltes;
 private    View view;
    private ListView listView;
    private  ArrayAdapter<String> itemsAdapter=null;

    private Context context;
    public TemplateList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_template_list, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        context=getActivity();

        listView=view.findViewById(R.id.listTemplateList);

        view.findViewById(R.id.templatesfab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(listOfTempaltes!=null&&listOfTempaltes.size()>0)
                showDialog(null,-1);

            }
        });

        laodTempaltes();







    }

    private void showDialog(final String message, final int i){

        View viewd= LayoutInflater.from(context).inflate(R.layout.edittext_dialog_template,null);
        final EditText editTemplates=viewd.findViewById(R.id.editTemplate);
        if(message!=null||i==-1)
            editTemplates.setText(message);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(R.string.addTemplate);
        alertDialogBuilder.setView(viewd);
        alertDialogBuilder

                .setPositiveButton(R.string.add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                String tem=editTemplates.getText().toString().trim();
                                if(tem.length()>0){

                                    if(message==null)
                                    listOfTempaltes.add(tem);
                                    else {


                                        listOfTempaltes.add(i,tem);
                                        listOfTempaltes.remove(i+1);
                                        ProcessData.removeNameOfProfile(message,context,ProcessData.TEMPLATES_FILE);

                                    }



                                    ProcessData.saveProfileName(tem,context,ProcessData.TEMPLATES_FILE);


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

        String[] mama=context.getResources().getStringArray(R.array.templates);
        listOfTempaltes=new ArrayList<>();
        listOfTempaltes.addAll(Arrays.asList(mama));
        String []temp=ProcessData.loadProfileNames(context,ProcessData.TEMPLATES_FILE);



        if(temp!=null&&temp.length>0) {


            listOfTempaltes.addAll(Arrays.asList(temp));
        }

       itemsAdapter =
                new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, listOfTempaltes);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {


                if(i<3){

                    Toast.makeText(context, R.string.youCannotModifyTemplate,Toast.LENGTH_SHORT).show();
                }
                else {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle(R.string.template);
                alertDialogBuilder.setMessage(R.string.whatDoUWant);
                alertDialogBuilder

                        .setPositiveButton(R.string.edit,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {



                                        dialog.dismiss();
                                        showDialog(listOfTempaltes.get(i),i);




                                    }
                                })
                        .setNegativeButton(context.getString(R.string.deleteOption),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        ProcessData.removeNameOfProfile(listOfTempaltes.remove(i),context,ProcessData.TEMPLATES_FILE);
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

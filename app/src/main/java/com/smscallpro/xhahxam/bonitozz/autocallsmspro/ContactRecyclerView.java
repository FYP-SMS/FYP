package com.smscallpro.xhahxam.bonitozz.autocallsmspro;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;


import static com.smscallpro.xhahxam.bonitozz.autocallsmspro.ProcessData.selectedContacs;


public class ContactRecyclerView extends RecyclerView.Adapter<ContactRecyclerView.MyViewHolder> {


    private Context context;
    private ArrayList<ContactsPersons> contactsPerson;




    ContactRecyclerView(Context context,ArrayList<ContactsPersons> contactsPersons) {

        this.contactsPerson=contactsPersons;


        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {




        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_list_rescyclerview,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int ii) {



        final int i=ii;
        myViewHolder.name.setText(contactsPerson.get(i).getNameContact());
        myViewHolder.phonenumber.setText(contactsPerson.get(i).getPhoneContact());
        if(contactsPerson.get(i).isChecked()){       //hjh
            myViewHolder.checkBox.setChecked(true);
        }
        else
            myViewHolder.checkBox.setChecked(false);



        myViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {



                        if(myViewHolder.checkBox.isChecked()){



                            contactsPerson.get(i).setChecked(true);//gjghj
                            selectedContacs.add(contactsPerson.get(i));

                        }
                        else {

                            contactsPerson.get(i).setChecked(false);//ghghg
                            selectedContacs.remove(contactsPerson.get(i));

                        }
                    }
                }).start();

            }



        });















    }

    @Override
    public int getItemCount()
    {

        return    contactsPerson.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, phonenumber;
        CheckBox checkBox;


         MyViewHolder(@NonNull View itemView) {


            super(itemView);

            name = itemView.findViewById(R.id.contactname);
            phonenumber = itemView.findViewById(R.id.contactphone);
            checkBox = itemView.findViewById(R.id.checkBox);


        }

    }






    public void setFilter(ArrayList<ContactsPersons> dataEntries1) {


        contactsPerson = new ArrayList<>();
        contactsPerson.addAll(dataEntries1);
        notifyDataSetChanged();


    }


}

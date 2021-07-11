package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import static com.smscallpro.xhahxam.bonitozz.autocallsmspro.ProcessData.selectedContacs;

public class ContactListSelector extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Context context;


    private RecyclerView recyclerView;
    private  ContactRecyclerView contactRecyclerView;
    private ProgressBar progressBar;
    private Handler handler;
    private TextView textView;
   private ArrayList<ContactsPersons> contactsPersons=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list_selector);

        handler=new Handler();
        recyclerView=findViewById(R.id.RecyclerViewContactList);
        progressBar=findViewById(R.id.ProgresBarLoadContacts);

        textView=findViewById(R.id.loadtextview);
        Toolbar toolbar = findViewById(R.id.toolbarx);

        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle(R.string.contacts);
        }catch (Exception e){
            Log.e("taliException",e.getLocalizedMessage());
        }




        context=this;
        LoadRecyclerViewContacts();
    }


    public void LoadRecyclerViewContacts() {



        new Thread(new Runnable() {
            @Override
            public void run() {


                try {

                    if (contactsPersons.isEmpty()) {

                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

                        while (phones.moveToNext()) {
                            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactsPersons.add(new ContactsPersons(name.trim(), phoneNumber.trim()));



                        }
                        phones.close();


                        Collections.sort(contactsPersons, new Comparator<ContactsPersons>() {
                            @Override
                            public int compare(ContactsPersons t1, ContactsPersons t2) {

                                return t1.getNameContact().toLowerCase().compareTo(t2.getNameContact().toLowerCase());

                            }
                        });


                        handler.post(new Runnable() {
                            @Override
                            public void run() {


                                progressBar.setVisibility(View.GONE);
                                textView.setVisibility(View.GONE);

                                contactRecyclerView = new ContactRecyclerView(context, contactsPersons);
                                recyclerView.setItemViewCacheSize(contactsPersons.size() + 10);

                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
                                recyclerView.setAdapter(contactRecyclerView);



                            }
                        });









                    }
                } catch (Exception e) {


                    Log.e("taliException",e.getMessage());
                }
            }
        }).start();









    }

    @Override
    public void onBackPressed() {


        recyclerView.stopScroll();
        recyclerView.getRecycledViewPool().clear();
        Runtime.getRuntime().gc();
        recyclerView.setAdapter(null);
        recyclerView=null;
        super.onBackPressed();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_items,menu);
        MenuItem menuItem=menu.findItem(R.id.searchitemx);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);

        return  true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menuSelectAll:

                selectAllContacts();
                break;


            case R.id.menuSelectNon:
               unSelectAllContacts();
                break;


        }


        return super.onOptionsItemSelected(item);
    }



    private  void selectAllContacts(){


            new Thread(new Runnable() {
                @Override
                public void run() {


                    selectedContacs.clear();
                    selectedContacs.addAll(contactsPersons);
                    for (int i = 0; i < contactsPersons.size(); i++) {
                        contactsPersons.get(i).setChecked(true);

                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            contactRecyclerView.setFilter(contactsPersons);

                        }
                    });

                }
            }).start();




    }

    private  void unSelectAllContacts(){

        selectedContacs.clear();


            new Thread(new Runnable() {
                @Override
                public void run() {




                    for (int i = 0; i < contactsPersons.size(); i++) {
                        contactsPersons.get(i).setChecked(false);

                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            contactRecyclerView.setFilter(contactsPersons);

                        }
                    });

                }
            }).start();




    }






    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }





    @Override
    public boolean onQueryTextChange(String ss) {


        progressBar.setVisibility(View.VISIBLE);





        final String s=ss.toLowerCase();

        new Thread(new Runnable() {
            @Override
            public void run() {

                final ArrayList<ContactsPersons> newlist=new ArrayList<>();



                for(int i=0;i<contactsPersons.size();i++){
                    if(contactsPersons.get(i).getNameContact().toLowerCase().contains(s)) {

                        newlist.add(contactsPersons.remove(i));



                    }

                }



                contactsPersons.addAll(0,newlist);

                Collections.sort(contactsPersons, new Comparator<ContactsPersons>() {
                    @Override
                    public int compare(ContactsPersons t1, ContactsPersons t2) {

                        return t1.getNameContact().toLowerCase().compareTo(t2.getNameContact().toLowerCase());

                    }
                });





             /*  for(ContactsPersons datas:contactsPersons)
                {
                    if(datas.getNameContact().toString().toLowerCase().contains(s)){

                        newlist.add(contactsPersons.re);
                    }




                }*/

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contactRecyclerView.setFilter(newlist);
                        progressBar.setVisibility(View.GONE);
                    }
                });



            }
        }).start();






        return true;
    }


}

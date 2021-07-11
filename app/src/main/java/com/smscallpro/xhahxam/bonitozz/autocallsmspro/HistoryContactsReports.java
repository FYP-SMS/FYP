package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.Context;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class HistoryContactsReports extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryContactListView historyContactListView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_contacts_reports);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Log.e("talihistory","reports");
        context=this;

        recyclerView=findViewById(R.id.reportsGenerated);

        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setTitle(R.string.SentTo);
        }catch (Exception e){
            Log.e("taliException",e.getLocalizedMessage());
        }
        loadReports();




    }

    private void loadReports()
    {







                String historyProfile=null;

               historyProfile= getIntent().getStringExtra("profile");

                Log.e("talihistory","historyprofile:    "+historyProfile);
               if(historyProfile!=null){


              ProfileHistory profileHistory=     ProcessData.loadProfile(historyProfile,getApplicationContext());


              if(profileHistory!=null){

                  Log.e("talihistory","no null");





                  historyContactListView=new HistoryContactListView(context,profileHistory);



                          Log.e("talihistory","recycler view post");
                          recyclerView.setLayoutManager(new LinearLayoutManager(context));
                          recyclerView.setAdapter(historyContactListView);











              }







               }



            }







}

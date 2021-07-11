package com.smscallpro.xhahxam.bonitozz.autocallsmspro;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class IncomingSMS extends Fragment {

    private View view;
    private ImageView incomingImage;
    private TextView incomingText;
    private Handler handler;
    private RecyclerView recyclerView;
    private final int ADD_MODE=142;
    private IncomingRecyclerView incomingRecyclerView;
    public static String modeProfileHistoryName="";  //for editing as activity result doesnot work
    public static int modeProfileHistoryIndex=-1;


    public IncomingSMS() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_incoming_sm, container, false);
        return view;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {


handler=new Handler();

        super.onActivityCreated(savedInstanceState);

        handler=new Handler();
        incomingImage=view.findViewById(R.id.imageIncomingWait);
        incomingText=view.findViewById(R.id.textIncomingWait);
        recyclerView=view.findViewById(R.id.resIncomingView);

        view.findViewById(R.id.fabIncoming).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(new Intent(getActivity(),IncomingCallsSchedule.class),ADD_MODE);
                    }
                });

        loadProfilesHistoriesData();










    }



    private void loadProfilesHistoriesData() {







        new Thread(new Runnable() {
            ArrayList<ProfileHistory> profileHistoriesx=new ArrayList<>();
            @Override
            public void run() {


                try{

                    final String[] profilesNames=ProcessData.loadProfileNames(getContext(),ProcessData.INCOMOING_FILE_NAMES);
                    if(profilesNames!=null){





                        for (String name : profilesNames) {


                            ProfileHistory p=(ProcessData.loadProfile(name,getContext()));
                            if(p==null)
                                continue;

                            profileHistoriesx.add(p);


                        }




                    }


                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            incomingRecyclerView= new IncomingRecyclerView(getContext(),profileHistoriesx);

                            recyclerView.setHasFixedSize(true);

                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


                            recyclerView.setAdapter(incomingRecyclerView);
                            if(profileHistoriesx==null) {
                                incomingImage.setVisibility(View.VISIBLE);
                                incomingText.setVisibility(View.VISIBLE);

                            }

                              else   if (profileHistoriesx.isEmpty()) {


                                    incomingImage.setVisibility(View.VISIBLE);
                                    incomingText.setVisibility(View.VISIBLE);
                                }
                                else {

                                    incomingImage.setVisibility(View.GONE);
                                    incomingText.setVisibility(View.GONE);


                                }






                        }
                    });


                }catch (Exception e){




                    Log.e("taliException",e.getMessage());


                }}
        }).start();






    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode){

            case ADD_MODE:
                if(resultCode==RESULT_OK&&data!=null&&!(data.getStringExtra("profilename").equals("null"))){


                    incomingRecyclerView.filterAddProfile(ProcessData.loadProfile(data.getStringExtra("profilename"),getContext()),-1);
                    if(incomingImage.getVisibility()==View.VISIBLE){

                        incomingImage.setVisibility(View.GONE);
                        incomingText.setVisibility(View.GONE);
                    }




                }

                break;





        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {



        if(!modeProfileHistoryName.equals("")&&modeProfileHistoryIndex!=-1) {

            incomingRecyclerView.filterAddProfile(ProcessData.loadProfile(modeProfileHistoryName,getContext()),modeProfileHistoryIndex);
            modeProfileHistoryIndex=-1;
            modeProfileHistoryName="";
        }

        super.onStart();



    }


}

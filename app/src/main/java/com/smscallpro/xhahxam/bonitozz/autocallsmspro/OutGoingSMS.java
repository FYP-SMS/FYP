package com.smscallpro.xhahxam.bonitozz.autocallsmspro;


import android.content.Context;
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


public class OutGoingSMS extends Fragment {

    private RecyclerView recyclerView;
    private TextView description;
    private ImageView imageView;
    private Handler handler;
    public static final int ADD_ACTIVTY=121;

    public static int refrenece=-1;
    public static String profileNames="null";
    private Context context;
    private ArrayList<ProfileHistory> profileHistories;
   private   MainOutGoingRecyclerView mainOutGoingRecyclerView;

    View view;
    public OutGoingSMS() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context=getActivity();
        view= inflater.inflate(R.layout.fragment_out_going_sm, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {



        super.onActivityCreated(savedInstanceState);



         recyclerView=view.findViewById(R.id.resOutGoingView);
         handler=new Handler();
         description=view.findViewById(R.id.textNewShow);
         imageView=view.findViewById(R.id.imageopaque);


       profileHistories=new ArrayList<>();

        loadProfilesHistoriesData();

       view.findViewById(R.id.fab)


       .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i=new Intent(context,AddOutGoingSms.class);
                i.putExtra("edit","no");


                startActivityForResult(i,ADD_ACTIVTY);

            }
        });







    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode){

            case ADD_ACTIVTY:
                if(resultCode==RESULT_OK&&data!=null&&!(data.getStringExtra("profilename").equals("null"))){


                    mainOutGoingRecyclerView.outGoingResFilter(ProcessData.loadProfile(data.getStringExtra("profilename"),context));
                    if(description.getVisibility()==View.VISIBLE){

                        description.setVisibility(View.GONE);
                        imageView.setVisibility(View.GONE);
                    }




                }

                break;





        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadProfilesHistoriesData() {







            new Thread(new Runnable() {
                @Override
                public void run() {

                    try{

                        final String[] profilesNames=ProcessData.loadProfileNames(context,ProcessData.OUTGOING_FILE_NAMES);
                        if(profilesNames!=null){









                            for (String name : profilesNames) {


                                ProfileHistory p=(ProcessData.loadProfile(name,context));
                                if(p==null)
                                  continue;

                                profileHistories.add(p);


                            }




                        }


                        handler.post(new Runnable() {
                            @Override
                            public void run() {


                                    mainOutGoingRecyclerView=new MainOutGoingRecyclerView(context,profileHistories);


                                recyclerView.setLayoutManager(new LinearLayoutManager(context));


                                recyclerView.setAdapter(mainOutGoingRecyclerView);
                                if(profileHistories!=null) {
                                    if (profileHistories.isEmpty()) {


                                        description.setVisibility(View.VISIBLE);
                                        imageView.setVisibility(View.VISIBLE);
                                    }
                                    else {

                                        description.setVisibility(View.GONE);
                                        imageView.setVisibility(View.GONE);


                                    }
                                }





                            }
                        });


                    }catch (Exception e){




                        Log.e("taliException",e.getMessage());


                    }}
            }).start();






    }




    @Override
    public void onStart() {

        profileHistories.clear();

        if(!profileNames.equals("null")&&refrenece!=-1) {

            mainOutGoingRecyclerView.outGoingResFilter(ProcessData.loadProfile(profileNames, context), refrenece);
        }

        super.onStart();



    }


}

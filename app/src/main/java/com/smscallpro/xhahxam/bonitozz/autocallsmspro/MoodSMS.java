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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


public class MoodSMS extends Fragment {


    private View view;
    private Context context;
    private ImageView ModesImage;
    private TextView ModesText;
    private RecyclerView recyclerView;
    private ModeRescyclerVIew modeRescyclerVIew;
    private final int ADD_MODE=142;
    private Handler handler;
    public static String modeProfileHistoryName="";  //for editing as activity result doesnot work
    public static int modeProfileHistoryIndex=-1;
    public MoodSMS() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_mood_sm, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {




        super.onActivityCreated(savedInstanceState);

handler=new Handler();
        recyclerView=view.findViewById(R.id.resModeView);
        ModesImage=view.findViewById(R.id.imageModeWait);
        ModesText=view.findViewById(R.id.textModeWait);
        context=getActivity();
        view.findViewById(R.id.fabMode).
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(),Mode_Entry.class),ADD_MODE);
            }
        });

        loadProfilesHistoriesData();










    }



    private void loadProfilesHistoriesData() {







        new Thread(new Runnable() {

            @Override
            public void run() {

                final ArrayList<ProfileHistory> profileHistoriesx=new ArrayList<>();
                try{

                    final String[] profilesNames=ProcessData.loadProfileNames(context,ProcessData.MODE_FILE_NAMES);
                    if(profilesNames!=null){


                        Calendar calendar=Calendar.getInstance();


                        for (String name : profilesNames) {


                            ProfileHistory p=(ProcessData.loadProfile(name,context));
                            if(p==null)
                                continue;

                            String repeats = ProcessData.getRepeated(name, context);
                            if(repeats.contains("no")&&p.getSavingDate()!=-1) {



                                    calendar.setTimeInMillis(p.getSavingDate());
                                    if (calendar.before(Calendar.getInstance())) {

                                        ProcessData.deleteProfile(p.getProfileName(), context, ProcessData.MODE_FILE_NAMES);
                                        continue;


                                    }

                            }



                                profileHistoriesx.add(p);


                        }




                    }


                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            modeRescyclerVIew= new ModeRescyclerVIew(context,profileHistoriesx);

                            recyclerView.setHasFixedSize(true);

                            recyclerView.setLayoutManager(new LinearLayoutManager(context));


                            recyclerView.setAdapter(modeRescyclerVIew);
                            if(profileHistoriesx==null) {

                                ModesImage.setVisibility(View.VISIBLE);
                                ModesText.setVisibility(View.VISIBLE);
                            }
                                else if (profileHistoriesx.isEmpty()) {


                                    ModesImage.setVisibility(View.VISIBLE);
                                    ModesText.setVisibility(View.VISIBLE);
                                }
                                else {

                                    ModesImage.setVisibility(View.GONE);
                                    ModesText.setVisibility(View.GONE);


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


                    modeRescyclerVIew.filterAddProfile(ProcessData.loadProfile(data.getStringExtra("profilename"),context),-1);
                    if(ModesImage.getVisibility()==View.VISIBLE){

                        ModesImage.setVisibility(View.GONE);
                        ModesText.setVisibility(View.GONE);
                    }




                }

                break;





        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {



        if(!modeProfileHistoryName.equals("")&&modeProfileHistoryIndex!=-1) {

            modeRescyclerVIew.filterAddProfile(ProcessData.loadProfile(modeProfileHistoryName,context),modeProfileHistoryIndex);
            modeProfileHistoryIndex=-1;
            modeProfileHistoryName="";
        }

        super.onStart();



    }




}

package com.smscallpro.xhahxam.bonitozz.autocallsmspro;



import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class History extends Fragment {

private RecyclerView recyclerView;
private HistroyResAdapter histroyResAdapter;
private ImageView histroyImage;
private TextView historyText;
private Context context;
private Handler handler;
   private View view;
    public History() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler=new Handler();
        recyclerView=view.findViewById(R.id.historyResView);
        histroyImage=view.findViewById(R.id.imageHistoryWait);
        historyText=view.findViewById(R.id.textHistoryWait);
        context=getContext();


        new Thread(new Runnable() {
            @Override
            public void run() {
                loadHistory();
            }
        }).start();












        view.findViewById(R.id.fabHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(context.getString(R.string.alert));
                alertDialog.setMessage(getString(R.string.DoUClearHistory));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String fileNames[]= ProcessData.loadProfileNames(context,ProcessData.HISTORY_FILE_NAMES);
                                if(fileNames!=null&&fileNames.length>0){


                                    for(String f:fileNames){


                                        ProfileHistory p= ProcessData.loadProfile(f,context);
                                        if(p!=null){


                                            ProcessData.deleteProfile(p.getProfileName(),context,ProcessData.HISTORY_FILE_NAMES);


                                        }




                                    }


                                    histroyResAdapter.historyRefresh();
                                    historyText.setVisibility(View.VISIBLE);
                                    histroyImage.setVisibility(View.VISIBLE);


                                }





                                dialog.dismiss();



                            }
                        });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();

            }
        });












    }


    private void loadHistory(){



        ArrayList<ProfileHistory> profileHistories=new ArrayList<>();
      String[] names=  ProcessData.loadProfileNames(context,ProcessData.HISTORY_FILE_NAMES);

      if(names==null)
          return;
      if(names.length<1)
          return;



      for(int i=0;i<names.length;i++){


          ProfileHistory profileHistory =ProcessData.loadProfile(names[i],context);
          if(profileHistory==null)
              continue;

          Log.e("tali121",profileHistory.getProfileName());
          profileHistories.add(profileHistory);




      }


      if(profileHistories.isEmpty())
          return;



      histroyResAdapter=new HistroyResAdapter(context,profileHistories);
      handler.post(new Runnable() {
          @Override
          public void run() {

              historyText.setVisibility(View.GONE);
              histroyImage.setVisibility(View.GONE);
              recyclerView.setLayoutManager(new LinearLayoutManager(context));
              recyclerView.setAdapter(histroyResAdapter);

          }
      });










    }
}

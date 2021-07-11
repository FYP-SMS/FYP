package com.smscallpro.xhahxam.bonitozz.autocallsmspro;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class InComingCalls extends Fragment {


   private View view;
   private TextView mailUs;

    public InComingCalls() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_in_coming_calls, container, false);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {



        super.onActivityCreated(savedInstanceState);


        mailUs=view.findViewById(R.id.textbbbb);

        mailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uriText =
                        "mailto:bonitoz.inc@gmail.com";

                Uri uri = Uri.parse(uriText);

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(uri);

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

                    startActivity(Intent.createChooser(intent, getString(R.string.send_email_now)));
                }
            }
        });

    }







}

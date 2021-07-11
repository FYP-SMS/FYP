package com.smscallpro.xhahxam.bonitozz.autocallsmspro;


import android.content.DialogInterface;
import android.content.Intent;

import android.content.res.Configuration;
import android.net.Uri;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.Objects;

public class SmsSedular extends AppCompatActivity {

   private DrawerLayout drawerLayout;



    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_sedular);



        initializeUi();
        setUpNavigationView(navigationView);



        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        onNewIntent(getIntent());







    }

    @Override
    public void onNewIntent(Intent intent){
        boolean   tabNumber =getIntent().getBooleanExtra("history",false);
        String speak=getIntent().getStringExtra("speak");
        if(speak==null)
            speak=" ";


        if(tabNumber) {
            Log.e("TEMP","dsds");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new History()).commit();
            navigationView.setCheckedItem(R.id.nav_history);
            setTitle(getString(R.string.histroy));
        }
        else if(speak.equals("xxx")){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new InComingCalls()).commit();
            navigationView.setCheckedItem(R.id.nav_help);
            setTitle(getString(R.string.help));


        }

        else {
            FragmentManager fragmentManager = getSupportFragmentManager();



           fragmentManager.beginTransaction().replace(R.id.fragment_container, new TabFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_message);
            setTitle(getString(R.string.sms_schedular));

        }

    }

    @Override
    public void onBackPressed() {

        MenuItem menuItem=navigationView.getCheckedItem();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        else if(Objects.requireNonNull(menuItem).getItemId()!=R.id.nav_message) {


            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container,new TabFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_message);
            setTitle(getString(R.string.sms_schedular));
        }


        else {
            super.onBackPressed();
        }
    }





    public void initializeUi(){

        drawerLayout=findViewById(R.id.navigater_main);
        toolbar =  findViewById(R.id.toolbar);
       navigationView = findViewById(R.id.nav_view);

    }


    private void setUpNavigationView(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        Fragment selectedFragment = selectDrawerItem(menuItem);
                        if (selectedFragment != null){
                            FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();


                        navigationView.setCheckedItem(menuItem.getItemId());
                        setTitle(menuItem.getTitle());

                    }

                           drawerLayout.closeDrawers();


                        return true;
                    }
                });
    }

    public Fragment selectDrawerItem(MenuItem menuItem){

        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_message:
                fragment = new TabFragment();
                break;
            case R.id.nav_history:
                fragment = new History();
                break;

            case R.id.nav_Templates:
                fragment=new TemplateList();
                break;

            case R.id.nav_help:
                fragment=new InComingCalls();
                break;

            case R.id.nav_Settings:



                fragment=new Settings();


                break;


            case R.id.nav_rateus:

                try {
                    Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getBaseContext().getPackageName()));
                    startActivity(rateIntent);

                }catch (Exception e){

                    Log.e("taliException",e.getMessage());

                }
                break;

            case R.id.nav_info:
                fragment=new AboutAppFragment();
                break;



            case R.id.nav_mailus:

                alertDialogShow();


                break;





                default:

                    fragment=new TabFragment();
        }
        return fragment;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void alertDialogShow(){

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(this.getString(R.string.contact_us));
        alertDialog.setMessage(getString(R.string.emailRequestDialog));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.send_email_now),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String uriText =
                                "mailto:bonitoz.inc@gmail.com";

                        Uri uri = Uri.parse(uriText);

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(uri);

                        if (intent.resolveActivity(getPackageManager()) != null) {

                            startActivity(Intent.createChooser(intent, getString(R.string.send_email_now)));
                        }

                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancelOption),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }



}


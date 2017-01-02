package com.example.prekshasingla.homegenie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  {

    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter viewPagerAdapter;
    TextView tv_NoInternet;
    int PLACE_PICKER_REQUEST = 1;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_NoInternet = (TextView) findViewById(R.id.no_internet);

        if (!CheckNetwork.isInternetAvailable(this)) //returns true if internet available
        {
            tv_NoInternet.setVisibility(View.VISIBLE);
        }

        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new MasterControlFragment(),"Master Control");
        viewPagerAdapter.addFragment(new PresetTimeFragment(),"Preset Time");
        viewPagerAdapter.addFragment(new Weather(),"Weather");

        viewPager =(ViewPager)findViewById(R.id.viewpager);
        tabLayout=(TabLayout)findViewById(R.id.tablayout);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if(id == R.id.action_choose_place)
        {

            try {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            }
            catch (Exception e){}

        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                String pincode=null;
                LatLng latLng=place.getLatLng();
                double latitude=latLng.latitude;
                double longitude=latLng.longitude;

                    try {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                       if(!addresses.isEmpty()) {
                           Address add = addresses.get(0);
                           pincode = add.getPostalCode();


                    if (pincode != null) {
                        Toast.makeText(this, pincode, Toast.LENGTH_LONG).show();
                        prefs = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("location", pincode);
                        editor.commit();
                    } else {
                        Toast.makeText(this, "Pincode not found, Please retry", Toast.LENGTH_LONG).show();
                    }
                       }
                       else{
                           Toast.makeText(this, "Location not available", Toast.LENGTH_LONG).show();
                       }

                    } catch (IOException e) {
                    }

            }
        }
    }

}

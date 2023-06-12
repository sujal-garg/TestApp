package com.example.QAapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.QAapp.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomNavigateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomNavigateFragment extends Fragment {

    public BottomNavigateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomNavigateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomNavigateFragment newInstance(String param1, String param2) {
        BottomNavigateFragment fragment = new BottomNavigateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 111);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_navigate, container, false);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);
        bottomNavigationView.setItemIconTintList(null);

        if (getArguments().get(Constants.ActivityName) == Constants.MainActi) {
            bottomNavigationView.setSelectedItemId(R.id.buttonPage);
        }

        return view;
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            Fragment supportFragment = null;
            switch (item.getItemId()) {
                case R.id.webpage:
                    supportFragment = new BrowserFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
                            .addToBackStack("browser").commit();
                    break;
                case R.id.buttonPage:
                    supportFragment = new ButtonViewFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
                            .addToBackStack("button").commit();
                    break;
                case R.id.hybridView:
                    supportFragment = new HybridViewFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
                            .addToBackStack("hybrid").commit();
                    break;
                case R.id.GPS:
                    supportFragment = new LocationFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder,supportFragment)
                            .addToBackStack("gps").commit();
                    break;
                case R.id.google:
                    supportFragment = new GoogleSignInFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, supportFragment)
                            .addToBackStack("google").commit();
            }
            return true;
        }
    };



}
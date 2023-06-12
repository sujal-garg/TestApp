package com.example.QAapp.utils;

import static com.example.QAapp.utils.Constants.ActivityName;
import static com.example.QAapp.utils.Constants.MainActi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.QAapp.BottomNavigateFragment;

public class CommonMethods {

    public static Fragment getBottomNavBundleFragment(String activityName){
        Bundle bundle = new Bundle();
        bundle.putString(ActivityName,activityName);

        BottomNavigateFragment bottomNavigateFragment = new BottomNavigateFragment();
        bottomNavigateFragment.setArguments(bundle);

        return bottomNavigateFragment;
    }

}

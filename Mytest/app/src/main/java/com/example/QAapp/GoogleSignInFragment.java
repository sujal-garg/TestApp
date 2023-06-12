package com.example.QAapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoogleSignInFragment#} factory method to
 * create an instance of this fragment.
 */
public class GoogleSignInFragment extends Fragment {

    public GoogleSignInFragment() {
        // Required empty public constructor
    }

//    // TODO: Rename and change types and number of parameters
//    public static GoogleSignIn newInstance(String param1, String param2) {
//        Bundle args = new Bundle();
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient =
                GoogleSignIn.getClient(getActivity(), gso);
        startActivityForResult(mGoogleSignInClient.getSignInIntent(),200);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

        View v = inflater.inflate(R.layout.fragment_google_sign_in, container, false);
        return v;
    }

}
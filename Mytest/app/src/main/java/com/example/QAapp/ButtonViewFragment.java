package com.example.QAapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ButtonViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonViewFragment extends Fragment {

    Location gpsLocation;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    public ButtonViewFragment(){

    }

    public ButtonViewFragment(Location gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ButtonViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ButtonViewFragment newInstance(String param1, String param2) {
        ButtonViewFragment fragment = new ButtonViewFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_button_view, container, false);

        Button b = view.findViewById(R.id.click);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    TextView t = getView().findViewById(R.id.textChanger);

                    t.setText("CLICK BUTTON! is Clicked");
                }catch (Exception e){
                    EditText editText = view.findViewById(R.id.plain_text_input);
                    try {
                        editText.setText(getActivity().getIntent().getBundleExtra("DEVICE_UDID").toString());
                    }catch (Exception exception){
                        editText.setText("123123");
                    }
                }
            }
        });

        view.findViewById(R.id.Tap).setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector =
                    new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            TextView t = view.findViewById(R.id.textChanger);
                            t.setText("TAP BUTTON! is Clicked");
                            return true;
                        }
                    });
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
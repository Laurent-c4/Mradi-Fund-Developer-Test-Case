package com.example.mradifundmobiledevelopertestcase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.mradifundmobiledevelopertestcase.MainActivity.fab;

public class FirstFragment extends Fragment {
    private CardView summaryCardView;
    private CardView barGraphCardView;
    private CardView lineGraphCardView;
    private CardView IDNumberCardView;
    private CardView historyCardView;
    private TextView welcomeTextView;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String displayName = user.getDisplayName();

//        ll = rootView.findViewById(R.id.ll);
//
//        YoYo.with(Techniques.ZoomInLeft)
//                .duration(300)
//                .repeat(0)
//                .playOn(ll);


        welcomeTextView = rootView.findViewById(R.id.welcomeTextView);
        welcomeTextView.setText(String.format("Welcome, %s.\nUpload your MPESA Statement to analyze your expenses.", displayName));

        summaryCardView = rootView.findViewById(R.id.viewSummaryCardView);
        summaryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.uploadedFilename!=null){
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_firstFragment_to_pieChartFragment);
                } else {
                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .repeat(2)
                            .playOn(fab);
                    Toast.makeText(getContext(),"First upload MPESA Statement using the button at the bottom of your screen or from History",Toast.LENGTH_LONG).show();
                }
            }
        });

        barGraphCardView = rootView.findViewById(R.id.barGraphCardView);
        barGraphCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.uploadedFilename!=null){
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_firstFragment_to_barGraphFragment);
                } else {
                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .repeat(2)
                            .playOn(fab);
                    Toast.makeText(getContext(),"First upload MPESA Statement using the button at the bottom of your screen or from History",Toast.LENGTH_LONG).show();
                }


            }
        });

        lineGraphCardView = rootView.findViewById(R.id.lineGraphCardView);
        lineGraphCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.uploadedFilename!=null){
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_firstFragment_to_lineGraphFragment);
                } else {
                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .repeat(2)
                            .playOn(fab);
                    Toast.makeText(getContext(),"First upload MPESA Statement using the button at the bottom of your screen or from History",Toast.LENGTH_LONG).show();
                }
            }
        });

        IDNumberCardView = rootView.findViewById(R.id.IDNumberCardView);
        IDNumberCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_firstFragment_to_IDNumberFragment);
            }
        });

        historyCardView = rootView.findViewById(R.id.historyCardView);
        historyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_firstFragment_to_historyFragment);
            }
        });
        return rootView;


    }

}

package com.example.mradifundmobiledevelopertestcase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        welcomeTextView = rootView.findViewById(R.id.welcomeTextView);
        welcomeTextView.setText(displayName);

        summaryCardView = rootView.findViewById(R.id.viewSummaryCardView);
        summaryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.mPDFUri!=null){
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_firstFragment_to_pieChartFragment);
                } else {
                    Toast.makeText(getContext(),"First upload a PDF using the button at the bottom of your screen",Toast.LENGTH_SHORT).show();
                }
            }
        });

        barGraphCardView = rootView.findViewById(R.id.barGraphCardView);
        barGraphCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.mPDFUri!=null){
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_firstFragment_to_barGraphFragment);
                } else {
                    Toast.makeText(getContext(),"First upload a PDF using the button at the bottom of your screen",Toast.LENGTH_SHORT).show();
                }


            }
        });

        lineGraphCardView = rootView.findViewById(R.id.lineGraphCardView);
        lineGraphCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.mPDFUri!=null){
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_firstFragment_to_lineGraphFragment);
                } else {
                    Toast.makeText(getContext(),"First upload a PDF using the button at the bottom of your screen",Toast.LENGTH_SHORT).show();
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

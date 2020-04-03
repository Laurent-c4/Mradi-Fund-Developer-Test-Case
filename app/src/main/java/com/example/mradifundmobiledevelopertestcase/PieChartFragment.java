package com.example.mradifundmobiledevelopertestcase;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.mradifundmobiledevelopertestcase.MainActivity.uploadedFilename;

public class PieChartFragment extends Fragment {

    PieChart pieChart;
    ImageButton leftNavigation;
    ImageButton rightNavigation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_piechart, container, false);

        pieChart = rootview.findViewById(R.id.pieChart);
        leftNavigation = rootview.findViewById(R.id.navigateToLineGraph);
        rightNavigation = rootview.findViewById(R.id.navigateToBarGraph);

        leftNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PieChartFragment.this)
                        .navigate(R.id.action_pieChartFragment_to_lineGraphFragment);
            }
        });

        rightNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PieChartFragment.this)
                        .navigate(R.id.action_pieChartFragment_to_barGraphFragment);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference mUserSummaryListReference = FirebaseDatabase.getInstance().getReference("Summary_List").child(userId).child(uploadedFilename.replaceAll("[^A-Za-z0-9]","")).child("summaryList");
        mUserSummaryListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<PieEntry> dataValues = new ArrayList<>();
                List<Summary> summaryList = new ArrayList<>();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    Summary summary = locationSnapshot.getValue(Summary.class);
                    summaryList.add(summary);
                }

                dataValues.add(new PieEntry(Float.parseFloat(summaryList.get(0).getSpent().toString()),summaryList.get(0).getTransactionType()));
                dataValues.add(new PieEntry(Float.parseFloat(summaryList.get(3).getSpent().toString()),summaryList.get(3).getTransactionType()));
                dataValues.add(new PieEntry(Float.parseFloat(summaryList.get(4).getSpent().toString()),summaryList.get(4).getTransactionType()));
                dataValues.add(new PieEntry(Float.parseFloat(summaryList.get(5).getSpent().toString()),summaryList.get(5).getTransactionType()));
                dataValues.add(new PieEntry(Float.parseFloat(summaryList.get(6).getSpent().toString()),summaryList.get(6).getTransactionType()));

                int[] colorClassArray = new int[]{Color.GREEN,Color.MAGENTA,Color.YELLOW,Color.CYAN,Color.RED};

                PieDataSet pieDataSet = new PieDataSet(dataValues,"Summary of Expenditure");
                pieDataSet.setColors(colorClassArray);
                PieData pieData = new PieData(pieDataSet);

                pieChart.setHoleRadius(10);
                pieChart.setCenterTextRadiusPercent(50);
                pieChart.setEntryLabelColor(Color.BLACK);
                pieChart.animateY(1000);
                pieChart.setTransparentCircleRadius(40);
                pieChart.setData(pieData);
                pieChart.invalidate();

                Toast.makeText(getContext(),"Rotate",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return rootview;
    }



}

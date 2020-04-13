package com.example.mradifundmobiledevelopertestcase;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
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
    TableLayout childlayout;
    ImageButton leftNavigation;
    ImageButton rightNavigation;
    List<Summary> summaryList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_piechart, container, false);

        pieChart =  rootview.findViewById(R.id.pieChart);
        childlayout = rootview.findViewById(R.id.child_layout);
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
                summaryList.clear();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    Summary summary = locationSnapshot.getValue(Summary.class);
                    summaryList.add(summary);
                }

                try {
                    dataValues.add(new PieEntry(Float.parseFloat(summaryList.get(0).getSpent().toString()), summaryList.get(0).getTransactionType()));
                    dataValues.add(new PieEntry(Float.parseFloat(summaryList.get(3).getSpent().toString()), summaryList.get(3).getTransactionType()));
                    dataValues.add(new PieEntry(Float.parseFloat(summaryList.get(4).getSpent().toString()), summaryList.get(4).getTransactionType()));
                    dataValues.add(new PieEntry(Float.parseFloat(summaryList.get(5).getSpent().toString()), summaryList.get(5).getTransactionType()));
                    dataValues.add(new PieEntry(Float.parseFloat(summaryList.get(6).getSpent().toString()), summaryList.get(6).getTransactionType()));

                    int[] colorClassArray = new int[]{Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.RED};



                    Description description = new Description();
                    description.setText("Summary");
                    pieChart.setDescription(description);

                    PieDataSet pieDataSet = new PieDataSet(dataValues, "Summary of Expenditure");
                    pieDataSet.setColors(colorClassArray);
                    PieData pieData = new PieData(pieDataSet);

                    setCustomLegend();

                    pieChart.setDrawEntryLabels(false);
                    pieChart.setUsePercentValues(true);
                    pieChart.setHoleRadius(10);
                    pieChart.setCenterTextRadiusPercent(50);
                    pieChart.setEntryLabelColor(Color.BLACK);
                    pieChart.animateY(1000);
                    pieChart.setTransparentCircleRadius(40);
                    pieChart.setData(pieData);
                    pieChart.setNoDataText("No data");
                    pieChart.invalidate();

                    Toast.makeText(getContext(), "Rotate", Toast.LENGTH_LONG).show();
                }catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return rootview;
    }

    public void setCustomLegend(){
        Legend legend = pieChart.getLegend();

        int[] colorClassArray = new int[]{Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.RED};

            LayoutInflater inflater = getLayoutInflater();
            TableRow tr = (TableRow)          inflater.inflate(R.layout.table_row_legend,
                    childlayout, false);
            childlayout.addView(tr);
            LinearLayout linearLayoutColorContainer=(LinearLayout) tr.getChildAt(0);
            LinearLayout linearLayoutColor= (LinearLayout) linearLayoutColorContainer.getChildAt(0);
            TextView tvLabel = (TextView) tr.getChildAt(1);
            TextView tvAmt = (TextView) tr.getChildAt(2);
            linearLayoutColor.setBackgroundColor(colorClassArray[0]);
            tvLabel.setText(summaryList.get(0).getTransactionType());
            tvAmt.setText("Ksh. " + summaryList.get(0).getSpent().toString());

            LayoutInflater inflater2 = getLayoutInflater();
            TableRow tr2 = (TableRow)          inflater2.inflate(R.layout.table_row_legend,
                    childlayout, false);
            childlayout.addView(tr2);
            LinearLayout linearLayoutColorContainer2=(LinearLayout) tr2.getChildAt(0);
            LinearLayout linearLayoutColor2= (LinearLayout) linearLayoutColorContainer2.getChildAt(0);
            TextView tvLabel2 = (TextView) tr2.getChildAt(1);
            TextView tvAmt2 = (TextView) tr2.getChildAt(2);
            linearLayoutColor2.setBackgroundColor(colorClassArray[1]);
            tvLabel2.setText(summaryList.get(3).getTransactionType());
            tvAmt2.setText("Ksh. " + summaryList.get(3).getSpent().toString());

            LayoutInflater inflater3 = getLayoutInflater();
            TableRow tr3 = (TableRow)          inflater3.inflate(R.layout.table_row_legend,
                    childlayout, false);
            childlayout.addView(tr3);
            LinearLayout linearLayoutColorContainer3=(LinearLayout) tr3.getChildAt(0);
            LinearLayout linearLayoutColor3= (LinearLayout) linearLayoutColorContainer3.getChildAt(0);
            TextView tvLabel3 = (TextView) tr3.getChildAt(1);
            TextView tvAmt3 = (TextView) tr3.getChildAt(2);
            linearLayoutColor3.setBackgroundColor(colorClassArray[2]);
            tvLabel3.setText(summaryList.get(4).getTransactionType());
            tvAmt3.setText("Ksh. " + summaryList.get(4).getSpent().toString());

            LayoutInflater inflater4 = getLayoutInflater();
            TableRow tr4 = (TableRow)          inflater4.inflate(R.layout.table_row_legend,
                    childlayout, false);
            childlayout.addView(tr4);
            LinearLayout linearLayoutColorContainer4=(LinearLayout) tr4.getChildAt(0);
            LinearLayout linearLayoutColor4= (LinearLayout) linearLayoutColorContainer4.getChildAt(0);
            TextView tvLabel4 = (TextView) tr4.getChildAt(1);
            TextView tvAmt4 = (TextView) tr4.getChildAt(2);
            linearLayoutColor4.setBackgroundColor(colorClassArray[3]);
            tvLabel4.setText(summaryList.get(5).getTransactionType());
            tvAmt4.setText("Ksh. " + summaryList.get(5).getSpent().toString());

            LayoutInflater inflater5 = getLayoutInflater();
            TableRow tr5 = (TableRow)          inflater5.inflate(R.layout.table_row_legend,
                    childlayout, false);
            childlayout.addView(tr5);
            LinearLayout linearLayoutColorContainer5=(LinearLayout) tr5.getChildAt(0);
            LinearLayout linearLayoutColor5= (LinearLayout) linearLayoutColorContainer5.getChildAt(0);
            TextView tvLabel5 = (TextView) tr5.getChildAt(1);
            TextView tvAmt5 = (TextView) tr5.getChildAt(2);
            linearLayoutColor5.setBackgroundColor(colorClassArray[4]);
            tvLabel5.setText(summaryList.get(6).getTransactionType());
            tvAmt5.setText("Ksh. " + summaryList.get(6).getSpent().toString());

        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getLegend().setEnabled(false);
    }



}

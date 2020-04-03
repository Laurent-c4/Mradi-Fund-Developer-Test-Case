package com.example.mradifundmobiledevelopertestcase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.example.mradifundmobiledevelopertestcase.MainActivity.lineGraphSeries;
import static com.example.mradifundmobiledevelopertestcase.MainActivity.maxY;
import static com.example.mradifundmobiledevelopertestcase.MainActivity.uploadedFilename;

public class BarGraphFragment extends Fragment {

    BarChart barChart;
    ArrayList<BarEntry> barEntries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<>();

    ImageButton leftNavigation;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_bargraph, container, false);
        barChart = rootview.findViewById(R.id.barGraph);
        leftNavigation = rootview.findViewById(R.id.navigateToPieChart);

        leftNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(BarGraphFragment.this)
                        .navigate(R.id.action_barGraphFragment_to_pieChartFragment);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference mUserExpenditureListReference = FirebaseDatabase.getInstance().getReference("Expenditure_List").child(userId).child(uploadedFilename.replaceAll("[^A-Za-z0-9]","")).child("expenditureList");
        mUserExpenditureListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, List<Expenditure>> cache = new HashMap<>();
                List<Expenditure> expenditureList = new ArrayList<>();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()){
                    Expenditure expenditure = locationSnapshot.getValue(Expenditure.class);


                    //Group same-day expenditure in hash map
                    List<Expenditure> list = cache.get(expenditure.getDate().substring(0,10));
                    if (list == null) {
                        list = new ArrayList<>();
                        cache.put(expenditure.getDate().substring(0,10),list);
                    }
                    list.add(expenditure);

                }

                // Function to sort map by Key
                // TreeMap to store values of HashMap
                TreeMap<String, List<Expenditure>> sorted = new TreeMap<>();

                // Copy all data from hashMap into TreeMap
                sorted.putAll(cache);

                // Display the TreeMap which is naturally sorted
                for (Map.Entry<String, List<Expenditure>> entry : sorted.entrySet()){

                    Double totalSpent = 0.0;

                    for (int i = 0; i < entry.getValue().size(); i++) {
                        totalSpent += entry.getValue().get(i).getSpent();
                    }

                    barEntries.add(new BarEntry(Integer.parseInt(entry.getKey().substring(8)), Float.parseFloat(totalSpent.toString()),entry.getKey()));
                    labels.add(entry.getKey());
//                    descriptionString = entry.getKey().substring(0,7);
                }

                BarDataSet barDataSet = new BarDataSet(barEntries, "Expenditure");
                Description description = new Description();
                description.setText("Daily Spending");
                barChart.setDescription(description);
                BarData data = new BarData();
                data.addDataSet(barDataSet);
                barChart.setData(data);
                barChart.animateY(4000);
                barChart.invalidate();

                YoYo.with(Techniques.ZoomIn)
                        .duration(1000)
                        .repeat(0)
                        .playOn(barChart);

                Toast.makeText(getContext(),"Zoom along x or y axis to adjust",Toast.LENGTH_LONG).show();


                barChart.setTouchEnabled(true);
                barChart.setDragEnabled(true);
                barChart.setScaleEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootview;
    }



}

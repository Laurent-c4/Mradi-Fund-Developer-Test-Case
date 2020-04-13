package com.example.mradifundmobiledevelopertestcase;

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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.example.mradifundmobiledevelopertestcase.MainActivity.uploadedFilename;

public class LineGraphFragment extends Fragment {

    ImageButton rightNavigation;
    LineChart lineChart;
    ArrayList<Entry> dataValues = new ArrayList<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_linegraph, container, false);
        lineChart = rootView.findViewById(R.id.lineChart);
        rightNavigation = rootView.findViewById(R.id.navigateToPieChart);

        rightNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LineGraphFragment.this)
                        .navigate(R.id.action_lineGraphFragment_to_pieChartFragment);
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


                    try {
                        //Group same-day expenditure in hash map
                        List<Expenditure> list = cache.get(expenditure.getDate().substring(0,10));
                        if (list == null) {
                            list = new ArrayList<>();
                            cache.put(expenditure.getDate().substring(0,10),list);
                        }
                        list.add(expenditure);
                    } catch (Exception e){

                    }


                }

                try {

                    // Function to sort map by Key
                    // TreeMap to store values of HashMap
                    TreeMap<String, List<Expenditure>> sorted = new TreeMap<>();

                    // Copy all data from hashMap into TreeMap
                    sorted.putAll(cache);

                    // Display the TreeMap which is naturally sorted
                    for (Map.Entry<String, List<Expenditure>> entry : sorted.entrySet()) {

                        Double totalSpent = 0.0;

                        for (int i = 0; i < entry.getValue().size(); i++) {
                            totalSpent += entry.getValue().get(i).getSpent();
                        }

                        dataValues.add(new Entry(Integer.parseInt(entry.getKey().substring(8)), Float.parseFloat(totalSpent.toString()), entry.getKey()));

                    }

                    LineDataSet lineDataSet = new LineDataSet(dataValues, "Expenditure");
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet);
                    Description description = new Description();
                    description.setText("Daily Spending");
                    lineChart.setDescription(description);
                    LineData data = new LineData(dataSets);
                    lineChart.setData(data);
                    lineChart.animateY(1500);
                    lineChart.invalidate();

                    YoYo.with(Techniques.ZoomIn)
                            .duration(1000)
                            .repeat(0)
                            .playOn(lineChart);

                    Toast.makeText(getContext(), "Zoom along x or y axis to adjust", Toast.LENGTH_LONG).show();

                }catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return rootView;
    }

}

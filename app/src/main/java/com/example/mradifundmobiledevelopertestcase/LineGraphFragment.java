package com.example.mradifundmobiledevelopertestcase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
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

public class LineGraphFragment extends Fragment {

    ImageButton rightNavigation;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_linegraph, container, false);
        GraphView graph = rootView.findViewById(R.id.lineGraph);
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
        //    LineGraphSeries<DataPoint> series;
        DatabaseReference mUserExpenditureListReference = FirebaseDatabase.getInstance().getReference("Expenditure_List").child(userId).child(uploadedFilename.replaceAll("[^A-Za-z0-9]","")).child("expenditureList");
        mUserExpenditureListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, List<Expenditure>> cache = new HashMap<>();
                List<Expenditure> expenditureList = new ArrayList<>();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()){
                    Expenditure expenditure = locationSnapshot.getValue(Expenditure.class);


                    //Group same-day expenditure in hash map
                    List<Expenditure> list = cache.get(expenditure.getDate().substring(8,10));
                    if (list == null) {
                        list = new ArrayList<>();
                        cache.put(expenditure.getDate().substring(8,10),list);
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



                    lineGraphSeries.appendData(new DataPoint(Integer.parseInt(entry.getKey()), totalSpent),true,cache.entrySet().size());
                    if (totalSpent > maxY){
                        maxY = totalSpent + 2000;
                    }
                }

                graph.removeAllSeries();
                graph.addSeries(lineGraphSeries);

                graph.getViewport().setMaxX(31);
                graph.getViewport().setMinX(0);
                graph.getViewport().setXAxisBoundsManual(true);

                graph.getViewport().setMaxY(maxY);
                graph.getViewport().setMinY(0);
                graph.getViewport().setYAxisBoundsManual(true);

                lineGraphSeries = new LineGraphSeries<DataPoint>();
                maxY=0.0;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return rootView;
    }

}

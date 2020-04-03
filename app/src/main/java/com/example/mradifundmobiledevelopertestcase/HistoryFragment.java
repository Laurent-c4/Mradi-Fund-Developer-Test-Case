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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.mradifundmobiledevelopertestcase.MainActivity.fab;

public class HistoryFragment extends Fragment {
    private CardView historyListCardView;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        historyListCardView = rootView.findViewById(R.id.historyListCardView);

        MainActivity mainActivity = (MainActivity) getActivity();
        FirebaseRecyclerAdapter<ExpenditureList, FireBaseMPESAStatementsViewHolder> mFirebaseAdapter = mainActivity.getmFirebaseAdapter();

        RecyclerView mRecyclerview = rootView.findViewById(R.id.historyRecyclerView);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.list_divider));
        mRecyclerview.addItemDecoration(divider);
        mRecyclerview.setItemViewCacheSize(20);

        mRecyclerview.setAdapter(mFirebaseAdapter);

        YoYo.with(Techniques.ZoomInRight)
                .duration(400)
                .repeat(0)
                .playOn(historyListCardView);

        return rootView;


    }

}

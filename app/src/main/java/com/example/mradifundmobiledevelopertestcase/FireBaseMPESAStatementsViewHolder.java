package com.example.mradifundmobiledevelopertestcase;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class FireBaseMPESAStatementsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    View mView;
    Context mContext;
    private int position;

    public FireBaseMPESAStatementsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindStatement(ExpenditureList expenditureList, int i) {
        TextView labelTextView = mView.findViewById(R.id.labelTextView);
        TextView MPESAStatementTextView = mView.findViewById(R.id.MPESAStatementTextView);
        TextView totalSpentTextView = mView.findViewById(R.id.totalSpentTextView);

        try{labelTextView.setText(Integer.toString(i+1));}catch (Exception e){}
        MPESAStatementTextView.setText(String.format("%s %s-%s", expenditureList.getStatementName().substring(0, 14), expenditureList.getStatementName().substring(14, 18), expenditureList.getStatementName().substring(18, 20)));

        Double totalSpent = 0.0;
        for (Expenditure expenditure : expenditureList.getExpenditureList()){
            totalSpent+=expenditure.getSpent();
        }
        totalSpentTextView.setText(totalSpent.toString());

        position = i;

    }

    @Override
    public void onClick(View view) {

    }



}

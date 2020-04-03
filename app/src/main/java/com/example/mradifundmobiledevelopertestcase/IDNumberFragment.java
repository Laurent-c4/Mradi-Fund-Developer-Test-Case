package com.example.mradifundmobiledevelopertestcase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IDNumberFragment extends DialogFragment {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = FirebaseDatabase.getInstance()
            .getReference("Passwords")
            .child(user.getUid());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_id_number, container, false);

        TextView IDUserNameTextView = rootView.findViewById(R.id.IDUserNameTextView);
        EditText setIDNumberEditText = rootView.findViewById(R.id.setIDNumberEditText);
        Button submitIDNumberButton = rootView.findViewById(R.id.sumbitIDNUmberButton);
        Button editIDNumberButton = rootView.findViewById(R.id.editIDNumberButton);
        TextView IDNumberTextView = rootView.findViewById(R.id.IDNumberTextView);
        TextView promptTextView = rootView.findViewById(R.id.promptTextView);

        IDUserNameTextView.setText(user.getDisplayName());


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Password password = new Password();
                password = dataSnapshot.getValue(Password.class);
                try{
                IDNumberTextView.setText(password.getPassword());}catch (Exception e){}

                if(IDNumberTextView.getText().toString().trim().length()==0){
                    editIDNumberButton.setVisibility(View.GONE);
                    IDNumberTextView.setVisibility(View.GONE);
                    setIDNumberEditText.setVisibility(View.VISIBLE);
                    submitIDNumberButton.setVisibility(View.VISIBLE);
                    promptTextView.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        editIDNumberButton.setVisibility(View.VISIBLE);
        IDNumberTextView.setVisibility(View.VISIBLE);
        setIDNumberEditText.setVisibility(View.GONE);
        submitIDNumberButton.setVisibility(View.GONE);
        promptTextView.setVisibility(View.GONE);


        editIDNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editIDNumberButton.setVisibility(View.GONE);
                IDNumberTextView.setVisibility(View.GONE);
                setIDNumberEditText.setVisibility(View.VISIBLE);
                submitIDNumberButton.setVisibility(View.VISIBLE);
                promptTextView.setVisibility(View.VISIBLE);


            }
        });

        submitIDNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setIDNumberEditText.getText().toString().trim().length()>0 && setIDNumberEditText.getText().toString().matches("[0-9]+")){
                    Password password = new Password();
                    password.setPassword(setIDNumberEditText.getText().toString());
                    ref.setValue(password);
                    editIDNumberButton.setVisibility(View.VISIBLE);
                    IDNumberTextView.setVisibility(View.VISIBLE);
                    setIDNumberEditText.setVisibility(View.GONE);
                    submitIDNumberButton.setVisibility(View.GONE);

                    Toast.makeText(getContext(), "Done! Ensure ID Number is the one responsible for you MPESA Statements",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getContext(), "Invalid ID Number Format",Toast.LENGTH_SHORT).show();

                }

            }
        });
        return rootView;
    }


}

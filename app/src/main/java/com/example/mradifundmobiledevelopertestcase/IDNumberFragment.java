package com.example.mradifundmobiledevelopertestcase;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IDNumberFragment extends DialogFragment {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = FirebaseDatabase.getInstance()
            .getReference("Passwords")
            .child(user.getUid());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_id_number, container, false);

        EditText passwordEditText = rootView.findViewById(R.id.setPassword);

        Button submit = rootView.findViewById(R.id.submitPassword);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Password password = new Password();
                password.setPassword(passwordEditText.getText().toString());
                ref.setValue(password);
                dismiss();
            }
        });



        return rootView;
    }
}

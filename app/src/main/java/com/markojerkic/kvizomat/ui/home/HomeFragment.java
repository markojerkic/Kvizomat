package com.markojerkic.kvizomat.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.markojerkic.kvizomat.R;
import com.markojerkic.kvizomat.ui.PostaviPitanje;
import com.markojerkic.kvizomat.ui.kviz.Kviz;
import com.markojerkic.kvizomat.ui.kviz.KvizInformacije;
import com.markojerkic.kvizomat.ui.kviz.Pitanje;

import java.util.ArrayList;
import java.util.Random;

public class HomeFragment extends Fragment {

    private ArrayList<Pitanje> mListaPitanja;

    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("pitanja");

    private HomeViewModel homeViewModel;
    private Button mLastManButton;
    private Button mCategoryButton;
    private Toast mToast;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        // Set MainActivity main buttons upon entering
        mLastManButton = root.findViewById(R.id.last_man_button);
        mCategoryButton = root.findViewById(R.id.friendly_quitz_button);

        // Database listener
        mListaPitanja = new ArrayList<>();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Pitanje pitanje = dataSnapshot.getValue(Pitanje.class);
                mListaPitanja.add(pitanje);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mRef.addChildEventListener(childEventListener);

        // Set on-click listeners
        mLastManButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pitanjeActivity = new Intent(getActivity(), Kviz.class);
                ArrayList<Pitanje> pitanja = randomPitanja(4);

                pitanjeActivity.putExtra("pitanja", new KvizInformacije(pitanja));
                startActivity(pitanjeActivity);
                Toast.makeText(getActivity(), "Idemo na pitanje!!!", Toast.LENGTH_SHORT).show();
            }
        });
        mCategoryButton.setClickable(false);
        mCategoryButton.setVisibility(View.INVISIBLE);
        mCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postaviPitanjeActivity = new Intent(getActivity(), PostaviPitanje.class);
                startActivity(postaviPitanjeActivity);
                Toast.makeText(getActivity(), "Ajmo napraviti par pitanja!!!", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
    private ArrayList<Pitanje> randomPitanja (int brojPitanja) {
        Random random = new Random();
        ArrayList<Pitanje> pitanjaRez = new ArrayList<>();
        while (pitanjaRez.size() < brojPitanja) {
            int idPitanja = random.nextInt(mListaPitanja.size());
            Pitanje trPit = mListaPitanja.get(idPitanja);
            if (!pitanjaRez.contains(trPit)) {
                pitanjaRez.add(trPit);
            }
        }
        return pitanjaRez;
    }
}

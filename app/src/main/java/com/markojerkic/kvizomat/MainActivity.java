package com.markojerkic.kvizomat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.markojerkic.kvizomat.ui.kviz.Korisnik;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 555;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseUser mUser;
    private TextView userName;
    private TextView userEmail;
    private ImageView userPhoto;
    private TextView odjavaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        userName = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.user_email);
        userPhoto = navigationView.getHeaderView(0).findViewById(R.id.user_photo);
        odjavaView = navigationView.findViewById(R.id.odjava);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.d("auth", "nema usera");
            createSignInIntent();
        } else {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            updateUI();
        }

        odjavaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser != null) {
                    odjava();
                }
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    private void odjava() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                createSignInIntent();
            }
        });
    }

    private void updateUI() {
        String name = mUser.getDisplayName();
        String email = mUser.getEmail();
        Uri photoURL = mUser.getPhotoUrl();
        Log.d("auth", "ime: " + userName);
        Log.d("auth", "email " + userEmail);
        Log.d("auth", "photo " + photoURL);
        userEmail.setText(email);
        userName.setText(name);
        Picasso.get().load(photoURL).into(userPhoto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                mUser = FirebaseAuth.getInstance().getCurrentUser();final ArrayList<Korisnik> korisnici = new ArrayList<>();
                final DatabaseReference db = FirebaseDatabase.getInstance().getReference("korisnici");
                 db.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         for (DataSnapshot ds: dataSnapshot.getChildren()) {
                             korisnici.add(ds.getValue(Korisnik.class));
                         }
                         ArrayList<String> pr = new ArrayList<>();
                         pr.add("prvVr");
                         Korisnik upKor = new Korisnik(mUser.getDisplayName(), mUser.getEmail(),
                                 mUser.getPhotoUrl().toString(), mUser.getUid(), pr, 0.f);
                         Log.d("Korisnik", "Postojim");

                         if (!korisnici.contains(upKor)) {
                             db.push().setValue(upKor);
                         }
                         updateUI();
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {

                     }
                 });
//                db.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        korisnici.add(dataSnapshot.getValue(Korisnik.class));
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//                ArrayList<String> pr = new ArrayList<>();
//                pr.add("prvVr");
//                Korisnik upKor = new Korisnik(mUser.getDisplayName(), mUser.getEmail(),
//                        mUser.getPhotoUrl().toString(), mUser.getUid(), pr, 0.f);
//
//                if (!korisnici.contains(upKor)) {
//                    db.push().setValue(upKor);
//                }
//                updateUI();
            } else {
                int errorCode = response.getError().getErrorCode();
                Log.e("Kviz", "signin greška " + errorCode);
            }

        }
    }

    public void createSignInIntent() {
        // Firebase login
        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers).build(), RC_SIGN_IN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

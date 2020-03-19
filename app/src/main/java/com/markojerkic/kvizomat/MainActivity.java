package com.markojerkic.kvizomat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
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
    private Dialog infoDialog;

    private Dialog upisiInfoDialog;

    private Korisnik upKor;
    final DatabaseReference db = FirebaseDatabase.getInstance().getReference("korisnici");
    final DatabaseReference tokenDb =FirebaseDatabase.getInstance().getReference("korisniciToken");

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

        upisiInfoDialog = new Dialog(this);
        upisiInfoDialog.setContentView(R.layout.upisi_informacije);

        infoDialog = new Dialog(this);
        infoDialog.setContentView(R.layout.info_o_nama);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.d("auth", "nema usera");
            createSignInIntent();
        } else {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            setKorisnik();
        }

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        odjavaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser != null) {
                    odjava();
                }
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

            }
        });
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
        String name = upKor.getIme();
        String email = mUser.getEmail();
        String photoURL = upKor.getUri();
        Log.d("auth", "ime: " + userName);
        Log.d("auth", "email " + userEmail);
        Log.d("auth", "photo " + photoURL);
        userEmail.setText(email);
        userName.setText(name);
        if (!photoURL.equals("null")) {
            Picasso.get().load(photoURL).into(userPhoto);
        }
    }

    public void setKorisnik() {
        if (mUser == null)
            mUser = FirebaseAuth.getInstance().getCurrentUser();
        final ArrayList<Korisnik> korisnici = new ArrayList<>();
        final ArrayList<String> korUID = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String korIndex[] = new String[1];
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Korisnik k = ds.getValue(Korisnik.class);
                    korisnici.add(k);
                    korUID.add(k.getUid());
                    Log.d("korisnik", mUser.getUid());
                    if (k.getUid().equals(mUser.getUid())) {
                        upKor = k;
                        korIndex[0] = ds.getKey();
                    }
                }
                if (upKor == null) {
                    setKorisnik();
                    return;
                }

                if (upKor.getIme().equals("null_null_null")) {
                    final EditText editText = upisiInfoDialog.findViewById(R.id.upisi_ime);
                    Button upisiTipka = upisiInfoDialog.findViewById(R.id.upisi_ime_tipka);
                    upisiInfoDialog.show();

                    upisiTipka.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (editText.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(),
                                        "Upisite informacije", Toast.LENGTH_LONG).show();
                            } else {
                                upKor.setIme(editText.getText().toString().trim());
                                db.child(korIndex[0]).setValue(upKor);
                            }
                            upisiInfoDialog.cancel();
                            updateUI();
                        }
                    });
                    updateUI();
                } else {
                    updateUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        String token = task.getResult().getToken();

                        tokenDb.child(mUser.getUid()).setValue(token);
                    }
                });
                setKorisnik();
            } else {
                createSignInIntent();
            }

        }
    }

    public void createSignInIntent() {
        // Firebase login
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher)
                .build(), RC_SIGN_IN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                infoDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

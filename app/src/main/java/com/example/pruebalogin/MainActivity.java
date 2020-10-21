package com.example.pruebalogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 12345 ;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    public SignInButton sign_in_button;
    public LinearLayout signOutAndDisconnect;
    public TextView tvMostrarUsuario;
    public Button signOutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        sign_in_button=findViewById(R.id.sign_in_button);
        signOutAndDisconnect=findViewById(R.id.signOutAndDisconnect);
        tvMostrarUsuario=findViewById(R.id.tvMostrarUsuario);
        signOutButton=findViewById(R.id.signOutButton);

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;

                }
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount  account = task.getResult(ApiException.class);
                Log.d("miFiltro", "firebaseAuthWithGoogle:" + account.getId());
                handleSignInResult(task);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("miFiltro", "Google sign in failed", e);
                // ...
            }
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("miFiltro", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    protected void onStart(){
        super.onStart();
        GoogleSignInAccount currentUser =GoogleSignIn.getLastSignedInAccount(this);
        updateUI(currentUser);
    }


    private void updateUI(GoogleSignInAccount user) {

        if (user != null) {
            Toast.makeText(MainActivity.this, "Authentication ok.",
                    Toast.LENGTH_SHORT).show();
            sign_in_button.setVisibility(View.GONE);
            signOutAndDisconnect.setVisibility(View.VISIBLE);
            tvMostrarUsuario.setText(user.getEmail());
            cerrarSesion();
        } else {
            Toast.makeText(MainActivity.this, "Authentication nono.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void cerrarSesion() {
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut();

                sign_in_button.setVisibility(View.VISIBLE);
                signOutAndDisconnect.setVisibility(View.GONE);
                updateUI(null);
            }
        });
    }
}
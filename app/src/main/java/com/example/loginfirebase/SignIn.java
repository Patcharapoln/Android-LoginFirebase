package com.example.loginfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        email = (TextView) findViewById(R.id.email);
        email.setText(mAuth.getCurrentUser().getEmail());
    }

    public void onLogoutButtonClicked(View view) {
        mAuth.signOut();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(i);
    }

    public void onNewPostButtonClicked(View view) {
        Intent i = new Intent(SignIn.this, NewPost.class);
        startActivity(i);
    }

    public void onViewPostButtonClicked(View view) {
        Intent i = new Intent(SignIn.this, ViewPost.class);
        startActivity(i);
    }
}

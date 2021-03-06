package com.ead.epicbase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

    }

    public void resetPassword(View view) {
        startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
    }

    public void signIn(View view) {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        finish();
    }

    public void signUp(final View view) {
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Snackbar.make(view, "Enter email address!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Snackbar.make(view, "Enter email address!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }

        if(password.length() < 6) {
            Snackbar.make(view, "Password too short, enter minimum 6 characters!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //create user - createUserWithEmailAndPassword
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                /*Snackbar.make(view, "createUserWithEmail:onComplete "+task.isSuccessful(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();*/
                progressBar.setVisibility(View.GONE);

                //if sign in fails, display a message to the user. If sign in succeeds
                //then the auth state listener will be notified and logic for handling signed-in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Snackbar.make(view, "Authentication failed! ", Snackbar.LENGTH_SHORT)
                            .setAction("Action",null).show();
                    Log.d("Firebase",""+task.getException());
                }
                else {
                    String user_name = firebaseAuth.getCurrentUser().getDisplayName();
                    String user_email = email;
                    Log.d("firebase user_name",""+firebaseAuth.getCurrentUser());

                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name",user_name);
                    bundle.putString("email",user_email);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    //remove SignupActivity from android stack after redirecting to HomeActivity,
                    //so that when you press back button 'SignupActivity' doesn't appears again
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnLogin, btnSignUp, btnReset;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //get firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //if there is a current user (ie., already signed-in user), then redirect to HomeActivity
        //else if current user is NULL, then dont redirect to HomeActivity, rather show Login screen
        if(firebaseAuth.getCurrentUser() != null) {
            String user_email = firebaseAuth.getCurrentUser().getEmail();
            String user_name = firebaseAuth.getCurrentUser().getDisplayName();

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("name",user_name);
            bundle.putString("email",user_email);
            intent.putExtras(bundle);
            startActivity(intent);
            //remove LoginActivity from android's back stack, so that when user clicks on back button
            //Login screen doesn't shows up again
            finish();
        }

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //get firebase auth instance - (AGAIN) <-- testing
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signUp(View view) {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        finish();
    }

    public void resetPassword(View view) {
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        //on pressing back button, LoginActivity will re-appear
    }

    public void signIn(final View view) {
        email = inputEmail.getText().toString();
        password = inputPassword.getText().toString();

        if(TextUtils.isEmpty(email)) {
            Snackbar.make(view, "Enter email address!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Snackbar.make(view, "Enter password!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //authenticate user - signInWithEmailAndPassword
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                //if sign in fails, display a message to the user. If sign in succeeds
                //then the auth state listener will be notified and logic for handling signed-in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    //there was an error
                    if(password.length() < 6) {
                        inputPassword.setError(getString(R.string.minimum_password));
                    }
                    else{
                        Snackbar.make(view, getString(R.string.auth_failed), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }
                //no error
                else {
                    String user_name = firebaseAuth.getCurrentUser().getDisplayName();
                    String user_email = email;

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name",user_name);
                    bundle.putString("email",user_email);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    //remove SignupActivity from android stack after redirecting to HomeActivity,
                    //so that when you press back button 'LoginActivity' doesn't appears again
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

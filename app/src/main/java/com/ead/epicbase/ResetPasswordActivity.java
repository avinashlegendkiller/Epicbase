package com.ead.epicbase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        btnBack = (Button) findViewById(R.id.btn_back);
        inputEmail = (EditText) findViewById(R.id.email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //handle keyboard
        //code from stackoverflow - http://stackoverflow.com/questions/8063439/android-edittext-finished-typing-event
        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
                    //when focus of Editext is lost, ie., when enter/done key is pressed - hide the keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    public void resetPassword(final View view) {
        email = inputEmail.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            Snackbar.make(view, "Enter your registered email id", Snackbar.LENGTH_SHORT)
                    .setAction("Action",null).show();
            return;
        }

        //show progress bar
        progressBar.setVisibility(View.VISIBLE);
        //reset password - sendPasswordResetEmail
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(ResetPasswordActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Snackbar.make(view, "We have sent you instructions to reset your password!", Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                else {
                    Snackbar.make(view, "Failed to send reset email!", Snackbar.LENGTH_SHORT)
                            .setAction("Action",null).show();
                }
                //hide progress bar
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void goBack(View view) {
        //close this activity, controls goes back to either LoginActivity/SignupActivity
        //depending on the activity which called ResetPassword
        finish();
    }
}

package com.ead.epicbase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    String user_name, user_email;
    TextView details;
    FirebaseAuth firebaseAuth;
    private Button btnChangeEmail, btnChangePassword, btnSendResetEmail, btnRemoveUser,
            changeEmail, changePassword, sendEmail, remove, signOut;

    private EditText oldEmail, newEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this doesn't works
        //toolbar.setTitle(getString(R.string.app_name));
        //this works!!!
        getSupportActionBar().setTitle(getString(R.string.app_name));

        //get firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null) {
                    //user auth state is changed - user is null
                    //launch login activity
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user_email = bundle.getString("email");
        user_name = bundle.getString("name");

        //set emoji by unicode - http://stackoverflow.com/questions/26893796/how-set-emoji-by-unicode-in-android-textview
        //unicode list - http://apps.timwhitlock.info/emoji/tables/unicode
        int unicode = 0x1F60A;
        String emoji = getEmojiByUnicode(unicode);

        //textview
        details = (TextView) findViewById(R.id.details);
        details.setText("Hola! "+extractNameFromEmail(user_email)+" "+emoji);
        //button
        btnChangeEmail = (Button) findViewById(R.id.change_email_button);
        btnChangePassword = (Button) findViewById(R.id.change_password_button);
        btnSendResetEmail = (Button) findViewById(R.id.sending_pass_reset_button);
        btnRemoveUser = (Button) findViewById(R.id.remove_user_button);
        changeEmail = (Button) findViewById(R.id.changeEmail);
        changePassword = (Button) findViewById(R.id.changePass);
        sendEmail = (Button) findViewById(R.id.send);
        remove = (Button) findViewById(R.id.remove);
        signOut = (Button) findViewById(R.id.btn_signout);
        //edit text
        oldEmail = (EditText) findViewById(R.id.old_email);
        newEmail = (EditText) findViewById(R.id.new_email);
        password = (EditText) findViewById(R.id.password);
        newPassword = (EditText) findViewById(R.id.newPassword);

        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.VISIBLE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newEmailValue = newEmail.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);

                if(user != null && !newEmailValue.equals("")) { //user should not be NULL & new-email should not be NULL
                    //user.updateEmail
                    user.updateEmail(newEmailValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(HomeActivity.this, "Email address is updated. Please sign in with email id!", Toast.LENGTH_SHORT).show();
                                signOut();
                                progressBar.setVisibility(View.GONE);
                            }
                            else {
                                Toast.makeText(HomeActivity.this, "Failed to update email!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                }
                else if(newEmailValue.equals("")) {
                    newEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPasswordValue = newPassword.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);

                if(user != null && !newPasswordValue.equals("")) {
                    if(newPasswordValue.length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    }
                    else {
                        //user.updatePassword
                        user.updatePassword(newPasswordValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(HomeActivity.this, "Password is updated. Please sign in with new password!", Toast.LENGTH_SHORT).show();
                                    signOut();
                                    progressBar.setVisibility(View.GONE);
                                }
                                else {
                                    Toast.makeText(HomeActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }


                }
                else if(newPasswordValue.equals("")) {
                    newEmail.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldEmail.setVisibility(View.VISIBLE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String oldEmailValue = oldEmail.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);

                if(!oldEmailValue.equals("")) {
                    //sendPasswordResetEmail
                    firebaseAuth.sendPasswordResetEmail(oldEmailValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Snackbar.make(view, "Reset password email is sent!",Snackbar.LENGTH_SHORT)
                                        .setAction("Action",null).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            else {
                                Snackbar.make(view, "Failed to send reset email!",Snackbar.LENGTH_SHORT)
                                        .setAction("Action",null).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else {
                    oldEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                progressBar.setVisibility(View.VISIBLE);
                // http://stackoverflow.com/questions/5796611/dialog-throwing-unable-to-add-window-token-null-is-not-for-an-application-wi
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this, R.style.MyDialogTheme);
                builder.setMessage("Are you sure you want to delete your profile?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes
                        if(user != null) { //when a user exists, delete
                            //delete user - user.delete
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(HomeActivity.this, "Your profile is now deleted :( Create a new one!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(HomeActivity.this, SignupActivity.class));
                                        //clear this HomeActivity
                                        finish();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    else {
                                        Snackbar.make(view, "Failed to delete your account!",Snackbar.LENGTH_SHORT)
                                                .setAction("Action",null).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        progressBar.setVisibility(View.GONE);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public void signOutButton(View view) {
       signOut();
    }

    private void signOut() {
        firebaseAuth.signOut();
    }

    private String extractNameFromEmail(String email) {
        int length = email.length();
        char[] emailCharArray = new char[length];

        for(int index=0; index<length; index++) {
            if(email.charAt(index) == '@') {
                break;
            }
            else {
                emailCharArray[index] = email.charAt(index);
            }
        }

        //convert char array to string
        String name = String.valueOf(emailCharArray);
        return name;
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
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

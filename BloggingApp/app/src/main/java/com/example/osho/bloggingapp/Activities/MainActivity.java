package com.example.osho.bloggingapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.osho.bloggingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private EditText email;
    private EditText password;
    private Button login;
    private Button createAccount;

    // Read the Article https://www.techotopia.com/index.php/Firebase_Realtime_Database_Rules
    // to know the about the Firebase security rules

    //My application can be related to Google Classroom in which teacher addd something and students can read the content

    //Link to databse - https://console.firebase.google.com/u/0/project/bloggingapp-7d625/database/bloggingapp-7d625/data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        email = (EditText) findViewById(R.id.emailID);
        password = (EditText) findViewById(R.id.passwordID);
        login = (Button) findViewById(R.id.loginID);
        createAccount=(Button) findViewById(R.id.createAccountID);

        mAuth=FirebaseAuth.getInstance();

        //Authentication Process
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mUser=firebaseAuth.getCurrentUser();

                if(mUser!=null)
                {
                    Toast.makeText(MainActivity.this," Sign in", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this,PostListActivity.class));
                    finish(); // current activity got finish

                }


                else
                    Toast.makeText(MainActivity.this," Failed Sign in", Toast.LENGTH_LONG).show();


            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailString=email.getText().toString();
                String pwd=password.getText().toString();

                loginFunction(emailString,pwd);
            }
        });



        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateAccountActivity.class));
                finish();
            }
        });

    }

    // function for log in for the exisiting users
    //Also you can take help from Firebase Assistant in Android Studio whick will guide us in many examples

    private void loginFunction(String emailString, String pwd)
    {
        if(!emailString.equals("") && !pwd.equals(""))
        {
            mAuth.signInWithEmailAndPassword(emailString,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) { // will tell whether we logged in or not

                    if(!task.isSuccessful())
                        Toast.makeText(MainActivity.this,"Failed Sign in", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MainActivity.this," Sign in", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this,PostListActivity.class));
                    finish();

                    // we can now write to the database
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_signout)
        {
            mAuth.signOut();
        }


        return super.onOptionsItemSelected(item);
    }

    // this override method will display the menu in the top most bar which we have created in main_menu.xml file
    // this function creates the menu on the screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Right click on the screen and then press Generate option ... , from there choose onStart and onStop method
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }
}

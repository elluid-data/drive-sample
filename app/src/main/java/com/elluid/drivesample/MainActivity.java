package com.elluid.drivesample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.elluid.drivesample.drive.DriveServiceHelper;
import com.elluid.drivesample.drive.SignInHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import static com.elluid.drivesample.drive.SignInHelper.REQUEST_CODE_SIGN_IN;

public class MainActivity extends AppCompatActivity {

    private SignInHelper signInHelper;
    private TextView textviewDriveStatus;
    private Button buttonSendData;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQUEST_CODE_SIGN_IN:
                handleSignInResult(data);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textviewDriveStatus = findViewById(R.id.drive_status);
        buttonSendData = findViewById(R.id.button_send_data);
        findViewById(R.id.button_connect).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        buttonSendData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSampleData();
            }
        });
    }

    private void signIn() {
        if(signInHelper == null)
            signInHelper = new SignInHelper(MainActivity.this);
        if(signInHelper.isSignedIn()) {
            updateDriveStatus();
        } else {
            Intent startSignIn = signInHelper.startSignIn(this);
            startActivityForResult(startSignIn, REQUEST_CODE_SIGN_IN);
        }
    }

    private void handleSignInResult(Intent result) {

        GoogleSignIn.getSignedInAccountFromIntent(result).addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        signInHelper.setAccount(googleSignInAccount);
                        updateDriveStatus();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signInHelper.parseErrorMessage(e.getMessage(), MainActivity.this);
                    }
                });
    }

    private void updateDriveStatus()
    {
        textviewDriveStatus.setText("Authorized for Drive usage!");
        textviewDriveStatus.setVisibility(View.VISIBLE);
        buttonSendData.setVisibility(View.VISIBLE);
    }

    private void sendSampleData()
    {
        DriveServiceHelper driveServiceHelper = DriveServiceHelper.getDriveServiceHelper(signInHelper.getAccount(), this);
        String testData = "Hi Drive! \n I Hope this arrives safe and sound. \n Regards, Elluid";
        Task<String> uploadTask = driveServiceHelper.sendFile("sample.txt", testData.getBytes());

        uploadTask.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getBaseContext(), task.getResult() + " sent successfully.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Error:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
package com.elluid.drivesample.drive;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.drive.DriveScopes;

public class SignInHelper {
    public static final int REQUEST_CODE_SIGN_IN = 20;

    private GoogleSignInAccount account;
    private GoogleSignInClient client;


    public SignInHelper(Context context)
    {
        this.account = GoogleSignIn.getLastSignedInAccount(context);
    }
    // More info about the DriveScopes at
    // https://developers.google.com/resources/api-libraries/documentation/drive/v3/java/latest/com/google/api/services/drive/DriveScopes.html

    public boolean isSignedIn() {
        return (account != null && account.getGrantedScopes().contains(DriveScopes.DRIVE_FILE));
    }

    public Intent startSignIn(Context context) {
        buildGoogleSignInClient(context);
        return client.getSignInIntent();
    }

    private void buildGoogleSignInClient(Context context) {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();

        client =  GoogleSignIn.getClient(context, signInOptions);
    }

    public GoogleSignInAccount getAccount() {
        return account;
    }

    public void setAccount(GoogleSignInAccount account)
    {
        this.account = account;
    }

    public void parseErrorMessage(String message, Context context) {
        int errorCode = Integer.parseInt(message.replaceAll("[^\\d]", ""));
        switch (errorCode) {
            case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                Toast.makeText(context, "No account selected.", Toast.LENGTH_LONG).show();
                break;
            case GoogleSignInStatusCodes.SIGN_IN_FAILED:
                Toast.makeText(context, "Login failed. Application authorized?", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(context, "Error: " + message, Toast.LENGTH_LONG).show();
        }
    }
}


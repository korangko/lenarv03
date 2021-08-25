package com.example.lenarv03.livestream;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.lenarv03.MainMenuActivity;
import com.example.lenarv03.R;
import com.example.lenarv03.utils.YouTubeApi;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import static com.example.lenarv03.livestream.YtbSettingActivity2.REQUEST_AUTHORIZATION;
import static com.example.lenarv03.utils.YouTubeApi.AccountMail;
import static com.example.lenarv03.utils.YouTubeApi.AccountName;
import static com.example.lenarv03.utils.YouTubeApi.account;
import static com.example.lenarv03.utils.YouTubeApi.credential;
import static com.example.lenarv03.utils.YouTubeApi.mGoogleSignInClient;

public class LiveSelectActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    private static final int REQUEST_GMS_ERROR_DIALOG = 1;
    private static final int REQUEST_ACCOUNT_PICKER = 2;
    private static final int REQUEST_STREAMER = 4;
    private static final int RC_SIGN_IN = 5;
    private static final String TAG = "Lenar TAG";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveselect);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        findViewById(R.id.menu_before_btn).setOnClickListener(this);
        findViewById(R.id.youtube_selectbtn).setOnClickListener(this);
        findViewById(R.id.facebook_selectbtn).setOnClickListener(this);
        findViewById(R.id.twitch_selectbtn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.youtube_selectbtn:
                /**google account**/
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                account = GoogleSignIn.getLastSignedInAccount(this);
                if (account != null) {
                    System.out.println("josh account  not null");
                    startActivity(new Intent(LiveSelectActivity.this, YtbSettingActivitiy1.class)); //로딩이 끝난 후, ChoiceFunction 이동
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    LiveSelectActivity.this.finish();
                } else {
                    System.out.println("josh account null");
                    signIn();
                }
                break;
            case R.id.facebook_selectbtn:

                break;
            case R.id.twitch_selectbtn:
                break;

            case R.id.menu_before_btn:
                startActivity(new Intent(LiveSelectActivity.this, MainMenuActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                LiveSelectActivity.this.finish();
                break;
        }
    }

    /**
     * google login
     **/
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        switch (requestCode) {
            case RC_SIGN_IN:
                System.out.println("josh 0");

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
            case REQUEST_GMS_ERROR_DIALOG:
                System.out.println("josh 1");

                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
                System.out.println("josh 2");

                if (resultCode == Activity.RESULT_OK) {
                    haveGooglePlayServices();
                } else {
                    checkGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                System.out.println("josh 3");
                if (resultCode == Activity.RESULT_OK && data != null
                        && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(
                            AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(AccountMail);
                    }
                }
                break;
            case REQUEST_STREAMER:
                System.out.println("josh 4");

                if (resultCode == Activity.RESULT_OK && data != null
                        && data.getExtras() != null) {
                    String broadcastId = data.getStringExtra(YouTubeApi.BROADCAST_ID_KEY);
                    if (broadcastId != null) {
                        new EndEventTask().execute(broadcastId);
                    }
                }
                break;

            case REQUEST_AUTHORIZATION:
                break;

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Log.d(TAG, "handleSignInResult:personName " + personName);
                Log.d(TAG, "handleSignInResult:personGivenName " + personGivenName);
                Log.d(TAG, "handleSignInResult:personEmail " + personEmail);
                Log.d(TAG, "handleSignInResult:personId " + personId);
                Log.d(TAG, "handleSignInResult:personFamilyName " + personFamilyName);
                Log.d(TAG, "handleSignInResult:personPhoto " + personPhoto);

                AccountMail = personEmail;
                AccountName = personName;
                System.out.println("josh email = " + personEmail);
                System.out.println("josh name  = " + personName);

                startActivity(new Intent(LiveSelectActivity.this, YtbSettingActivitiy1.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                LiveSelectActivity.this.finish();
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }
    private void haveGooglePlayServices() {
        // check if there is already an account selected
        if (credential.getSelectedAccountName() == null) {
            // ask user to choose account
            chooseAccount();
        }
    }
    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }
    private boolean checkGooglePlayServicesAvailable() {
        final int connectionStatusCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        }
        return true;
    }
    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, LiveSelectActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }
    private class EndEventTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            progressDialog.dismiss();
        }
    }

}

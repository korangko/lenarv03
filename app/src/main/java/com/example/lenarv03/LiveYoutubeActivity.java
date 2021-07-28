package com.example.lenarv03;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lenarv03.utils.EventData;
import com.example.lenarv03.utils.Utils;
import com.example.lenarv03.utils.YouTubeApi;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.google.api.client.extensions.android.http.AndroidHttp.newCompatibleTransport;

public class LiveYoutubeActivity extends Activity implements View.OnClickListener {

    Context mainContext;
    TextView accountText;
    EditText liveTitleText, liveDescText;

    // to log in to google account
    private static final String TAG = "josh_action";
    GoogleSignInClient mGoogleSignInClient;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
    private static final int REQUEST_GMS_ERROR_DIALOG = 1;
    private static final int REQUEST_ACCOUNT_PICKER = 2;
    public static final int REQUEST_AUTHORIZATION = 3;
    private static final int REQUEST_STREAMER = 4;
    private static final int RC_SIGN_IN = 5;
    private String AccountName;
    // to make youtube stream
    public static final String APP_NAME = "Lenar_App";
    GoogleAccountCredential credential;
    final HttpTransport transport = newCompatibleTransport();
    final JsonFactory jsonFactory = new GsonFactory();
    public static String broadCastingUrl = null;
    public static EventData currentEvent;
    //alert dialog build
    AlertDialog dialog;
    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveyoutube);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mainContext = this;

        /**variables**/
        findViewById(R.id.menu_before_btn).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.live_start_btn).setOnClickListener(this);
        liveTitleText = findViewById(R.id.live_stream_title_text);
        liveDescText = findViewById(R.id.live_stream_desc_text);

        /**Google account**/
        accountText = findViewById(R.id.google_account_text);
        accountText.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        mHandler = new Handler(Looper.getMainLooper());
        AlertDialog.Builder builder = new AlertDialog.Builder(mainContext);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        dialog = builder.create();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_account_text:
                signIn();
                break;
            case R.id.menu_before_btn:
                startActivity(new Intent(LiveYoutubeActivity.this, LiveSelectActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                LiveYoutubeActivity.this.finish();
                break;
            case R.id.live_start_btn:

                //thread live create
                CreateYoutubeLive createLiveThread = new CreateYoutubeLive();
                createLiveThread.start();

                // async live create
//                credential = GoogleAccountCredential.usingOAuth2(
//                        getApplicationContext(), Arrays.asList(Utils.SCOPES));
//                credential.setBackOff(new ExponentialBackOff());
//                credential.setSelectedAccountName(AccountName);
//                new CreateLiveEventTask().execute();
                break;
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    //original
//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
                break;
            case REQUEST_GMS_ERROR_DIALOG:
                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    haveGooglePlayServices();
                } else {
                    checkGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null
                        && data.getExtras() != null) {
                    String accountName = data.getExtras().getString(
                            AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(AccountName);
                    }
                }
                break;
            case REQUEST_STREAMER:
                if (resultCode == Activity.RESULT_OK && data != null
                        && data.getExtras() != null) {
                    String broadcastId = data.getStringExtra(YouTubeApi.BROADCAST_ID_KEY);
                    if (broadcastId != null) {
                        new EndEventTask().execute(broadcastId);
                    }
                }
                break;

            case REQUEST_AUTHORIZATION:
                System.out.println("josh error authorization");
                break;

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
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    public void updateUI(GoogleSignInAccount account) {
        if (account != null) {
//            Toast.makeText(this, "Signed In successfully", Toast.LENGTH_SHORT).show();
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                AccountName = personEmail;
                accountText.setText(AccountName);
            } else {
                accountText.setText("Log In");
//                Toast.makeText(this, "Log In First", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void haveGooglePlayServices() {
        // check if there is already an account selected
        if (credential.getSelectedAccountName() == null) {
            // ask user to choose account
            chooseAccount();
        }
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
                        connectionStatusCode, LiveYoutubeActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER);
    }

    private class EndEventTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(LiveYoutubeActivity.this, null,
                    getResources().getText(R.string.endingEvent), true);
        }

        @Override
        protected Void doInBackground(String... params) {
            YouTube youtube = new YouTube.Builder(transport, jsonFactory,
                    credential).setApplicationName(APP_NAME)
                    .build();
            try {
                if (params.length >= 1) {
                    YouTubeApi.endEvent(youtube, params[0]);
                }
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                Log.e(MainActivity.APP_NAME, "", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            progressDialog.dismiss();
        }

    }

    public class CreateYoutubeLive extends Thread {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {  // 화면에 그려줄 작업
                    dialog.show();
                }
            });

            credential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(Utils.SCOPES));
            credential.setBackOff(new ExponentialBackOff());
            credential.setSelectedAccountName(AccountName);

            //to start youtube stream
            YouTube youtube = new YouTube.Builder(transport, jsonFactory,
                    credential).setApplicationName(APP_NAME)
                    .build();
            String date = new Date().toString();
//            if(liveTitleText.getText() != null){
//                YouTubeApi.createLiveEvent(youtube, "Event - " + date,
//                        liveTitleText.getText().toString());
//                System.out.println("josh live made1");
//
//            }else{
            YouTubeApi.createLiveEvent(youtube, "Event - " + date,
                    "Live stream by LENAR - " + date);
            System.out.println("josh live made2");
//            }
            try {
                YouTubeApi.getLiveEvents(youtube);
            } catch (IOException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }
    }

    private class CreateLiveEventTask extends
            AsyncTask<Void, Void, List<EventData>> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(LiveYoutubeActivity.this, null,
                    getResources().getText(R.string.creatingEvent), true);
        }

        @Override
        protected List<EventData> doInBackground(
                Void... params) {
            YouTube youtube = new YouTube.Builder(transport, jsonFactory,
                    credential).setApplicationName(APP_NAME)
                    .build();
            try {
                String date = new Date().toString();
                YouTubeApi.createLiveEvent(youtube, "Event - " + date,
                        "LENAR App LIVE STERAMING Testing - " + date);
                return YouTubeApi.getLiveEvents(youtube);

            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                Log.e(MainActivity.APP_NAME, "", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(
                List<EventData> fetchedEvents) {
            progressDialog.dismiss();
        }
    }

    private class StartEventTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(LiveYoutubeActivity.this, null,
                    getResources().getText(R.string.startingEvent), true);
        }

        @Override
        protected Void doInBackground(String... params) {
            YouTube youtube = new YouTube.Builder(transport, jsonFactory,
                    credential).setApplicationName(APP_NAME)
                    .build();
            try {
                YouTubeApi.startEvent(youtube, params[0]);
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                Log.e(MainActivity.APP_NAME, "", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            progressDialog.dismiss();
        }

    }
}

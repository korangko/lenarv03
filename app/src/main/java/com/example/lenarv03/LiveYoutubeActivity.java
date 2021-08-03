package com.example.lenarv03;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.lenarv03.utils.EventData;
import com.example.lenarv03.utils.RtmpSender;
import com.example.lenarv03.utils.Utils;
import com.example.lenarv03.utils.YouTubeApi;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.google.api.client.extensions.android.http.AndroidHttp.newCompatibleTransport;

public class LiveYoutubeActivity extends Activity implements View.OnClickListener {

    Context mainContext;
    TextView accountText;
    ImageView accountImage;
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
    GoogleSignInAccount account;
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
    //rtmp variables
    RtmpSender mRtmpsender = new RtmpSender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveyoutube);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mainContext = this;

        /**variables**/
        findViewById(R.id.menu_before_btn).setOnClickListener(this);
        findViewById(R.id.live_start_btn).setOnClickListener(this);
        findViewById(R.id.logout_btn).setOnClickListener(this);
        liveTitleText = findViewById(R.id.live_stream_title_text);
        liveDescText = findViewById(R.id.live_stream_desc_text);
        accountImage = findViewById(R.id.google_account_image);

        /**Google account**/
        accountText = findViewById(R.id.google_account_text);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

        /**creating loading dialog sequence **/
        mHandler = new Handler(Looper.getMainLooper());
        AlertDialog.Builder builder = new AlertDialog.Builder(mainContext);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        dialog = builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_before_btn:
                startActivity(new Intent(LiveYoutubeActivity.this, LiveSelectActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                LiveYoutubeActivity.this.finish();
                break;
            case R.id.live_start_btn:
                //thread live create
                CreateYoutubeLive createLiveThread = new CreateYoutubeLive();
                createLiveThread.start();
                break;
            case R.id.logout_btn:
                signOut();
                startActivity(new Intent(LiveYoutubeActivity.this, LiveSelectActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                LiveYoutubeActivity.this.finish();
                break;
        }

    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("josh logout");
                    }
                });
    }


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
            Toast.makeText(this, "Signed In successfully", Toast.LENGTH_SHORT).show();
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
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

                AccountName = personName;
                accountText.setText(personName);
                Glide.with(this)
                        .load(personPhoto)
                        .circleCrop()
                        .into(accountImage);

            } else {
                Toast.makeText(this, "Log In First", Toast.LENGTH_LONG).show();
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
            if(liveTitleText.getText() != null){
                YouTubeApi.createLiveEvent(youtube, "Event - " + date,
                        liveTitleText.getText().toString());

            }else{
            YouTubeApi.createLiveEvent(youtube, "Event - " + date,
                    "Live stream by " + AccountName);
            }
            try {
                YouTubeApi.getLiveEvents(youtube);
                System.out.println("josh got live events ");
            } catch (IOException e) {
                System.out.println("josh live events acquasition failed ");
                e.printStackTrace();
            }
            dialog.dismiss();
            mRtmpsender.broadcastStart(mainContext, broadCastingUrl);
            String broadcId = currentEvent.getId();
            new StartEventTask().execute(broadcId);
        }
    }

    private class StartEventTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 사용하고자 하는 코드
                    progressDialog = ProgressDialog.show(LiveYoutubeActivity.this, null,
                            getResources().getText(R.string.startingEvent), true);
                }
            }, 0);
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

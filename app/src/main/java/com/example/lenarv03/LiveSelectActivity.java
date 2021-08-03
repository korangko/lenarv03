package com.example.lenarv03;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.lenarv03.utils.YouTubeApi;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LiveSelectActivity extends Activity implements View.OnClickListener {

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
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if (account != null) {
                    startActivity(new Intent(LiveSelectActivity.this, LiveYoutubeActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    LiveSelectActivity.this.finish();
                }else{
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

    /**google account **/
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
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

                Log.d(TAG, "handleSignInResult:personName "+personName);
                Log.d(TAG, "handleSignInResult:personGivenName "+personGivenName);
                Log.d(TAG, "handleSignInResult:personEmail "+personEmail);
                Log.d(TAG, "handleSignInResult:personId "+personId);
                Log.d(TAG, "handleSignInResult:personFamilyName "+personFamilyName);
                Log.d(TAG, "handleSignInResult:personPhoto "+personPhoto);

                startActivity(new Intent(LiveSelectActivity.this, LiveYoutubeActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                LiveSelectActivity.this.finish();
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

}

package com.example.lenarv03.livestream;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.lenarv03.MainMenuActivity;
import com.example.lenarv03.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoginStatusCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

import static com.example.lenarv03.utils.YouTubeApi.AccountMail;
import static com.example.lenarv03.utils.YouTubeApi.AccountName;
import static com.example.lenarv03.utils.YouTubeApi.account;
import static com.example.lenarv03.utils.YouTubeApi.mGoogleSignInClient;

public class LiveSelectActivity extends Activity implements View.OnClickListener {

    int RC_SIGN_IN = 5;
    private static final String TAG = "Lenar TAG";
    //facebook live
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    private LoginStatusCallback mLoginCallback;
    private CallbackManager mCallbackManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveselect);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        findViewById(R.id.menu_before_btn).setOnClickListener(this);
        findViewById(R.id.youtube_selectbtn).setOnClickListener(this);
        findViewById(R.id.facebook_selectbtn).setOnClickListener(this);
        findViewById(R.id.twitch_selectbtn).setOnClickListener(this);

        //facebook test
        loginButton = (LoginButton)findViewById(R.id.login_button);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("User ID: " + loginResult.getAccessToken().getUserId() + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                System.out.println("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                System.out.println("Login attempt failed.");
            }

        });

        //test
        mCallbackManager = CallbackManager.Factory.create();
        mLoginCallback = new LoginStatusCallback() {
            @Override
            public void onCompleted(AccessToken accessToken) {

            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onError(Exception exception) {

            }
        };

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
                    startActivity(new Intent(LiveSelectActivity.this, LiveSettingActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    LiveSelectActivity.this.finish();
                }else{
                    signIn();
                }
                break;
            case R.id.facebook_selectbtn:
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.logInWithReadPermissions(LiveSelectActivity.this,
                        Arrays.asList("public_profile", "email"));
                loginManager.registerCallback(mCallbackManager, (FacebookCallback<LoginResult>) mLoginCallback);
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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

                AccountMail = personEmail;
                AccountName = personName;
                System.out.println("josh email = " +personEmail);
                System.out.println("josh name  = " +personName);

                startActivity(new Intent(LiveSelectActivity.this, LiveSettingActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
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

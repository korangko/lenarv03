package com.example.lenarv03.livestream;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.lenarv03.R;
import com.example.lenarv03.utils.Utils;
import com.example.lenarv03.utils.YouTubeApi;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.Date;

import static com.example.lenarv03.MainActivity.APP_NAME;
import static com.example.lenarv03.utils.YouTubeApi.AccountMail;
import static com.example.lenarv03.utils.YouTubeApi.AccountName;
import static com.example.lenarv03.utils.YouTubeApi.broadCastingUrl;
import static com.example.lenarv03.utils.YouTubeApi.broadcastPublic;
import static com.example.lenarv03.utils.YouTubeApi.credential;
import static com.example.lenarv03.utils.YouTubeApi.forKids;
import static com.example.lenarv03.utils.YouTubeApi.jsonFactory;
import static com.example.lenarv03.utils.YouTubeApi.mGoogleSignInClient;
import static com.example.lenarv03.utils.YouTubeApi.transport;


public class YtbSettingActivity2 extends Activity implements View.OnClickListener {

    Context mainContext;
    TextView accountText;
    EditText liveTitleText, liveDescText;
    CheckBox broadcastModeBox, kidContentBox;
    Dialog dialog01;
    TextView liveSetBtn;
    ConstraintLayout liveSetBtn2;
    private CreateYoutubeLive livethread;

    // to log in to google account
    private static final String TAG = "Lenar App";
    public static final int REQUEST_AUTHORIZATION = 3;
    boolean authConfirm = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytb_setting2);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mainContext = this;
        /**variables**/
        findViewById(R.id.menu_before_btn).setOnClickListener(this);
        findViewById(R.id.live_set_btn).setOnClickListener(this);
        liveTitleText = findViewById(R.id.live_stream_title_text);
        liveDescText = findViewById(R.id.live_stream_desc_text);
        liveSetBtn = findViewById(R.id.live_set_btn);
        liveSetBtn2 = findViewById(R.id.live_set_btn2);
        /**check box**/
        broadcastModeBox = findViewById(R.id.broadcast_mode_chkbox);
        kidContentBox = findViewById(R.id.kids_content_chkbox);
        /**Google account**/
        accountText = findViewById(R.id.google_account_text);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        /**live thread **/
        livethread = new CreateYoutubeLive();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_before_btn:
                livethread.interrupt();
                startActivity(new Intent(YtbSettingActivity2.this, LiveSelectActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                YtbSettingActivity2.this.finish();
                break;
            case R.id.live_set_btn:
//                liveSetBtn.setVisibility(View.GONE);
//                liveSetBtn2.setVisibility(View.VISIBLE);
                CreateYoutubeLive livethread = new CreateYoutubeLive();
                livethread.start();
                break;
        }
    }

    public void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                AccountMail = acct.getEmail();
                AccountName = personName;
                accountText.setText(AccountMail);
            } else {
                Toast.makeText(this, "Log In First", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class CreateYoutubeLive extends Thread {
        @Override
        public void run() {
            forKids = (kidContentBox.isChecked()) ? true : false;
            broadcastPublic = (broadcastModeBox.isChecked()) ? "private" : "public";
            credential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(Utils.SCOPES));
            credential.setBackOff(new ExponentialBackOff());
            credential.setSelectedAccountName(AccountMail);
            YouTube youtube = new YouTube.Builder(transport, jsonFactory,
                    credential).setApplicationName(APP_NAME)
                    .build();
            String date = new Date().toString();

            try {
                if (liveTitleText.getText().toString().equals("") || liveTitleText.getText().toString() == null) {
                    YouTubeApi.createLiveEvent(youtube, "Event - " + date,
                            "Live stream by " + AccountName);
                } else {
                    YouTubeApi.createLiveEvent(youtube, "Event - " + date,
                            liveTitleText.getText().toString());
                }
                YouTubeApi.getLiveEvents(youtube);
                Thread.sleep(500);
                // if okay with getting live info then -> authconfirm - ok - > next intent
                authConfirm = true;
            } catch (UserRecoverableAuthIOException e) {
                // 이부분이 있어야지 Auth error 가 발생하지 않는다. (Auth가 할당되지 않았을때 물어보는 부분)
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.out.println("josh interrupted");
                e.printStackTrace();
            }
            // if getting live events action is successful, then move to next intent
            if(authConfirm) {
                startActivity(new Intent(YtbSettingActivity2.this, LiveStreamActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                YtbSettingActivity2.this.finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        switch (requestCode) {
            case REQUEST_AUTHORIZATION:
                break;
        }
    }
}

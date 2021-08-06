package com.example.lenarv03.livestream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.lenarv03.R;
import com.example.lenarv03.utils.Utils;
import com.example.lenarv03.utils.YouTubeApi;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static com.example.lenarv03.MainActivity.APP_NAME;
import static com.example.lenarv03.utils.YouTubeApi.AccountMail;
import static com.example.lenarv03.utils.YouTubeApi.AccountName;
import static com.example.lenarv03.utils.YouTubeApi.broadcastPublic;
import static com.example.lenarv03.utils.YouTubeApi.credential;
import static com.example.lenarv03.utils.YouTubeApi.forKids;
import static com.example.lenarv03.utils.YouTubeApi.jsonFactory;
import static com.example.lenarv03.utils.YouTubeApi.mGoogleSignInClient;
import static com.example.lenarv03.utils.YouTubeApi.transport;


public class LiveSettingActivity extends Activity implements View.OnClickListener {

    Context mainContext;
    TextView accountText;
    ImageView accountImage;
    EditText liveTitleText, liveDescText;
    CheckBox broadcastModeBox, kidContentBox;

    // to log in to google account
    private static final String TAG = "Lenar App";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_setting);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mainContext = this;

        /**variables**/
        findViewById(R.id.menu_before_btn).setOnClickListener(this);
        findViewById(R.id.live_set_btn).setOnClickListener(this);
        findViewById(R.id.logout_btn).setOnClickListener(this);
        liveTitleText = findViewById(R.id.live_stream_title_text);
        liveDescText = findViewById(R.id.live_stream_desc_text);
        accountImage = findViewById(R.id.google_account_image);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_before_btn:
                startActivity(new Intent(LiveSettingActivity.this, LiveSelectActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                LiveSettingActivity.this.finish();
                break;
            case R.id.live_set_btn:
                CreateYoutubeLive livethread = new CreateYoutubeLive();
                livethread.start();
                break;
            case R.id.logout_btn:
                signOut();
                startActivity(new Intent(LiveSettingActivity.this, LiveSelectActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                LiveSettingActivity.this.finish();
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


    public void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                Uri personPhoto = acct.getPhotoUrl();

                AccountMail = acct.getEmail();
                AccountName = personName;
                System.out.println("josh email = " + AccountMail);
                System.out.println("josh name  = " + AccountName);
                accountText.setText(AccountMail);
                if(personPhoto == null){
                    Glide.with(this)
                            .load(R.drawable.default_profile_image)
                            .circleCrop()
                            .into(accountImage);
                }else{
                    Glide.with(this)
                            .load(personPhoto)
                            .circleCrop()
                            .into(accountImage);
                }

            } else {
                Toast.makeText(this, "Log In First", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class CreateYoutubeLive extends Thread {
//        GoogleAccountCredential credential;
//        HttpTransport transport = newCompatibleTransport();
//        JsonFactory jsonFactory = new GsonFactory();

        @Override
        public void run() {

            /** kid and private mode check**/
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
            /** setting live stream name or default name and create live stream event **/
            if (liveTitleText.getText().toString().equals("") || liveTitleText.getText().toString() == null) {
                System.out.println("no title is founded");
                YouTubeApi.createLiveEvent(youtube, "Event - " + date,
                        "Live stream by " + AccountName);
            } else {
                YouTubeApi.createLiveEvent(youtube, "Event - " + date,
                        liveTitleText.getText().toString());
            }
            /**update live events on your channel **/
            try {
                YouTubeApi.getLiveEvents(youtube);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(LiveSettingActivity.this, LiveStreamActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            LiveSettingActivity.this.finish();

        }
    }


}

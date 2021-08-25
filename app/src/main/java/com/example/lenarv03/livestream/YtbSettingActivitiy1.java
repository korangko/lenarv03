package com.example.lenarv03.livestream;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.lenarv03.MainMenuActivity;
import com.example.lenarv03.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.example.lenarv03.utils.YouTubeApi.AccountMail;
import static com.example.lenarv03.utils.YouTubeApi.AccountName;
import static com.example.lenarv03.utils.YouTubeApi.mGoogleSignInClient;

public class YtbSettingActivitiy1 extends Activity implements View.OnClickListener {

    TextView accountText;
    ImageView accountImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytb_setting1);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        accountText = findViewById(R.id.google_account_text);
        accountImage = findViewById(R.id.google_account_image);
        findViewById(R.id.menu_before_btn).setOnClickListener(this);
        findViewById(R.id.ok_btn).setOnClickListener(this);
        findViewById(R.id.not_ok_btn).setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }


    public void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                Uri personPhoto = acct.getPhotoUrl();
                AccountMail = acct.getEmail();
                AccountName = personName;
                accountText.setText(AccountMail);
                if (personPhoto == null) {
                    Glide.with(this)
                            .load(R.drawable.default_profile_image)
                            .circleCrop()
                            .into(accountImage);
                } else {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_before_btn:
                startActivity(new Intent(YtbSettingActivitiy1.this, LiveSelectActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                YtbSettingActivitiy1.this.finish();
                break;
            case R.id.ok_btn:
                startActivity(new Intent(YtbSettingActivitiy1.this, YtbSettingActivity2.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                YtbSettingActivitiy1.this.finish();
                break;
            case R.id.not_ok_btn:
                signOut();
                startActivity(new Intent(YtbSettingActivitiy1.this, LiveSelectActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                YtbSettingActivitiy1.this.finish();
                break;
        }

    }


    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }
}

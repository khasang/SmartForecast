package com.khasang.forecast.activities.etc;

/**
 * Created by qwsa on 08.06.16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appinvite.AppInviteReferral;
import com.khasang.forecast.R;

/**
 * Activity for displaying information about a receive App Invite invitation.  This activity
 * displays as a Dialog over the MainActivity and does not cover the full screen.
 */
public class DeepLinkActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = DeepLinkActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deep_link_activity);

        // Button click listener
        findViewById(R.id.button_ok).setOnClickListener(this);
    }

    // [START deep_link_on_start]
    @Override
    protected void onStart() {
        super.onStart();

        // Check if the intent contains an AppInvite and then process the referral information.
        Intent intent = getIntent();
        if (AppInviteReferral.hasReferral(intent)) {
            processReferralIntent(intent);
        }
    }
    // [END deep_link_on_start]

    // [START process_referral_intent]
    private void processReferralIntent(Intent intent) {
        // Extract referral information from the intent
        String invitationId = AppInviteReferral.getInvitationId(intent);
        String deepLink = AppInviteReferral.getDeepLink(intent);

        // Display referral information
        // [START_EXCLUDE]
        Log.d(TAG, "Found Referral: " + invitationId + ":" + deepLink);
        ((TextView) findViewById(R.id.deep_link_text))
                .setText(getString(R.string.deep_link_fmt, deepLink));
        ((TextView) findViewById(R.id.invitation_id_text))
                .setText(getString(R.string.invitation_id_fmt, invitationId));
        // [END_EXCLUDE]
    }
    // [END process_referral_intent]


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ok:
                finish();
                break;
        }
    }
}

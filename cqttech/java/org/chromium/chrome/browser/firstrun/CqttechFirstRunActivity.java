package org.chromium.chrome.browser.firstrun;

import static org.chromium.chrome.browser.firstrun.FirstRunActivityBase.EXTRA_CHROME_LAUNCH_INTENT;
import static org.chromium.chrome.browser.firstrun.FirstRunActivityBase.EXTRA_CHROME_LAUNCH_INTENT_IS_CCT;
import static org.chromium.chrome.browser.firstrun.FirstRunActivityBase.EXTRA_FIRST_RUN_ACTIVITY_RESULT;
import static org.chromium.chrome.browser.firstrun.FirstRunActivityBase.EXTRA_FIRST_RUN_COMPLETE;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.chromium.chrome.R;

import org.chromium.base.Callback;
import org.chromium.base.ActivityState;
import org.chromium.base.ApplicationStatus;
import org.chromium.base.Log;
import org.chromium.chrome.browser.UrlConstants;
import org.chromium.chrome.browser.base.BaseCqttechFullscreenActivity;
import org.chromium.chrome.browser.customtabs.CustomTabActivity;
import org.chromium.chrome.browser.customtabs.CustomTabsConnection;
import org.chromium.chrome.browser.net.spdyproxy.DataReductionProxySettings;
import org.chromium.chrome.browser.preferences.datareduction.DataReductionPromoUtils;
import org.chromium.chrome.browser.preferences.datareduction.DataReductionProxyUma;
import org.chromium.chrome.browser.util.IntentUtils;
import org.chromium.ui.text.SpanApplier;
import org.wwg.common.DisplayUtils;

public class CqttechFirstRunActivity extends BaseCqttechFullscreenActivity {
    private static final String TAG = "CqttechFirstRunActivity";

    @Nullable
    private Dialog mDialog = null;

    private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    private void showDialog() {
        if (mDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.cqttech_first_run_dialog, null);
            initAuthorRead(view);

            int margin = getResources().getDimensionPixelSize(R.dimen.cqttech_first_run_dialog_margin);
            Dialog dialog = new Dialog(this, R.style.CqttechFirstRunDialog);

            dialog.setCanceledOnTouchOutside(false);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            lp.width = DisplayUtils.getScreenMetricsWidth(this) - margin * 2;
            dialog.setContentView(view, lp);
            dialog.setOnDismissListener(d -> mDialog = null);
            dialog.setOnKeyListener((d, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);

            dialog.show();
            mDialog = dialog;
        }
    }

    private void initAuthorRead(View view) {
        int color = getResources().getColor(R.color.main_primary_color);
        FirstRunClickableSpan clickableTermsSpan = new FirstRunClickableSpan(color,
                result -> CustomTabActivity.showInfoPage(
                        this,
                        UrlConstants.CQTTECH_LICENSE));
        FirstRunClickableSpan clickablePrivacySpan = new FirstRunClickableSpan(color,
                result -> CustomTabActivity.showInfoPage(
                        this,
                        UrlConstants.CQTTECH_PRIVACY));
        FirstRunClickableSpan clickableSDK = new FirstRunClickableSpan(color,
                result -> CustomTabActivity.showInfoPage(
                        this,
                        UrlConstants.CQTTECH_SDK));
        CharSequence readText = SpanApplier.applySpans(
                getString(R.string.first_run_dialog_author_warning),
                new SpanApplier.SpanInfo("<LINK1>", "</LINK1>", clickableTermsSpan),
                new SpanApplier.SpanInfo("<LINK2>", "</LINK2>", clickablePrivacySpan),
                new SpanApplier.SpanInfo("<LINK3>", "</LINK3>", clickableSDK)
        );

        TextView hint = view.findViewById(R.id.first_run_hint);
        hint.setText(readText);
        hint.setMovementMethod(LinkMovementMethod.getInstance());

        view.findViewById(R.id.exist).setOnClickListener(v -> finish());
        view.findViewById(R.id.agree).setOnClickListener(v -> completeFirstRunExperience());
    }

    private void completeFirstRunExperience() {
        FirstRunFlowSequencer.markFlowAsCompleted(null, false);

        if (DataReductionPromoUtils.getDisplayedFreOrSecondRunPromo()) {
            if (DataReductionProxySettings.getInstance().isDataReductionProxyEnabled()) {
                DataReductionProxyUma
                        .dataReductionProxyUIAction(DataReductionProxyUma.ACTION_FRE_ENABLED);
                DataReductionPromoUtils.saveFrePromoOptOut(false);
            } else {
                DataReductionProxyUma
                        .dataReductionProxyUIAction(DataReductionProxyUma.ACTION_FRE_DISABLED);
                DataReductionPromoUtils.saveFrePromoOptOut(true);
            }
        }

        if (!sendPendingIntentIfNecessary()) {
            finish();
        } else {
            ApplicationStatus.registerStateListenerForAllActivities(new ApplicationStatus.ActivityStateListener() {
                @Override
                public void onActivityStateChange(Activity activity, int newState) {
                    boolean shouldFinish = false;
                    if (activity == CqttechFirstRunActivity.this) {
                        shouldFinish = (newState == ActivityState.STOPPED
                                || newState == ActivityState.DESTROYED);
                    } else {
                        shouldFinish = newState == ActivityState.RESUMED;
                    }
                    if (shouldFinish) {
                        finish();
                        ApplicationStatus.unregisterActivityStateListener(this);
                    }
                }
            });
        }
    }

    /**
     * Sends the PendingIntent included with the CHROME_LAUNCH_INTENT extra if it exists.
     * @return Whether a pending intent was sent.
     */
    private boolean sendPendingIntentIfNecessary() {
        PendingIntent pendingIntent =
                IntentUtils.safeGetParcelableExtra(getIntent(), EXTRA_CHROME_LAUNCH_INTENT);
        boolean pendingIntentIsCCT = IntentUtils.safeGetBooleanExtra(
                getIntent(), EXTRA_CHROME_LAUNCH_INTENT_IS_CCT, false);
        if (pendingIntent == null) return false;

        // Calling pending intent to report failure can result in UI flicker (crbug.com/788153).
        // Avoid doing that unless the intent is for custom tabs, in which case we don't have a
        // choice because we need to call "first run" CCT callback with the intent.
        // if (!pendingIntentIsCCT) return false;

        Intent extraDataIntent = new Intent();
        extraDataIntent.putExtra(EXTRA_FIRST_RUN_ACTIVITY_RESULT, true);
        extraDataIntent.putExtra(EXTRA_FIRST_RUN_COMPLETE, true);

        try {
            // After the PendingIntent has been sent, send a first run callback to custom tabs
            // if necessary.
            PendingIntent.OnFinished onFinished = (
                    penIntent, intent, resultCode, resultData, resultExtras
            ) -> CustomTabsConnection.getInstance().sendFirstRunCallbackIfNecessary(intent, true);

            // Use the PendingIntent to send the intent that originally launched Chrome. The intent
            // will go back to the ChromeLauncherActivity, which will route it accordingly.
            pendingIntent.send(this, Activity.RESULT_OK,
                    extraDataIntent,
                    onFinished, null);
            return true;
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, "Unable to send PendingIntent.", e);
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != mOrientation) {
            mOrientation = newConfig.orientation;
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }

            showDialog();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mOrientation = getRequestedOrientation();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cqttech_first_run_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showDialog();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || mDialog != null) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void finish() {
        if (mDialog != null) {
            mDialog.dismiss();
        }

        super.finish();
    }

    private static class FirstRunClickableSpan extends ClickableSpan {
        private final Callback<Void> mClickedListener;
        @ColorInt
        private final int mColor;

        public FirstRunClickableSpan(@ColorInt int color, Callback<Void> clickedListener) {
            mColor = color;
            mClickedListener = clickedListener;
        }

        @Override
        public void onClick(@NonNull View widget) {
            mClickedListener.onResult(null);
        }

        // Disable underline on the link text.
        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setColor(mColor);
            //ds.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }
}

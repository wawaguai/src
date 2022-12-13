// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chrome.browser.preferences;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.ContentSettingsType;
import org.chromium.components.location.LocationUtils;
import org.chromium.base.ContextUtils;
import org.chromium.chrome.browser.RestartWorker;
import org.wwg.common.ThemeConfig;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.SharedPreferences;

/**
* A custom adapter for listing search engines.
*/
public class AppearanceAdapter extends BaseAdapter implements OnClickListener {
    private static final String TAG = "cr_Appearances";

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_DIVIDER = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private List<String> mPrepopulatedAppearances = new ArrayList<>();
    private List<String> mPrepopulatedNames = new ArrayList<>();
    private List<String> mPrepopulatedDescriptions = new ArrayList<>();

    private int mSelectedAppearancePosition = -1;

    /** The position of the default search engine before user's action. */
    private int mInitialAppearancePosition = -1;

    /** The current context. */
    private Context mContext;

    /** The layout inflater to use for the custom views. */
    private LayoutInflater mLayoutInflater;

    /**
     * Construct a AppearanceAdapter.
     * @param context The current context.
     */
    public AppearanceAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Start the adapter to gather the available search engines and listen for updates.
     */
    public void start() {
      refreshData();
    }

    /**
     * Stop the adapter from listening for future search engine updates.
     */
    public void stop() {
    }

    /**
     * Initialize the search engine list.
     */
    private void refreshData() {
        mPrepopulatedAppearances = new ArrayList<>();
        mPrepopulatedDescriptions = new ArrayList<>();
        mPrepopulatedAppearances.add("Default");
        mPrepopulatedNames.add(mContext.getString(R.string.theme_default));
        mPrepopulatedDescriptions.add(mContext.getString(R.string.theme_default_description));

        mPrepopulatedAppearances.add("Diamond Black");
        mPrepopulatedNames.add(mContext.getString(R.string.theme_black));
        mPrepopulatedDescriptions.add(mContext.getString(R.string.theme_black_description));

        mPrepopulatedAppearances.add("Ultra White");
        mPrepopulatedNames.add(mContext.getString(R.string.theme_white));
        mPrepopulatedDescriptions.add(mContext.getString(R.string.theme_white_description));
        mSelectedAppearancePosition = 0;
        String activeTheme = ContextUtils.getAppSharedPreferences().getString("active_theme", "");
        for (int i = 0; i < mPrepopulatedAppearances.size(); ++i) {
          if (mPrepopulatedAppearances.get(i).equals(activeTheme)) {
             mSelectedAppearancePosition = i;
          }
        }
        notifyDataSetChanged();
    }

    // BaseAdapter:

    @Override
    public int getCount() {
        return mPrepopulatedAppearances.size();
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public Object getItem(int pos) {
        return mPrepopulatedAppearances.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        int itemViewType = getItemViewType(position);
        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.search_engine, null);
        }
        if (itemViewType == VIEW_TYPE_DIVIDER) {
            return view;
        }

        view.setOnClickListener(this);
        view.setTag(position);

        // TODO(finnur): There's a tinting bug in the AppCompat lib (see http://crbug.com/474695),
        // which causes the first radiobox to always appear selected, even if it is not. It is being
        // addressed, but in the meantime we should use the native RadioButton instead.
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.radiobutton);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Resources resources = view.getResources();
            ColorStateList colorState =
                    ApiCompatibilityUtils.getColorStateList(resources, R.color.dark_radio_tint);
            if (ThemeConfig.getInstance().isDark()) {
                colorState = ApiCompatibilityUtils.getColorStateList(resources, R.color.light_radio_tint);
            }
            radioButton.setButtonTintList(colorState);
        }
        // On Lollipop this removes the redundant animation ring on selection but on older versions
        // it would cause the radio button to disappear.
        // TODO(finnur): Remove the encompassing if statement once we go back to using the AppCompat
        // control.
        final boolean selected = position == mSelectedAppearancePosition;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            radioButton.setBackgroundResource(0);
        }
        radioButton.setChecked(selected);

        TextView description = (TextView) view.findViewById(R.id.name);
        Resources resources = mContext.getResources();

        description.setTextColor(ThemeConfig.getInstance().getMainTextColor());
        description.setText(mPrepopulatedNames.get(position));

        TextView url = (TextView) view.findViewById(R.id.url);
        url.setText(mPrepopulatedDescriptions.get(position));

        view.findViewById(R.id.location_permission).setVisibility(View.GONE);

        radioButton.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        description.setAccessibilityDelegate(new AccessibilityDelegate() {
            @Override
            public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                super.onInitializeAccessibilityEvent(host, event);
                event.setChecked(selected);
            }

            @Override
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setCheckable(true);
                info.setChecked(selected);
            }
        });

        return view;
    }

    // OnClickListener:

    @Override
    public void onClick(View view) {
        appearanceSelected((int) view.getTag());
    }

    private String appearanceSelected(int position) {
        // Record the change in search engine.
        mSelectedAppearancePosition = position;
        SharedPreferences.Editor sharedPreferencesEditor = ContextUtils.getAppSharedPreferences().edit();
        sharedPreferencesEditor.putString("active_theme", mPrepopulatedAppearances.get(position));
        sharedPreferencesEditor.apply();
        AskForRelaunch();
        notifyDataSetChanged();
        return mPrepopulatedAppearances.get(position);
    }

    private void AskForRelaunch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
         alertDialogBuilder
            .setMessage(R.string.preferences_restart_is_needed)
            .setCancelable(true)
            .setPositiveButton(R.string.preferences_restart_now, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,int id) {
                  RestartWorker restartWorker = new RestartWorker();
                  restartWorker.Restart();
                  dialog.cancel();
              }
            })
            .setNegativeButton(R.string.preferences_restart_later,new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog,int id) {
                  dialog.cancel();
              }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
    }
}

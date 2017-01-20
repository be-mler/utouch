/*
 * Copyright (C) 2017 be_mler_
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.bemler;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.SettingsPreferenceFragment;

import cyanogenmod.hardware.CMHardwareManager;
import cyanogenmod.providers.CMSettings;

import org.cyanogenmod.internal.logging.CMMetricsLogger;

import java.util.ArrayList;
import java.util.List;

public class UTouch extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String TAG = "SystemSettings";

    // Available custom actions to perform on a key press.
    // Must match values for KEY_HOME_LONG_PRESS_ACTION in:
    // frameworks/base/core/java/android/provider/Settings.java
    private static final int ACTION_NOTHING = 0;
    private static final int ACTION_MENU = 1;
    private static final int ACTION_APP_SWITCH = 2;
    private static final int ACTION_SEARCH = 3;
    private static final int ACTION_VOICE_SEARCH = 4;
    private static final int ACTION_IN_APP_SEARCH = 5;
    private static final int ACTION_LAUNCH_CAMERA = 6;
    private static final int ACTION_SLEEP = 7;
    private static final int ACTION_LAST_APP = 8;

    private static final String KEY_FP_LEFT_SWIPE = "utouch_settings_left_swipe";
    private static final String KEY_FP_RIGHT_SWIPE = "utouch_settings_right_swipe";
    private static final String KEY_HOME_DOUBLE_TAP = "hardware_keys_home_double_tap";
    private static final String KEY_APP_SWITCH_PRESS = "hardware_keys_app_switch_press";

    private ListPreference mFpRightSwipeAction;
    private ListPreference mFpLeftSwipeAction;
    private ListPreference mHomeDoubleTapAction;
    private ListPreference mAppSwitchPressAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.utouch_settings);

        final Resources res = getResources();
        final ContentResolver resolver = getActivity().getContentResolver();

        // Right swipe
        int defaultFpRightSwipe = res.getInteger(
                com.android.internal.R.integer.config_fpRightSwipe);

        if (defaultFpRightSwipe < ACTION_NOTHING ||
                defaultFpRightSwipe > ACTION_LAST_APP) {
            defaultFpRightSwipe = ACTION_NOTHING;
        }

        int fpRightSwipeAction = CMSettings.System.getInt(resolver,
                CMSettings.System.KEY_FP_RIGHT_SWIPE_ACTION, defaultFpRightSwipe);
        mFpRightSwipeAction = initActionList(KEY_FP_RIGHT_SWIPE, fpRightSwipeAction);

        // Left swipe
        int defaultFpLeftSwipe = res.getInteger(
                com.android.internal.R.integer.config_fpLeftSwipe);

        if (defaultFpLeftSwipe < ACTION_NOTHING ||
                defaultFpLeftSwipe > ACTION_LAST_APP) {
            defaultFpLeftSwipe = ACTION_NOTHING;
        }

        int fpLeftSwipeAction = CMSettings.System.getInt(resolver,
                CMSettings.System.KEY_FP_LEFT_SWIPE_ACTION, defaultFpLeftSwipe);
        mFpLeftSwipeAction = initActionList(KEY_FP_LEFT_SWIPE, fpLeftSwipeAction);

        // Home double tap (double press)
        int defaultDoubleTapAction = res.getInteger(
                com.android.internal.R.integer.config_doubleTapOnHomeBehavior);

        if (defaultDoubleTapAction < ACTION_NOTHING ||
                defaultDoubleTapAction > ACTION_LAST_APP) {
            defaultDoubleTapAction = ACTION_NOTHING;
        }

        int doubleTapAction = CMSettings.System.getInt(resolver,
                CMSettings.System.KEY_HOME_DOUBLE_TAP_ACTION,
                defaultDoubleTapAction);
        mHomeDoubleTapAction = initActionList(KEY_HOME_DOUBLE_TAP, doubleTapAction);

        // App switch (long tap)
        int pressAction = CMSettings.System.getInt(resolver,
                CMSettings.System.KEY_APP_SWITCH_ACTION, ACTION_APP_SWITCH);
        mAppSwitchPressAction = initActionList(KEY_APP_SWITCH_PRESS, pressAction);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private ListPreference initActionList(String key, int value) {
        ListPreference list = (ListPreference) getPreferenceScreen().findPreference(key);
        if (list == null) return null;
        list.setValue(Integer.toString(value));
        list.setSummary(list.getEntry());
        list.setOnPreferenceChangeListener(this);
        return list;
    }

    private void handleActionListChange(ListPreference pref, Object newValue, String setting) {
        String value = (String) newValue;
        int index = pref.findIndexOfValue(value);
        pref.setSummary(pref.getEntries()[index]);
        CMSettings.System.putInt(getContentResolver(), setting, Integer.valueOf(value));
    }

    @Override
    protected int getMetricsCategory() {
        return CMMetricsLogger.BUTTON_SETTINGS;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mFpRightSwipeAction) {
            handleActionListChange(mFpRightSwipeAction, newValue,
                    CMSettings.System.KEY_FP_RIGHT_SWIPE_ACTION);
            return true;
        } else if (preference == mFpLeftSwipeAction) {
            handleActionListChange(mFpLeftSwipeAction, newValue,
                    CMSettings.System.KEY_FP_LEFT_SWIPE_ACTION);
            return true;
        } else if (preference == mHomeDoubleTapAction) {
            handleActionListChange(mHomeDoubleTapAction, newValue,
                    CMSettings.System.KEY_HOME_DOUBLE_TAP_ACTION);
            return true;
        } else if (preference == mAppSwitchPressAction) {
            handleActionListChange(mAppSwitchPressAction, newValue,
                    CMSettings.System.KEY_APP_SWITCH_ACTION);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.button_settings;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    final List<String> keys = new ArrayList<String>();
                    return keys;
                }
            };
}

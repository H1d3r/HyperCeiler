/*
 * This file is part of HyperCeiler.

 * HyperCeiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.

 * Copyright (C) 2023-2025 HyperCeiler Contributions
 */
package com.sevtinge.hyperceiler.ui.hooker.systemui;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.sevtinge.hyperceiler.dashboard.DashboardFragment;
import com.sevtinge.hyperceiler.ui.R;

import fan.preference.ColorPickerPreference;
import fan.preference.DropDownPreference;
import fan.preference.SeekBarPreferenceCompat;

public class MediaCardSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    SwitchPreference mRemoveMediaCardBackFix;
    SwitchPreference mRemoveMediaCardBack;
    DropDownPreference mProgressMode;
    SeekBarPreferenceCompat mProgressModeThickness;
    SeekBarPreferenceCompat mProgressModeCornerRadius;
    ColorPickerPreference mSliderColor;
    ColorPickerPreference mProgressBarColor;

    @Override
    public int getPreferenceScreenResId() {
        return R.xml.system_ui_control_center_media_cards;
    }

    @Override
    public void initPrefs() {
        mRemoveMediaCardBackFix = findPreference("prefs_key_system_ui_control_center_media_control_panel_background_mix");
        mRemoveMediaCardBack = findPreference("prefs_key_system_ui_control_center_remove_media_control_panel_background");
        mProgressMode = findPreference("prefs_key_system_ui_control_center_media_control_progress_mode");
        mProgressModeThickness = findPreference("prefs_key_system_ui_control_center_media_control_progress_thickness");
        mProgressModeCornerRadius = findPreference("prefs_key_system_ui_control_center_media_control_progress_corner_radius");
        mSliderColor = findPreference("prefs_key_system_ui_control_center_media_control_seekbar_thumb_color");
        mProgressBarColor = findPreference("prefs_key_system_ui_control_center_media_control_seekbar_color");

        mProgressModeThickness.setVisible(Integer.parseInt(getSharedPreferences().getString("prefs_key_system_ui_control_center_media_control_progress_mode", "0")) == 2);
        mProgressModeCornerRadius.setVisible(Integer.parseInt(getSharedPreferences().getString("prefs_key_system_ui_control_center_media_control_progress_mode", "0")) == 2);

        mSliderColor.setVisible(Integer.parseInt(getSharedPreferences().getString("prefs_key_system_ui_control_center_media_control_progress_mode", "0")) != 2);
        mProgressBarColor.setVisible(Integer.parseInt(getSharedPreferences().getString("prefs_key_system_ui_control_center_media_control_progress_mode", "0")) != 2);


        mRemoveMediaCardBackFix.setOnPreferenceChangeListener((preference, o) -> {
            if (!(boolean) o) {
                mRemoveMediaCardBack.setChecked(false);
            }
            return true;
        });

        mProgressMode.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object o) {
        if (preference == mProgressMode) {
            setCanBeVisibleProgressMode(Integer.parseInt((String) o));
        }
        return true;
    }

    private void setCanBeVisibleProgressMode(int mode) {
        mProgressModeThickness.setVisible(mode == 2);
        mProgressModeCornerRadius.setVisible(mode == 2);
        mSliderColor.setVisible(mode != 2);
        mProgressBarColor.setVisible(mode != 2);
    }
}

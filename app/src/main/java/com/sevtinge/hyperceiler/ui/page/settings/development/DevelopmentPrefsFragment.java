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
package com.sevtinge.hyperceiler.ui.page.settings.development;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.preference.Preference;

import com.sevtinge.hyperceiler.dashboard.SettingsPreferenceFragment;
import com.sevtinge.hyperceiler.hook.utils.prefs.PrefsUtils;
import com.sevtinge.hyperceiler.ui.R;

import java.util.Arrays;
import java.util.HashSet;

import fan.appcompat.app.AlertDialog;

public class DevelopmentPrefsFragment extends SettingsPreferenceFragment {
    Preference mType;
    Preference mClean;

    public interface EditDialogCallback {
        void onInputReceived(String userInput);
    }

    @Override
    public int getPreferenceScreenResId() {
        return R.xml.prefs_development_prefs;
    }

    @Override
    public void initPrefs() {
        mType = findPreference("prefs_key_development_prefs_check");
        mClean = findPreference("prefs_key_development_prefs_clean");
        assert mType != null;
        assert mClean != null;
        mClean.setOnPreferenceClickListener(
            preference -> {
                showInDialog(
                    userInput -> {
                        if (cleanKey(userInput)) {
                            showOutDialog(getResources().getString(R.string.prefs_2) + userInput);
                        } else {
                            showOutDialog(getResources().getString(R.string.prefs_3) + userInput);
                        }
                    }
                );
                return true;
            });
        mType.setOnPreferenceClickListener(
            preference -> {
                showInDialog(
                    userInput -> {
                        if (hasKey(userInput)) {
                            Object prefs = getPrefs(userInput, false,
                                getType("prefs_key_development_prefs_type"));
                            // https://stackoverflow.com/a/78608931
                            //noinspection IfCanBeSwitch
                            if (prefs == null) return;
                            if (prefs instanceof String) {
                                showOutDialog((String) prefs);
                            } else if (prefs instanceof Integer) {
                                showOutDialog(Integer.toString((int) prefs));
                            } else if (prefs instanceof Boolean) {
                                showOutDialog(Boolean.toString((Boolean) prefs));
                            } else if (prefs instanceof HashSet<?>) {
                                showOutDialog(Arrays.toString(((HashSet<?>) prefs).toArray()));
                            }
                        } else {
                            showOutDialog(getResources().getString(R.string.prefs_1) + userInput);
                        }
                    }
                );
                return true;
            }
        );
    }

    private int getType(String key) {
        return Integer.parseInt((String) getPrefs(key, true, 0));
    }

    private Object getPrefs(String key, boolean type, int needType) {
        if (type) return PrefsUtils.mSharedPreferences.getString(key, "null");
        try {
            switch (needType) {
                case 0 -> {
                    return PrefsUtils.mSharedPreferences.getInt(key, -1);
                }
                case 1 -> {
                    return PrefsUtils.mSharedPreferences.getString(key, "null");
                }
                case 2 -> {
                    return Integer.parseInt(PrefsUtils.mSharedPreferences.getString(key, "-1"));
                }
                case 3 -> {
                    return PrefsUtils.mSharedPreferences.getStringSet(key, new HashSet<>());
                }
                case 4 -> {
                    return PrefsUtils.mSharedPreferences.getBoolean(key, false);
                }
                default -> {
                    return null;
                }
            }
        } catch (Throwable throwable) {
            showOutDialog(getResources().getString(R.string.prefs_4) + "\n E: " + throwable.getMessage());
            return null;
        }
    }

    private void showInDialog(EditDialogCallback callback) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_dialog, null);
        EditText input = view.findViewById(R.id.title);
        new AlertDialog.Builder(getActivity())
            .setTitle(R.string.edit_key)
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                String userInput = input.getText().toString();
                if (userInput.isEmpty()) {
                    dialog.dismiss();
                    showInDialog(callback);
                    return;
                }
                callback.onInputReceived(userInput);
                dialog.dismiss();
            })
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
            .show();
    }

    private void showOutDialog(String show) {
        new AlertDialog.Builder(getActivity())
            .setCancelable(false)
            .setTitle(R.string.edit_out)
            .setMessage(show)
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }
}

package com.sevtinge.hyperceiler.dashboard.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sevtinge.hyperceiler.ui.R;

import fan.appcompat.app.AppCompatActivity;

public class SettingsBaseActivity extends AppCompatActivity implements ActivityCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        applyGrayScaleFilter(this);
        registerObserver(getApplicationContext());
        createUiFromIntent(savedInstanceState, intent);
    }

    protected void createUiFromIntent(Bundle savedInstanceState, Intent intent) {
        setContentView(R.layout.settings_sub);
        String initialFragmentName = getInitialFragmentName(intent);
        if (!TextUtils.isEmpty(initialFragmentName)) {
            Fragment targetFragment = getTargetFragment(this, initialFragmentName, savedInstanceState);
            if (targetFragment != null) {
                targetFragment.setArguments(getArguments(intent));
                setFragment(targetFragment);
            }
        }
    }


    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frame_content, fragment)
            .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}

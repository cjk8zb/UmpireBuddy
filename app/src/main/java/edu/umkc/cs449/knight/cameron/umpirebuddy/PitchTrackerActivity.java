package edu.umkc.cs449.knight.cameron.umpirebuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.StringRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class PitchTrackerActivity extends AppCompatActivity implements ActionMode.Callback, TextToSpeech.OnInitListener, View.OnLongClickListener {
    private static final String TAG = PitchTrackerActivity.class.getSimpleName();

    private static final String KEY_BALL_COUNT = "ball_count";
    private static final String KEY_STRIKE_COUNT = "strike_count";
    private static final String KEY_OUT_COUNT = "out_count";

    private static final int MAX_BALLS = 4;
    private static final int MAX_STRIKES = 3;

    private int mBallCount;
    private int mOutCount;
    private int mStrikeCount;

    private ActionMode mActionMode;
    private Button mBallButton;
    private Button mStrikeButton;
    private TextToSpeech mTextToSpeech;
    private TextView mBallsTextView;
    private TextView mStrikesTextView;
    private TextView mOutsTextView;

    /**
     * extends AppCompatActivity
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_tracker);
        findViewById(R.id.relative_layout).setOnLongClickListener(this);

        if (savedInstanceState != null) {
            mBallCount = savedInstanceState.getInt(KEY_BALL_COUNT);
            mStrikeCount = savedInstanceState.getInt(KEY_STRIKE_COUNT);
        }
        mOutCount = PreferenceManager.getDefaultSharedPreferences(this).getInt(KEY_OUT_COUNT, 0);

        mBallButton = (Button) findViewById(R.id.ball_button);
        mStrikeButton = (Button) findViewById(R.id.strike_button);
        mBallsTextView = (TextView) findViewById(R.id.balls_text_view);
        mStrikesTextView = (TextView) findViewById(R.id.strikes_text_view);
        mOutsTextView = (TextView) findViewById(R.id.outs_text_view);

        mBallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ball();
            }
        });

        mStrikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strike();
            }
        });

        updateUI();

        mTextToSpeech = new TextToSpeech(this, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_STRIKE_COUNT, mStrikeCount);
        outState.putInt(KEY_BALL_COUNT, mBallCount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pitch_tracker, menu);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            applyMenuColor(actionBar.getThemedContext(), menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_about:
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                return true;
            case R.id.menu_item_reset:
                reset();
                return true;
            case R.id.menu_item_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * helper methods
     **/

    private void applyMenuColor(Context context, Menu menu) {
        if (context == null || menu == null) {
            return;
        }

        final TypedValue typedValue = new TypedValue();
        if (!context.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)) {
            return;
        }
        Integer textColorId = typedValue.resourceId;

        for (int i = 0; i < menu.size(); ++i) {
            MenuItem menuItem = menu.getItem(i);
            Drawable drawable = menuItem.getIcon();
            if (drawable == null) {
                continue;
            }

            drawable = DrawableCompat.wrap(drawable);
            drawable.mutate();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                DrawableCompat.setTint(drawable, getResources().getColor(textColorId, null));
            } else {
                //noinspection deprecation
                DrawableCompat.setTint(drawable, getResources().getColor(textColorId));
            }
            menuItem.setIcon(drawable);
        }
    }

    /**
     * interface ActionMode.Callback
     **/

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.contextual, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contextual_ball:
                ball();
                mode.finish();
                return true;
            case R.id.contextual_strike:
                strike();
                mode.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
    }

    /**
     * interface TextToSpeech.OnInitListener
     **/

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            Log.e(TAG, "Could not initialize TextToSpeech.");
            return;
        }

        int result = mTextToSpeech.setLanguage(Locale.US);
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e(TAG, "Language is not available.");
        }
    }

    /**
     * interface View.OnLongClickListener
     **/

    @Override
    public boolean onLongClick(View v) {

        if (mActionMode != null) {
            return false;
        }

        mActionMode = this.startActionMode(this);
        v.setSelected(true);
        return true;
    }

    private void ball() {
        mBallCount++;
        updateUI();
    }

    private void strike() {
        mStrikeCount++;
        updateUI();
    }

    private void reset() {
        mBallCount = 0;
        mStrikeCount = 0;
        mStrikeButton.setEnabled(true);
        mBallButton.setEnabled(true);
        updateUI();
    }

    private void updateUI() {
        if (mBallCount >= MAX_BALLS) {
            showAlert(R.string.max_balls_message, R.string.max_balls_confirm);
        } else if (mStrikeCount >= MAX_STRIKES) {
            mOutCount++;
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putInt(KEY_OUT_COUNT, mOutCount)
                    .apply();
            showAlert(R.string.max_strikes_message, R.string.max_strikes_confirm);
        }

        mStrikesTextView.setText(String.format(getString(R.string.count_format), mStrikeCount));
        mBallsTextView.setText(String.format(getString(R.string.count_format), mBallCount));
        mOutsTextView.setText(String.format(getString(R.string.count_format), mOutCount));
    }

    private void showAlert(@StringRes int messageId, @StringRes int textId) {
        mStrikeButton.setEnabled(false);
        mBallButton.setEnabled(false);

        say(getString(messageId) + getString(textId));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageId)
                .setTitle(R.string.alert_title)
                .setPositiveButton(textId, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        reset();
                    }
                })
                .show();
    }

    private void say(String message) {
        boolean announce = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(this.getString(R.string.preference_announce_key), false);
        if (announce) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTextToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                //noinspection deprecation
                mTextToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

}

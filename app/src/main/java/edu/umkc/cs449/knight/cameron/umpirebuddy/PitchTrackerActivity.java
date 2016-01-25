package edu.umkc.cs449.knight.cameron.umpirebuddy;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PitchTrackerActivity extends AppCompatActivity {

    private static final String KEY_BALL_COUNT = "ball_count";
    private static final String KEY_STRIKE_COUNT = "strike_count";

    private static final int MAX_BALLS = 4;
    private static final int MAX_STRIKES = 3;

    private int mBallCount;
    private int mStrikeCount;

    private Button mBallButton;
    private Button mStrikeButton;
    private TextView mBallsTextView;
    private TextView mStrikesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_tracker);

        if (savedInstanceState != null) {
            mBallCount = savedInstanceState.getInt(KEY_BALL_COUNT);
            mStrikeCount = savedInstanceState.getInt(KEY_STRIKE_COUNT);
        }

        mBallButton = (Button) findViewById(R.id.ball_button);
        mStrikeButton = (Button) findViewById(R.id.strike_button);
        mBallsTextView = (TextView) findViewById(R.id.balls_text_view);
        mStrikesTextView = (TextView) findViewById(R.id.strikes_text_view);

        mBallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBallCount++;
                updateUI();
            }
        });

        mStrikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStrikeCount++;
                updateUI();
            }
        });

        updateUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_STRIKE_COUNT, mStrikeCount);
        outState.putInt(KEY_BALL_COUNT, mBallCount);
    }

    private void showAlert(@StringRes int messageId, @StringRes int textId) {
        mStrikeButton.setEnabled(false);
        mBallButton.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageId)
                .setTitle(R.string.alert_title)
                .setPositiveButton(textId, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mBallCount = 0;
                        mStrikeCount = 0;
                        mStrikeButton.setEnabled(true);
                        mBallButton.setEnabled(true);
                        updateUI();
                    }
                })
                .show();
    }

    private void updateUI() {
        if (mBallCount >= MAX_BALLS) {
            showAlert(R.string.max_balls_message, R.string.max_balls_confirm);
        } else if (mStrikeCount >= MAX_STRIKES) {
            showAlert(R.string.max_strikes_message, R.string.max_strikes_confirm);
        }

        mStrikesTextView.setText(String.format(getString(R.string.count_format), mStrikeCount));
        mBallsTextView.setText(String.format(getString(R.string.count_format), mBallCount));
    }
}

package com.bignerdranch.android.geoquiz;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {


    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_ANSWERED = "answered";
    private static final String KEY_ANSWERS = "answers";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private boolean mIsCheater;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private boolean[] mIsAnsweredBank = {false, false, false, false, false, false};
    private boolean[] mAnswerBank = {false, false, false, false, false, false};
    private int mCurrentIndex = 0;
    private int mRightAnswerCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            if (savedInstanceState.getBooleanArray(KEY_ANSWERED) != null) {
                mIsAnsweredBank = savedInstanceState.getBooleanArray(KEY_ANSWERED);
            }
            if (savedInstanceState.getBooleanArray(KEY_ANSWERS) != null) {
                mAnswerBank = savedInstanceState.getBooleanArray(KEY_ANSWERS);
            }

        }

        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = mCurrentIndex - 1;
                if (mCurrentIndex < 0) {
                    mCurrentIndex = mCurrentIndex + mQuestionBank.length;
                }
                mIsCheater = false;
                updateQuestion();
            }
        });
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);

            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAnswer(false);
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start CheatActivity
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        boolean answered = mIsAnsweredBank[mCurrentIndex];


        mTrueButton.setEnabled(!answered);
        mFalseButton.setEnabled(!answered);

        if (answered) {
            if (mAnswerBank[mCurrentIndex]) {
                if (mQuestionBank[mCurrentIndex].isAnswerTrue()) {
                    mTrueButton.setTextColor(getResources().getColor(R.color.rightAnswer));
                    mFalseButton.setTextColor(getResources().getColor(R.color.default_text));
                } else {
                    mTrueButton.setTextColor(getResources().getColor(R.color.wrongAnswer));
                    mFalseButton.setTextColor(getResources().getColor(R.color.default_text));
                }
            } else {
                if (!mQuestionBank[mCurrentIndex].isAnswerTrue()) {
                    mFalseButton.setTextColor(getResources().getColor(R.color.rightAnswer));
                    mTrueButton.setTextColor(getResources().getColor(R.color.default_text));
                } else {
                    mFalseButton.setTextColor(getResources().getColor(R.color.wrongAnswer));
                    mTrueButton.setTextColor(getResources().getColor(R.color.default_text));
                }
            }
        } else {
            mTrueButton.setTextColor(getResources().getColor(R.color.default_text));
            mFalseButton.setTextColor(getResources().getColor(R.color.default_text));
        }

        if (isAllTrue(mIsAnsweredBank)) {
            Toast.makeText(this, "You've made " + mRightAnswerCounter + " of " +
                    mQuestionBank.length, Toast.LENGTH_SHORT).show();
        }

    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mRightAnswerCounter++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        mIsAnsweredBank[mCurrentIndex] = true;
        mAnswerBank[mCurrentIndex] = userPressedTrue;
        updateQuestion();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: put " + mCurrentIndex + " to saved state");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putBooleanArray(KEY_ANSWERED, mIsAnsweredBank);
        outState.putBooleanArray(KEY_ANSWERS, mAnswerBank);
    }

    private boolean isAllTrue(boolean[] array) {
        for (boolean b : array) {
            if (!b) return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

}

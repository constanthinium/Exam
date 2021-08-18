package com.constanthinium.exam

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

private const val EXTRA_TEST = "test"

class PassTestActivity : AppCompatActivity(), View.OnClickListener, DialogInterface.OnClickListener,
    TextView.OnEditorActionListener, DialogInterface.OnCancelListener {

    private lateinit var test: Test
    private lateinit var answerEditText: EditText
    private lateinit var vibrator: Vibrator
    private lateinit var questionTextView: TextView

    private lateinit var correctPlayer: MediaPlayer
    private lateinit var incorrectPlayer: MediaPlayer

    private lateinit var correctVibration: VibrationEffect
    private lateinit var incorrectVibration: VibrationEffect

    private var currentQuestionIndex = 0
    private var score = 0
    private val incorrectQuestions = ArrayList<QuestionAndAnswer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_test)
        test = intent.getSerializableExtra(EXTRA_TEST) as Test
        test.questions.shuffle()
        questionTextView = findViewById(R.id.question)
        questionTextView.text = test.questions[currentQuestionIndex].question
        findViewById<ImageButton>(R.id.done).setOnClickListener(this)
        answerEditText = findViewById(R.id.answer)
        answerEditText.setOnEditorActionListener(this)
        correctPlayer = MediaPlayer.create(this, R.raw.correct)
        incorrectPlayer = MediaPlayer.create(this, R.raw.incorrect)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        correctVibration = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
        incorrectVibration = VibrationEffect.createWaveform(longArrayOf(0, 100, 100, 100), -1)
    }

    override fun onClick(v: View?) {
        val currentQuestion = test.questions[currentQuestionIndex]
        Toast.makeText(
            this,
            if (answerEditText.text.toString()
                    .equals(currentQuestion.answer, true)
            ) {
                score++
                correctPlayer.start()
                vibrator.vibrate(correctVibration)
                "Answer is correct!"
            } else {
                incorrectQuestions.add(currentQuestion)
                incorrectPlayer.start()
                vibrator.vibrate(incorrectVibration)
                "Answer is incorrect!"
            },
            Toast.LENGTH_SHORT
        ).show()
        answerEditText.text.clear()

        if (++currentQuestionIndex != test.questions.size) {
            questionTextView.text = test.questions[currentQuestionIndex].question
        } else {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Test passed!")
                .setMessage("Score: ${score}/${test.questions.size}")
                .setNegativeButton("Back", this)
                .setOnCancelListener(this)
            if (score != test.questions.size) {
                dialog.setPositiveButton("Error correction", this)
            }
            dialog.show()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            setResult(
                RESULT_OK, Intent()
                    .putExtra(ResultExtras.NAME, test.name)
                    .putExtra(ResultExtras.QUESTIONS, incorrectQuestions)
            )
        }
        finish()
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_DONE) {
            onClick(null)
            true
        } else {
            false
        }
    }

    override fun onCancel(dialog: DialogInterface?) {
        finish()
    }

    companion object {
        fun newIntent(context: Context, test: Test): Intent {
            return Intent(context, PassTestActivity::class.java)
                .putExtra(EXTRA_TEST, test)
        }
    }
}
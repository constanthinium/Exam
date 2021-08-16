package ru.kostyadzyuba.exam

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

class PassTestActivity : AppCompatActivity(), View.OnClickListener, DialogInterface.OnClickListener,
    TextView.OnEditorActionListener, DialogInterface.OnCancelListener {

    private lateinit var questions: ArrayList<QuestionAndAnswer>
    private lateinit var answerEditText: EditText
    private lateinit var vibrator: Vibrator
    private lateinit var vibration: VibrationEffect
    private lateinit var questionTextView: TextView
    private lateinit var testName: String

    private lateinit var correctPlayer: MediaPlayer
    private lateinit var incorrectPlayer: MediaPlayer

    private var currentQuestionIndex = 0
    private var score = 0
    private val incorrectQuestions = ArrayList<QuestionAndAnswer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_test)
        val extras = intent.extras!!
        testName = extras.getString(Keys.NAME)!!
        questions = extras.getSerializable(Keys.QUESTIONS) as ArrayList<QuestionAndAnswer>
        questions.shuffle()
        questionTextView = findViewById(R.id.question)
        questionTextView.text = questions[currentQuestionIndex].question
        findViewById<ImageButton>(R.id.done).setOnClickListener(this)
        answerEditText = findViewById(R.id.answer)
        answerEditText.setOnEditorActionListener(this)
        correctPlayer = MediaPlayer.create(this, R.raw.correct)
        incorrectPlayer = MediaPlayer.create(this, R.raw.incorrect)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibration = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
    }

    override fun onClick(v: View?) {
        val currentQuestion = questions[currentQuestionIndex]
        Toast.makeText(
            this,
            if (answerEditText.text.toString()
                    .equals(currentQuestion.answer, true)
            ) {
                score++
                correctPlayer.start()
                "Answer is correct!"
            } else {
                incorrectQuestions.add(currentQuestion)
                incorrectPlayer.start()
                "Answer is incorrect!"
            },
            Toast.LENGTH_SHORT
        ).show()
        vibrator.vibrate(vibration)
        answerEditText.text.clear()

        if (++currentQuestionIndex != questions.size) {
            questionTextView.text = questions[currentQuestionIndex].question
        } else {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Test passed!")
                .setMessage("Score: ${score}/${questions.size}")
                .setNegativeButton("Back", this)
                .setOnCancelListener(this)
            if (score != questions.size) {
                dialog.setPositiveButton("Error correction", this)
            }
            dialog.show()
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            setResult(RESULT_OK, Intent()
                .putExtra(Keys.NAME, testName)
                .putExtra(Keys.QUESTIONS, incorrectQuestions))
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
}
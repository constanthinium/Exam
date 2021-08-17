package ru.kostyadzyuba.exam

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

private const val REQUEST_QUESTIONS = 0
private const val REQUEST_PASS = 1

class TestsFragment : Fragment(), View.OnClickListener, View.OnLongClickListener,
    DialogInterface.OnClickListener, DialogInterface.OnShowListener {

    private lateinit var testsAdapter: TestsAdapter
    private lateinit var testNameDialog: AlertDialog
    private lateinit var testNameText: EditText
    private lateinit var positiveButton: Button
    private lateinit var save: File
    private lateinit var emptyView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var packageContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tests, container, false)
        save = File(packageContext.filesDir, "tests.json")
        testsAdapter = TestsAdapter(loadOrCreate(), this, this)
        emptyView = root.findViewById(R.id.empty)
        recyclerView = root.findViewById<RecyclerView>(R.id.tests).apply {
            adapter = testsAdapter
            attachSwipe(this)
        }
        root.findViewById<FloatingActionButton>(R.id.add)
            .setOnClickListener(this)
        initDialog()
        updateUiAndSave(true)
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        packageContext = context
    }

    private fun loadOrCreate(): MutableList<Test> {
        if (!save.exists()) {
            return mutableListOf()
        } else {
            val jsonString = save.readText()
            val testsJsonObject = JSONObject(jsonString)
            val testsJson = testsJsonObject.getJSONArray("tests")
            val tests = mutableListOf<Test>()
            for (testIndex in 0 until testsJson.length()) {
                val testJson = testsJson.getJSONObject(testIndex)
                val questionsJson = testJson.getJSONArray("questions")
                val questionsCount = questionsJson.length()
                val test = Test(testJson.getString("name"), ArrayList(questionsCount))
                for (questionIndex in 0 until questionsCount) {
                    val questionJson = questionsJson.getJSONObject(questionIndex)
                    val question = QuestionAndAnswer(
                        questionJson.getString("question"),
                        questionJson.getString("answer")
                    )
                    test.questions.add(question)
                }
                tests.add(test)
            }
            return tests
        }
    }

    private fun attachSwipe(recyclerView: RecyclerView) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                testsAdapter.tests.removeAt(position)
                testsAdapter.notifyItemRemoved(position)
                updateUiAndSave()
            }
        }).attachToRecyclerView(recyclerView)
    }

    private fun initDialog() {
        testNameText = EditText(packageContext).apply {
            inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            hint = "Test name"
            requestFocus()
            doAfterTextChanged {
                positiveButton.isEnabled = !it.isNullOrBlank()
            }
        }

        testNameDialog = AlertDialog.Builder(packageContext)
            .setTitle("Enter Test Name")
            .setView(FrameLayout(packageContext).apply {
                val margin = resources.getDimension(R.dimen.dialog_margin).toInt()
                setPadding(margin, 0, margin, 0)
                addView(testNameText)
            })
            .setPositiveButton("OK", this)
            .setNegativeButton("Cancel", null)
            .create().apply {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                setOnShowListener(this@TestsFragment)
            }
    }

    private fun updateUiAndSave(disableSave: Boolean = false) {
        emptyView.visibility = if (testsAdapter.itemCount != 0)
            View.INVISIBLE else View.VISIBLE

        if (!disableSave) {
            val testsJson = JSONArray()
            for (test in testsAdapter.tests) {
                val testJson = JSONObject()
                testJson.put("name", test.name)
                val questionsJson = JSONArray()
                for (question in test.questions) {
                    val questionJson = JSONObject()
                    questionJson.put("question", question.question)
                    questionJson.put("answer", question.answer)
                    questionsJson.put(questionJson)
                }
                testJson.put("questions", questionsJson)
                testsJson.put(testJson)
            }
            val outputObject = JSONObject()
            outputObject.put("tests", testsJson)
            val jsonString = outputObject.toString(4)
            save.writeText(jsonString)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.add -> testNameDialog.show()
            else -> {
                val test = testsAdapter.tests[recyclerView.getChildAdapterPosition(view)]
                startActivityForResult(
                    Intent(packageContext, PassTestActivity::class.java)
                        .putExtra(Keys.NAME, test.name)
                        .putExtra(Keys.QUESTIONS, test.questions), REQUEST_PASS
                )
            }
        }
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        startActivityForResult(Intent(packageContext, QuestionsActivity::class.java), REQUEST_QUESTIONS)
    }

    override fun onShow(dialog: DialogInterface) {
        positiveButton = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.isEnabled = false
        testNameText.text.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            data as Intent
            if (requestCode == REQUEST_QUESTIONS) {
                val questions =
                    data.getSerializableExtra(Keys.QUESTIONS)!! as ArrayList<QuestionAndAnswer>
                val testIndex = data.getIntExtra(Keys.INDEX, -1)
                if (testIndex == -1) {
                    testsAdapter.tests.add(Test(testNameText.text.toString(), questions))
                    testsAdapter.notifyItemInserted(testsAdapter.itemCount - 1)
                    testNameText.text.clear()
                } else {
                    testsAdapter.tests[testIndex].questions = questions
                }
            } else if (requestCode == REQUEST_PASS) {
                testsAdapter.tests.add(
                    Test(
                        "Error correction: ${data.getStringExtra(Keys.NAME)}",
                        data.getSerializableExtra(Keys.QUESTIONS) as ArrayList<QuestionAndAnswer>
                    )
                )
            }
            updateUiAndSave()
        }
    }

    override fun onLongClick(view: View): Boolean {
        val position = recyclerView.getChildAdapterPosition(view)
        val test = testsAdapter.tests[position]
        startActivityForResult(
            Intent(packageContext, QuestionsActivity::class.java)
                .putExtra(Keys.TEST, test)
                .putExtra(Keys.INDEX, position), REQUEST_QUESTIONS
        )
        return true
    }
}
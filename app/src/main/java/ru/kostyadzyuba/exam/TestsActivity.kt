package ru.kostyadzyuba.exam

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import java.io.File

class TestsActivity : AppCompatActivity(),
    View.OnClickListener,
    DialogInterface.OnClickListener,
    DialogInterface.OnShowListener {

    private lateinit var testsAdapter: TestsAdapter
    private lateinit var testTitleDialog: AlertDialog
    private lateinit var testTitleText: EditText
    private lateinit var positiveButton: Button
    private lateinit var saveFile: File
    private lateinit var emptyView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests)
        loadTests()
        emptyView = findViewById(R.id.empty)
        findViewById<RecyclerView>(R.id.tests).apply {
            adapter = testsAdapter
            attachSwipe(this)
        }
        findViewById<FloatingActionButton>(R.id.add)
            .setOnClickListener(this)
        initDialog()
        updateUi(false)
    }

    private fun loadTests() {
        saveFile = File(filesDir, "tests.json")
        testsAdapter = TestsAdapter(
            if (saveFile.exists()) {
                val jsonArray = JSONArray(saveFile.readText())
                MutableList(jsonArray.length(), jsonArray::getString)
            } else {
                mutableListOf()
            }
        )
    }

    private fun updateUi(save: Boolean = true) {
        if (save) {
            val jsonArray = JSONArray(testsAdapter.tests)
            saveFile.writeText(jsonArray.toString(4))
        }
        emptyView.visibility = if (testsAdapter.itemCount != 0)
            View.INVISIBLE else View.VISIBLE
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
                updateUi()
            }
        }).attachToRecyclerView(recyclerView)
    }

    private fun initDialog() {
        testTitleText = EditText(this).apply {
            inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            hint = "Test title"
            requestFocus()
            doAfterTextChanged {
                positiveButton.isEnabled = !it.isNullOrBlank()
            }
        }

        testTitleDialog = AlertDialog.Builder(this)
            .setTitle("Enter Test Title")
            .setView(FrameLayout(this).apply {
                val margin = resources.getDimension(R.dimen.dialog_margin).toInt()
                setPadding(margin, 0, margin, 0)
                addView(testTitleText)
            })
            .setPositiveButton("OK", this)
            .setNegativeButton("Cancel", null)
            .create().apply {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                setOnShowListener(this@TestsActivity)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.help -> {
                AlertDialog.Builder(this)
                    .setTitle(item.title)
                    .setMessage("Tap a test to take it, hold to edit, and swipe to delete.")
                    .setPositiveButton("OK", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.add -> testTitleDialog.show()
            else -> throw IllegalArgumentException("View.id")
        }
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        startActivityForResult(Intent(this, QuestionsActivity::class.java), 0)
    }

    override fun onShow(dialog: DialogInterface) {
        positiveButton = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.isEnabled = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            testsAdapter.tests.add(testTitleText.text.toString())
            testsAdapter.notifyItemInserted(testsAdapter.itemCount - 1)
            testTitleText.text.clear()
            updateUi()
        }
    }
}
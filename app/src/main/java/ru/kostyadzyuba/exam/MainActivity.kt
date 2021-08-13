package ru.kostyadzyuba.exam

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(),
    View.OnClickListener,
    DialogInterface.OnClickListener,
    DialogInterface.OnShowListener {

    private var testsAdapter =
        TestsAdapter(mutableListOf("Entrance test", "Semester test", "Course test"))
    private lateinit var testTitleDialog: AlertDialog
    private lateinit var testTitleText: EditText
    private lateinit var positiveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tests = findViewById<RecyclerView>(R.id.tests)
        tests.adapter = testsAdapter
        val add = findViewById<FloatingActionButton>(R.id.add)
        add.setOnClickListener(this)
        initDialog()
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
                setOnShowListener(this@MainActivity)
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
        testsAdapter.testList.add(testTitleText.text.toString())
        testsAdapter.notifyItemInserted(testsAdapter.testList.size - 1)
        testTitleText.text.clear()
    }

    override fun onShow(dialog: DialogInterface) {
        positiveButton = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.isEnabled = false
    }
}
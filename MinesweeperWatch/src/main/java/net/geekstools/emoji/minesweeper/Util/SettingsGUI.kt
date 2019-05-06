package net.geekstools.emoji.minesweeper.Util

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import net.geekstools.emoji.minesweeper.MinesweeperActivity
import net.geekstools.emoji.minesweeper.R
import net.geekstools.emoji.minesweeper.Util.Functions.FunctionsClass
import net.geekstools.emoji.minesweeper.Util.Functions.WebInterface
import java.util.*


class SettingsGUI : AppCompatActivity(), View.OnClickListener {

    lateinit var functionsClass: FunctionsClass
    lateinit var webInterface: WebInterface

    lateinit var bombsCount: EditText
    lateinit var columnCount: EditText
    lateinit var rowCount: EditText

    lateinit var backSave: ImageView

    lateinit var emojiSets: LinearLayout

    lateinit var emojiMap: LinkedHashMap<String, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.isScreenRound) {
            setContentView(R.layout.settings_gui_circle)
        } else {
            setContentView(R.layout.settings_gui_square)
        }

        functionsClass = FunctionsClass(this@SettingsGUI, applicationContext)
        webInterface = WebInterface(this@SettingsGUI, applicationContext)

        emojiMap = LinkedHashMap<String, Int>()

        bombsCount = findViewById<EditText>(R.id.bombsCount) as EditText
        columnCount = findViewById<EditText>(R.id.columnCount) as EditText
        rowCount = findViewById<EditText>(R.id.rowCount) as EditText

        backSave = findViewById<ImageView>(R.id.backSave) as ImageView

        emojiSets = findViewById<LinearLayout>(R.id.emojiSets) as LinearLayout

        bombsCount.setText(webInterface.bombs.toString())
        columnCount.setText(webInterface.column.toString())
        rowCount.setText(webInterface.row.toString())

        bombsCount.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    val tableSize: Int = webInterface.column * webInterface.row
                    if (bombsCount.text.toString().toInt() >= tableSize) {
                        Toast.makeText(applicationContext, getString(R.string.bombError), Toast.LENGTH_LONG).show()
                    } else {
                        functionsClass.savePreference("Bomb", bombsCount.text.toString().toInt())
                        functionsClass.savePreference("PreferencesChanged", true)

                        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(textView.windowToken, 0)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            true
        }

        columnCount.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    if (columnCount.text.toString().toInt() < 9) {
                        Toast.makeText(applicationContext, getString(R.string.columnError), Toast.LENGTH_LONG).show()
                    } else {
                        functionsClass.savePreference("Column", columnCount.text.toString().toInt())
                        functionsClass.savePreference("PreferencesChanged", true)

                        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(textView.windowToken, 0)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            true
        }

        rowCount.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    if (rowCount.text.toString().toInt() < 7) {
                        Toast.makeText(applicationContext, getString(R.string.rowError), Toast.LENGTH_LONG).show()
                    } else {
                        functionsClass.savePreference("Row", rowCount.text.toString().toInt())
                        functionsClass.savePreference("PreferencesChanged", true)

                        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(textView.windowToken, 0)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            true
        }

        backSave.setOnClickListener {
            val tableSize: Int = webInterface.column * webInterface.row
            if (bombsCount.text.toString().toInt() >= tableSize) {
                Toast.makeText(applicationContext, getString(R.string.bombError), Toast.LENGTH_LONG).show()
            } else {
                var activityOptions: ActivityOptions = ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                startActivity(Intent(applicationContext, MinesweeperActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), activityOptions.toBundle())
                finish()
            }
        }

        loadEmojiSet()
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onClick(view: View?) {
        if (view is TextView) {
            try {
                functionsClass.savePreference("EmojiSet", (emojiMap[view.text.toString()]!!.toInt()))
                loadEmojiSet()
            } catch (e: KotlinNullPointerException) {
                e.printStackTrace()
            }
        }
    }

    fun loadEmojiSet() {
        emojiSets.removeAllViews()
        emojiMap.clear()

        val emojiSetsItems = arrayListOf(
                getString(R.string.emojiSetONE),
                getString(R.string.emojiSetTWO),
                getString(R.string.emojiSetTHREE),
                getString(R.string.emojiSetFOUR),
                getString(R.string.emojiSetFIVE)
        )
        for ((emojiIndex, emojiSet) in emojiSetsItems.withIndex()) {
            emojiMap.put(emojiSet, emojiIndex)

            val itemsEmoji = layoutInflater.inflate(R.layout.emoji_items, null)
            val itemEmoji = itemsEmoji.findViewById<TextView>(R.id.emojiSet)
            itemsEmoji.setOnClickListener(this@SettingsGUI)
            itemEmoji.text = emojiSet
            if (emojiIndex == webInterface.EmojiSet()) {
                itemEmoji.append("   ✔ ")
            }
            emojiSets.addView(itemsEmoji)
        }
    }
}

package net.geekstools.emoji.minesweeper.Util

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings_gui.*
import net.geekstools.emoji.minesweeper.MinesweeperActivity
import net.geekstools.emoji.minesweeper.R
import net.geekstools.emoji.minesweeper.Util.Functions.FunctionsClass
import net.geekstools.emoji.minesweeper.Util.Functions.PublicVariable
import net.geekstools.emoji.minesweeper.Util.Functions.WebInterface
import java.util.*


class SettingsGUI : AppCompatActivity(), View.OnClickListener {

    lateinit var functionsClass: FunctionsClass
    lateinit var webInterface: WebInterface

    lateinit var emojiMap: LinkedHashMap<String, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_gui)

        functionsClass = FunctionsClass(this@SettingsGUI, applicationContext)
        webInterface = WebInterface(this@SettingsGUI, applicationContext)

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.white)
        window.navigationBarColor = if (functionsClass.returnAPI() > 25) getColor(R.color.white) else getColor(R.color.grey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        emojiMap = LinkedHashMap<String, Int>()

        bombsCount.setText(webInterface.bombs.toString())
        columnCount.setText(webInterface.column.toString())
        rowCount.setText(webInterface.row.toString())

        columnCount.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                try {
                    if (columnCount.text.toString().toInt() < 9) {
                        Toast.makeText(applicationContext, getString(R.string.columnError), Toast.LENGTH_LONG).show()
                    } else {
                        functionsClass.saveDefaultPreference("Column", columnCount.text.toString().toInt())
                        functionsClass.saveDefaultPreference("PreferencesChanged", true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            true
        }

        rowCount.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                try {
                    if (rowCount.text.toString().toInt() < 7) {
                        Toast.makeText(applicationContext, getString(R.string.rowError), Toast.LENGTH_LONG).show()
                    } else {
                        functionsClass.saveDefaultPreference("Row", rowCount.text.toString().toInt())
                        functionsClass.saveDefaultPreference("PreferencesChanged", true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            true
        }

        bombsCount.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    val tableSize: Int = webInterface.column * webInterface.row
                    if (bombsCount.text.toString().toInt() >= tableSize) {
                        Toast.makeText(applicationContext, getString(R.string.bombError), Toast.LENGTH_LONG).show()
                    } else {
                        functionsClass.saveDefaultPreference("Bomb", bombsCount.text.toString().toInt())
                        functionsClass.saveDefaultPreference("PreferencesChanged", true)

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
                //Column
                try {
                    if (columnCount.text.toString().toInt() < 9) {
                        Toast.makeText(applicationContext, getString(R.string.columnError), Toast.LENGTH_LONG).show()
                    } else {
                        functionsClass.saveDefaultPreference("Column", columnCount.text.toString().toInt())
                        functionsClass.saveDefaultPreference("PreferencesChanged", true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                //Row
                try {
                    if (rowCount.text.toString().toInt() < 7) {
                        Toast.makeText(applicationContext, getString(R.string.rowError), Toast.LENGTH_LONG).show()
                    } else {
                        functionsClass.saveDefaultPreference("Row", rowCount.text.toString().toInt())
                        functionsClass.saveDefaultPreference("PreferencesChanged", true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                //Bomb
                try {
                    if (bombsCount.text.toString().toInt() >= tableSize) {
                        Toast.makeText(applicationContext, getString(R.string.bombError), Toast.LENGTH_LONG).show()
                    } else {
                        functionsClass.saveDefaultPreference("Bomb", bombsCount.text.toString().toInt())
                        functionsClass.saveDefaultPreference("PreferencesChanged", true)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                var activityOptions: ActivityOptions = ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out)
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
        PublicVariable.eligibleToLoadShowAds = true
    }

    override fun onPause() {
        super.onPause()
        PublicVariable.eligibleToLoadShowAds = false
    }

    override fun onClick(view: View?) {
        if (view is TextView) {
            try {
                functionsClass.saveDefaultPreference("EmojiSet", (emojiMap[view.text.toString()]!!.toInt()))
                loadEmojiSet()
            } catch (e: KotlinNullPointerException) {
                e.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        val tableSize: Int = webInterface.column * webInterface.row
        if (bombsCount.text.toString().toInt() >= tableSize) {
            Toast.makeText(applicationContext, getString(R.string.bombError), Toast.LENGTH_LONG).show()
        } else {
            //Column
            try {
                if (columnCount.text.toString().toInt() < 9) {
                    Toast.makeText(applicationContext, getString(R.string.columnError), Toast.LENGTH_LONG).show()
                } else {
                    functionsClass.saveDefaultPreference("Column", columnCount.text.toString().toInt())
                    functionsClass.saveDefaultPreference("PreferencesChanged", true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //Row
            try {
                if (rowCount.text.toString().toInt() < 7) {
                    Toast.makeText(applicationContext, getString(R.string.rowError), Toast.LENGTH_LONG).show()
                } else {
                    functionsClass.saveDefaultPreference("Row", rowCount.text.toString().toInt())
                    functionsClass.saveDefaultPreference("PreferencesChanged", true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //Bomb
            try {
                if (bombsCount.text.toString().toInt() >= tableSize) {
                    Toast.makeText(applicationContext, getString(R.string.bombError), Toast.LENGTH_LONG).show()
                } else {
                    functionsClass.saveDefaultPreference("Bomb", bombsCount.text.toString().toInt())
                    functionsClass.saveDefaultPreference("PreferencesChanged", true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            var activityOptions: ActivityOptions = ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out)
            startActivity(Intent(applicationContext, MinesweeperActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), activityOptions.toBundle())
            finish()
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
            emojiMap[emojiSet] = emojiIndex

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

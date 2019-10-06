package net.geekstools.emoji.minesweeper

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.support.wearable.activity.WearableActivity
import android.support.wearable.view.ConfirmationOverlay
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.wearable.intent.RemoteIntent
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.emoji.minesweeper.Util.Functions.FunctionsClass
import net.geekstools.emoji.minesweeper.Util.Functions.WebInterface
import org.xwalk.core.XWalkInitializer
import org.xwalk.core.XWalkView


class MinesweeperActivity : WearableActivity(), XWalkInitializer.XWalkInitListener {

    lateinit var functionsClass: FunctionsClass

    lateinit private var mineSweeper: XWalkView
    lateinit private var xWalkInitializer: XWalkInitializer

    lateinit private var splashScreen: RelativeLayout

    lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onXWalkInitStarted() {

    }

    override fun onXWalkInitCancelled() {

    }

    override fun onXWalkInitFailed() {

    }

    override fun onXWalkInitCompleted() {
        val xWalkSettings = mineSweeper!!.settings
        xWalkSettings.javaScriptEnabled = true
        xWalkSettings.domStorageEnabled = true
        xWalkSettings.databaseEnabled = true

        xWalkSettings.builtInZoomControls = true
        xWalkSettings.textZoom = 100

        val displayMetrics: DisplayMetrics = resources.displayMetrics
        if (displayMetrics.heightPixels >= 400) {
            xWalkSettings.setInitialPageScale(130.0f)
        } else {
            xWalkSettings.setInitialPageScale(90.0f)
        }
        xWalkSettings.supportZoom()

        if (functionsClass.networkConnection()) {
            mineSweeper.addJavascriptInterface(WebInterface(this@MinesweeperActivity, applicationContext), "Android")
            mineSweeper.loadUrl("file:///android_asset/minesweeper_watch/index.html")
        } else {
        }

        val animation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
        Handler().postDelayed({
            mineSweeper.scrollBy(mineSweeper.getTop(), mineSweeper.getBottom())

            splashScreen!!.startAnimation(animation)
        }, 1777)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                splashScreen.visibility = View.INVISIBLE

                if (!functionsClass.networkConnection()) {
                    ConfirmationOverlay()
                            .setMessage(getString(R.string.internetConnection))
                            .setDuration(1000 * 5)
                            .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                            .showOn(this@MinesweeperActivity)
                    Toast.makeText(applicationContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show()

                    Handler().postDelayed({
                        finish()
                    }, 5000)
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xWalkInitializer = XWalkInitializer(this@MinesweeperActivity, applicationContext)
        xWalkInitializer.initAsync()
        if (resources.configuration.isScreenRound) {
            setContentView(R.layout.minesweeper_emoji_view_circle)
        } else {
            setContentView(R.layout.minesweeper_emoji_view_square)
        }
        functionsClass = FunctionsClass(this@MinesweeperActivity, applicationContext)

        mineSweeper = findViewById<View>(R.id.tRexRun) as XWalkView
        splashScreen = findViewById<View>(R.id.splashScreen) as RelativeLayout

        setAmbientEnabled()
    }

    override fun onStart() {
        super.onStart()

        val resultReceiver = object : ResultReceiver(Handler()) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultCode == RemoteIntent.RESULT_OK) {
                    if (functionsClass.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(getString(R.string.booleanShowPlayStoreLinkDialogue))) {
                        ConfirmationOverlay()
                                .setMessage(firebaseRemoteConfig.getString(getString(R.string.stringPlayStoreLinkDialogue)))
                                .setDuration(1000 * 1)
                                .showOn(this@MinesweeperActivity)

                        functionsClass.savePreference(".UserState", "FirstTime", false)
                    }
                } else if (resultCode == RemoteIntent.RESULT_FAILED) {

                } else {

                }
            }
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this@MinesweeperActivity) { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnSuccessListener {
                            if (firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)) > functionsClass.appVersionCode(packageName)) {
                                Toast.makeText(applicationContext, getString(R.string.updateAvailable), Toast.LENGTH_LONG).show()
                                functionsClass.notificationCreator(
                                        getString(R.string.updateAvailable),
                                        firebaseRemoteConfig.getString(getString(R.string.stringUpcomingChangeLogSummaryPhone)),
                                        firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)).toInt()
                                )
                            }
                            if (functionsClass.isFirstTimeOpen() || firebaseRemoteConfig.getBoolean(getString(R.string.booleanPlayStoreLink))) {
                                if (!BuildConfig.DEBUG) {
                                    val intentPlayStore = Intent(Intent.ACTION_VIEW)
                                            .addCategory(Intent.CATEGORY_BROWSABLE)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            .setData(Uri.parse(firebaseRemoteConfig.getString(getString(R.string.stringPlayStoreLink))))
                                    RemoteIntent.startRemoteActivity(
                                            applicationContext,
                                            intentPlayStore,
                                            resultReceiver)
                                }
                            }
                        }
                    } else {

                    }
                }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            //Do nothing
            println("*** " + keyCode)

            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            println("*** " + keyCode)

            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            println("*** " + keyCode)

            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            println("*** " + keyCode)

            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            println("*** " + keyCode)

            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

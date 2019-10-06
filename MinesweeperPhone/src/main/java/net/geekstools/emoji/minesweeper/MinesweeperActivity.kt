package net.geekstools.emoji.minesweeper

import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import net.geekstools.emoji.minesweeper.Util.Functions.FunctionsClass
import net.geekstools.emoji.minesweeper.Util.Functions.PublicVariable
import net.geekstools.emoji.minesweeper.Util.Functions.WebInterface

class MinesweeperActivity : Activity() {

    lateinit private var functionsClass: FunctionsClass

    lateinit private var mineSweeper: WebView

    lateinit var rewardVideo: TextView
    lateinit private var splashScreen: RelativeLayout
    lateinit private var supportView: ImageView

    lateinit private var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onPointerCaptureChanged(hasCapture: Boolean) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.minesweeper_emoji_view)

        functionsClass = FunctionsClass(this@MinesweeperActivity, applicationContext)

        mineSweeper = findViewById<WebView>(R.id.tRexRun) as WebView
        rewardVideo = findViewById<TextView>(R.id.rewardVideo) as TextView
        splashScreen = findViewById<RelativeLayout>(R.id.splashScreen) as RelativeLayout
        supportView = findViewById<ImageView>(R.id.supportView) as ImageView

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getColor(R.color.default_color)
        window.navigationBarColor = getColor(R.color.default_color)

        val webSettings = mineSweeper.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true

        webSettings.builtInZoomControls = false
        webSettings.textZoom = 113
        if (functionsClass.networkConnection()) {
            mineSweeper.addJavascriptInterface(WebInterface(this@MinesweeperActivity, applicationContext), "Android")
            mineSweeper.loadUrl("file:///android_asset/minesweeper_phone/index.html")
        } else {
            Toast.makeText(applicationContext, getString(R.string.internetConnection), Toast.LENGTH_LONG).show()
        }

        Handler().postDelayed({
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

            val animation: Animation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
            splashScreen.startAnimation(animation)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    splashScreen.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })

            val colorAnimationStatus: ValueAnimator = ValueAnimator.ofArgb(window.navigationBarColor, getColor(R.color.white))
            colorAnimationStatus.duration = 333
            colorAnimationStatus.addUpdateListener { animator ->
                window.statusBarColor = animator.animatedValue as Int
            }
            colorAnimationStatus.start()

            val colorAnimationNav: ValueAnimator = ValueAnimator.ofArgb(window.navigationBarColor, if (functionsClass.returnAPI() > 25) getColor(R.color.white) else getColor(R.color.grey))
            colorAnimationStatus.duration = 333
            colorAnimationNav.addUpdateListener { animator ->
                if (functionsClass.returnAPI() > 25) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
                window.navigationBarColor = animator.animatedValue as Int
            }
            colorAnimationNav.start()
        }, 777)

        val intentFilter = IntentFilter()
        intentFilter.addAction("ENABLE_REWARDED_VIDEO")
        intentFilter.addAction("RELOAD_REWARDED_VIDEO")
        intentFilter.addAction("REWARDED_PROMOTION_CODE")
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "ENABLE_REWARDED_VIDEO") {
                    val rewardedPromotionCode = functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
                    if ((rewardedPromotionCode >= 33)
                            && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == true) {

                        rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                                "<big>Please Click to See Video Ads to<br/>" +
                                "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Support Geeks Empire Open Source Projects</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                                + "</font>")
                    } else {
                        rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                                "<big>Click to See Video Ads to Get<br/>" +
                                "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Promotion Codes of Geeks Empire Premium Apps</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                                + "</font>")
                        rewardVideo.append("$rewardedPromotionCode / 33")
                    }
                    rewardVideo.visibility = View.VISIBLE
                } else if (intent.action == "RELOAD_REWARDED_VIDEO") {
                    rewardVideo.visibility = View.INVISIBLE
                } else if (intent.action == "REWARDED_PROMOTION_CODE") {
                    rewardVideo.setTextColor(getColor(R.color.light))
                    rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                            "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                            "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                            + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9"
                            + "</font>")
                    rewardVideo.visibility = View.VISIBLE
                }
            }
        }
        registerReceiver(broadcastReceiver, intentFilter)

        rewardVideo.setOnClickListener {
            if ((functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0) >= 33)
                    && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == false) {
                try {
                    val textMsg = ("\n\n\n\n\n"
                            + "[Essential Information]" + "\n"
                            + getString(R.string.app_name) + " | " + functionsClass.appVersionName(getPackageName()) + "\n"
                            + functionsClass.getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + functionsClass.getCountryIso().toUpperCase())
                    val email = Intent(Intent.ACTION_SEND)
                    email.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support)))
                    email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.rewardedPromotionCodeTitle))
                    email.putExtra(Intent.EXTRA_TEXT, textMsg)
                    email.type = "text/*"
                    email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    email.setPackage("com.google.android.gm")
                    startActivity(Intent.createChooser(email, getString(R.string.rewardedPromotionCodeTitle)))

                    functionsClass.savePreference(".NoAdsRewardedInfo", "Requested", true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                rewardVideo.visibility = View.INVISIBLE
                sendBroadcast(Intent("SHOW_REWARDED_VIDEO_ADS"))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        supportView.setOnClickListener {
            val contactOption = arrayOf(
                    "Send an Email",
                    "Send a Message",
                    "Rate & Write Review",
                    "Floating Shortcuts",
                    "Super Shortcuts",
                    "Pin Pics on Map"
            )
            var builder: AlertDialog.Builder = AlertDialog.Builder(this@MinesweeperActivity, R.style.GeeksEmpire_Dialogue_Day)
            builder.setTitle(getString(R.string.supportTitle))
            builder.setIcon(getDrawable(R.drawable.draw_support))
            builder.setSingleChoiceItems(contactOption, 0, null)
            builder.setPositiveButton(android.R.string.ok) { dialog, whichButton ->
                when (whichButton) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition
                        if (selectedPosition == 0) {
                            val textMsg = ("\n\n\n\n\n"
                                    + "[Essential Information]" + "\n"
                                    + functionsClass.getDeviceName() + " | " + "API " + Build.VERSION.SDK_INT + " | " + functionsClass.getCountryIso().toUpperCase())
                            val email = Intent(Intent.ACTION_SEND)
                            email.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support)))
                            email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_tag) + " [" + functionsClass.appVersionName(packageName) + "] ")
                            email.putExtra(Intent.EXTRA_TEXT, textMsg)
                            email.type = "message/*"
                            email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(Intent.createChooser(email, getString(R.string.feedback_tag)))
                        } else if (selectedPosition == 1) {
                            val r = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_facebook)))
                            startActivity(r)
                        } else if (selectedPosition == 2) {
                            val r = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_play_store) + packageName))
                            startActivity(r)
                        } else if (selectedPosition == 3) {
                            val r = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_floating_shortcuts)))
                            startActivity(r)
                        } else if (selectedPosition == 4) {
                            val r = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_super_shortcuts)))
                            startActivity(r)
                        } else if (selectedPosition == 5) {
                            val r = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_pin_pic)))
                            startActivity(r)
                        }
                    }
                }
            }
            builder.show()
        }
    }

    override fun onResume() {
        super.onResume()
        PublicVariable.eligibleToLoadShowAds = true

        val rewardedPromotionCode = functionsClass.readPreference(".NoAdsRewardedInfo", "RewardedPromotionCode", 0)
        if ((rewardedPromotionCode >= 33)
                && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == true) {

            rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Please Click to See Rewarded Ads to<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Support Geeks Empire Open Source Projects</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                    + "</font>")
        } else if ((rewardedPromotionCode >= 33)
                && functionsClass.readPreference(".NoAdsRewardedInfo", "Requested", false) == false) {

            rewardVideo.setTextColor(getColor(R.color.light))
            rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Upgrade to Geeks Empire Premium Apps<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Rewarded to Get Promotion Codes</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                    + "\uD83D\uDCE9 Click Here to Request \uD83D\uDCE9<br/>"
                    + "</font>")
            rewardVideo.visibility = View.VISIBLE
        } else {
            rewardVideo.text = Html.fromHtml("<br/><font color='#f2f7ff'>" +
                    "<big>Click to See Rewarded Ads to Get<br/>" +
                    "\uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E <b>Promotion Codes of Geeks Empire Premium Apps</b> \uD83D\uDC8E  \uD83D\uDC8E  \uD83D\uDC8E </big><br/>"
                    + "</font>")
            rewardVideo.append("$rewardedPromotionCode / 33" + Html.fromHtml("<br/>"))
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        firebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this@MinesweeperActivity, OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activate().addOnSuccessListener {
                            if (firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)) > functionsClass.appVersionCode(packageName)) {
                                functionsClass.notificationCreator(
                                        getString(R.string.updateAvailable),
                                        firebaseRemoteConfig.getString(getString(R.string.stringUpcomingChangeLogSummaryPhone)),
                                        firebaseRemoteConfig.getLong(getString(R.string.integerVersionCodeNewUpdatePhone)).toInt()
                                )
                            } else {

                            }
                        }
                    } else {

                    }
                })
    }

    override fun onPause() {
        super.onPause()
        PublicVariable.eligibleToLoadShowAds = false
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}

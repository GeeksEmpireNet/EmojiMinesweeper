package net.geekstools.emoji.minesweeper.Util.Functions;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import net.geekstools.emoji.minesweeper.Util.SettingsGUI;

import org.xwalk.core.JavascriptInterface;

public class WebInterface {

    Activity activity;
    Context context;

    public WebInterface(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    @JavascriptInterface
    public void SettingsGUI() {
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out);
        context.startActivity(new Intent(context, SettingsGUI.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), activityOptions.toBundle());
        activity.finish();
    }

    @JavascriptInterface
    public int getColumn() {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("Column", 9);
    }

    @JavascriptInterface
    public int getRow() {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("Row", 7);
    }

    @JavascriptInterface
    public int getBombs() {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("Bomb", 13);
    }

    @JavascriptInterface
    public int EmojiSet() {
        // 🐣 💣 🚧 ◻️
        // 🍰 👾 📌 ◻️
        // 🌷 ⚡️ 🐞 ◻️
        // ⭕️ ❌ ❗️ ✖️️
        // ☀ ⚡ ☔ ☁️
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("EmojiSet", 0);
    }

    @JavascriptInterface
    public void LoadInterstitialAds() {
        context.sendBroadcast(new Intent("LOAD_INTERSTITIAL_ADS"));
    }

    @JavascriptInterface
    public void ShowInterstitialAds() {
        context.sendBroadcast(new Intent("SHOW_INTERSTITIAL_ADS"));
    }
}

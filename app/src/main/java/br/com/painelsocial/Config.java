package br.com.painelsocial;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Config {

	private static final String CFG_KEY_CUSTOMURL = "K_CUSTOMURL";
	private static final String CFG_KEY_STEAMID = "K_STEAMID";

	SharedPreferences preferences;

	private static Config singleton;

	private Config() {
		preferences = PreferenceManager.getDefaultSharedPreferences(SteamBadgeR.getInstance());
	}

	public synchronized static Config getInstance() {
		if (Config.singleton == null) {
            Config.singleton = new Config();
		}
		return Config.singleton;
	}

	public SharedPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	// configuracoes

    public String getCustomUrl() {
        return getPreferences().getString(CFG_KEY_CUSTOMURL, "");
    }

    public boolean setCustomUrl(String customUrl) {
        SharedPreferences.Editor ed = getPreferences().edit();
        ed.putString(CFG_KEY_CUSTOMURL, customUrl);
        return ed.commit();
    }

	public String getSteamId() {
		return getPreferences().getString(CFG_KEY_STEAMID, "");
	}

	public boolean setSteamId(String steamId) {
		SharedPreferences.Editor ed = getPreferences().edit();
		ed.putString(CFG_KEY_STEAMID, steamId);
		return ed.commit();
	}

}

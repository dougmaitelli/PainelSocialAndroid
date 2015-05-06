package br.com.painelsocial;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Config {

	private static final String CFG_KEY_TOKEN = "K_CUSTOMURL";

	SharedPreferences preferences;

	private static Config singleton;

	private Config() {
		preferences = PreferenceManager.getDefaultSharedPreferences(PainelSocial.getInstance());
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

	// Configs

    public String getToken() {
        return getPreferences().getString(CFG_KEY_TOKEN, null);
    }

    public boolean setToken(String token) {
        SharedPreferences.Editor ed = getPreferences().edit();
        ed.putString(CFG_KEY_TOKEN, token);
        return ed.commit();
    }

}

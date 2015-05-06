package br.com.painelsocial;

import android.app.Application;

public class PainelSocial extends Application {

	private static PainelSocial singleton;

	public PainelSocial() {

	}

	@Override
	public void onCreate() {
		super.onCreate();
		PainelSocial.singleton = this;
	}

	public static PainelSocial getInstance() {
		return PainelSocial.singleton;
	}

}

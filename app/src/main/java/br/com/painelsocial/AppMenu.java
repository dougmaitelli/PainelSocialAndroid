package br.com.painelsocial;

import android.support.v4.app.Fragment;

public enum AppMenu {

	CONTATO("Contato", null),
	SAIR("Sair", null);
	
	private String text;
	private Fragment tela;
	
	private AppMenu(String text, Fragment tela) {
		this.text = text;
		this.tela = tela;
	}

	public String getText() {
		return text;
	}

	public Fragment getTela() {
		return tela;
	}

}

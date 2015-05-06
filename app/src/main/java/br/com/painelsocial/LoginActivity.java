package br.com.painelsocial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends Activity {

    private Button buttonLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        new LoaderTask<LoginActivity>(this, true) {

            @Override
            public void process() {
                Config.getInstance().setToken("");
            }

            @Override
            public void onComplete() {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        };
    }

}

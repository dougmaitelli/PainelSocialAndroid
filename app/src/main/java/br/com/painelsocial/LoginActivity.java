package br.com.painelsocial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.painelsocial.ws.Ws;

public class LoginActivity extends Activity {

    private EditText inputEmail;
    private EditText inputPassword;
    private Button buttonLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        new LoaderTask<LoginActivity>(this, true) {

            private String token = null;

            @Override
            public void process() {
                try {
                    token = Ws.login(email, password);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onComplete() {
                if (token != null) {
                    Config.getInstance().setToken(token);
                    startActivity(new Intent(getContext(), MainActivity.class));
                }
            }
        };
    }

}

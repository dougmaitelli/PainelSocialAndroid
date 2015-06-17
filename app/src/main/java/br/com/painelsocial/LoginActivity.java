package br.com.painelsocial;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Favor preencher todos os campos!", Toast.LENGTH_LONG).show();
            return;
        }

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
                } else {
                    promptName(new PromptNameCallback() {
                        @Override
                        public void doCallback(final String name) {
                            new LoaderTask<LoginActivity>(getContext(), true) {

                                private String token = null;

                                @Override
                                public void process() {
                                    try {
                                        token = Ws.register(name, email, password);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                @Override
                                public void onComplete() {
                                    if (token != null) {
                                        Config.getInstance().setToken(token);
                                        startActivity(new Intent(getContext(), MainActivity.class));
                                    } else {
                                        Toast.makeText(getContext(), "Email ja cadastrado!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            };
                        }
                    });
                }
            }
        };
    }

    private static abstract class PromptNameCallback {

        public abstract void doCallback(String name);
    }

    private void promptName(final PromptNameCallback callback) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Nome");
        alert.setMessage("Insira seu nome:");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = input.getText().toString();

                callback.doCallback(name);
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

}

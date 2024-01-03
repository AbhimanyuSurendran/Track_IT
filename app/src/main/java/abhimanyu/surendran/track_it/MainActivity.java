package abhimanyu.surendran.track_it;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.Email_field);
        editTextPassword = findViewById(R.id.Password_field);
        buttonLogin = findViewById(R.id.Login_btn);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    loginUser(email, password);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter valid email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        class LoginUser extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, "{\r\n    \"email\": \"" + params[0] + "\",\r\n    \"password\": \"" + params[1] + "\"\r\n}");
                    Request request = new Request.Builder()
                            .url("http://192.168.1.111:3333/api/login")
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("X-API-Key", "")
                            .build();
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        return "Login successful";
                    } else {
                        return "Login failed";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Failed to connect to server";
                }
            }
        }

        LoginUser ru = new LoginUser();
        ru.execute(email, password);
    }
}
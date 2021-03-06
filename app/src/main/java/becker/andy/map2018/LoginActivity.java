package becker.andy.map2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jgabrielfreitas.core.BlurImageView;

import becker.andy.map2018.classes.PrefConfig;
import becker.andy.map2018.models.User;
import becker.andy.map2018.retrofit.ApiClient;
import becker.andy.map2018.retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    public static PrefConfig prefConfig;
    public static ApiInterface apiInterface;

    private BlurImageView blurImageView;
    private ImageView imageView;

    private EditText mEmail;
    private EditText mPassword;

    private Button mButtonLogin;
    private ImageButton mHaveAcc;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        prefConfig = new PrefConfig(this);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        if (prefConfig.readLoginStatus() && prefConfig.readUser().equals("Teacher")) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        } else if (prefConfig.readLoginStatus()) {
            finish();
            startActivity(new Intent(this, MainActivityStudent.class));
        }


        blurImageView = findViewById(R.id.bookBlurImageViewLogin);
        blurImageView.setBlur(2);
        imageView = findViewById(R.id.white_imageLogin);
        imageView.setAlpha(.7f);

        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);

        mButtonLogin = findViewById(R.id.btn_login);
        mProgressBar = findViewById(R.id.progressBarLogin);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        mHaveAcc = findViewById(R.id.btn_new_acc_login);
        mHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        mProgressBar.setVisibility(View.VISIBLE);
        String email = mEmail.getText().toString().trim();
        String pass = mPassword.getText().toString().trim();

        if (email.equals("") || pass.equals("")) {

            Toast.makeText(this, "Every Field Have to be Fullfilled", Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.GONE);
            return;
        }
        Call<User> call = LoginActivity.apiInterface.performLogin(email, pass);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                mProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().getResponse().equals("ok")) {

                        LoginActivity.prefConfig.writeLoginStatus(true);
                        LoginActivity.prefConfig.writeUserId(response.body().getUserId());
                        LoginActivity.prefConfig.writeEmail(response.body().getEmail());
                        LoginActivity.prefConfig.writeUser(response.body().getUser());
                        LoginActivity.prefConfig.writeInsti(response.body().getInstitution());
                        if (response.body().getUser().equals("Teacher")) {
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivityStudent.class));
                        }

                    }
                    if (response.body().getResponse().equals("failed")) {

                        Toast.makeText(LoginActivity.this, "Email and password incorrect", Toast.LENGTH_LONG).show();

                    } else {
                        Log.d(TAG, "onResponse: error");
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: failed");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

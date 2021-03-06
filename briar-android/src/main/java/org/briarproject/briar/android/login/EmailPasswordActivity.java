package org.briarproject.briar.android.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.activity.BaseActivity;
import org.briarproject.briar.android.controller.BriarController;
import org.briarproject.briar.android.controller.handler.UiResultHandler;
import org.briarproject.briar.android.util.UiUtils;

import javax.inject.Inject;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_HOME;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

//import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordActivity extends BaseActivity {

	private static final String TAG = "EmailPassword";
	@Inject
	PasswordController passwordController;

	@Inject
	BriarController briarController;

	private Button signInButton;
	private ProgressBar progress;
	private TextInputLayout emailInput;
	private EditText email;
	private TextInputLayout passwordInput;
	private EditText password;

	private FirebaseAuth mAuth;
	//private boolean successfulTask;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		// fade-in after splash screen instead of default animation
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
// THIS IS TO ENSURE THAT ANY DATA ON THE DEVICE WILL ALWAYS BE DELETED UPON APP STARTUP
		//if (!passwordController.accountExists()) {
			deleteAccount();
			return;
		//}
		/*mAuth = FirebaseAuth.getInstance();

		//if the user is already signed in
		if (mAuth.getCurrentUser() != null){
			//startActivity(new Intent(EmailPasswordActivity.this, some main activity))
			//finish();
		}

		setContentView(R.layout.activity_email_password_login);
		signInButton = findViewById(R.id.btn_sign_in);
		progress = findViewById(R.id.progress_wheel);
		emailInput = findViewById(R.id.email_layout);
		email = findViewById(R.id.edit_email);
		passwordInput = findViewById(R.id.password_layout);
		password = findViewById(R.id.edit_password);

		password.setOnEditorActionListener((v, actionId, event) -> {
			String email1 = email.getText().toString();
			String password1 = password.getText().toString();
			mAuth.signInWithEmailAndPassword(email1, password1);
			//validatePassword();
			return true;
		});
		password.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (count > 0) UiUtils.setError(passwordInput, null, false);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});*/
	}

	@Override
	public void onStart() {
		super.onStart();
		// If the user has already signed in, clean up this instance
		if (briarController.hasEncryptionKey()) {
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public void injectActivity(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	public void onBackPressed() {
		// Show the home screen rather than another password prompt
		Intent intent = new Intent(ACTION_MAIN);
		intent.addCategory(CATEGORY_HOME);
		startActivity(intent);
	}

	private void deleteAccount() {
		passwordController.deleteAccount(this);
		setResult(RESULT_CANCELED);
		Intent i = new Intent(this, SetupActivity.class);
		i.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
	}

	public void onSignInClick(View v) {
		String inputEmail = email.getText().toString();
		String inputPassword = password.getText().toString();

		if (TextUtils.isEmpty(inputEmail)){
			tryAgain();
		} else {
			validateLogin();
		}

		//validatePassword();
	}

	public void onCreateAccountClick(View v) {
		// TODO Encapsulate the dialog in a re-usable fragment
		//AlertDialog.Builder builder = new AlertDialog.Builder(this,
				//R.style.BriarDialogTheme);
		//builder.setTitle(R.string.dialog_title_lost_password);
		//builder.setMessage(R.string.dialog_message_lost_password);
		//builder.setPositiveButton(R.string.cancel, null);
		//builder.setNegativeButton(R.string.delete,
				//(dialog, which) -> deleteAccount());
		//AlertDialog dialog = builder.create();
		//dialog.show();

	}

	private void validateLogin(){
		String inputEmail = email.getText().toString();
		String inputPassword = password.getText().toString();
		hideSoftKeyboard(password);
		signInButton.setVisibility(INVISIBLE);
		progress.setVisibility(VISIBLE);

		//authenticate user
		mAuth.signInWithEmailAndPassword(inputEmail, inputPassword).addOnCompleteListener(
				EmailPasswordActivity.this,
				new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						//if sign in fails, display a message to the user. If sign in succeeds
						//the mAuth state listener will be notified and logic to handle the
						//signed in user can be handled in the listener.
						progress.setVisibility(INVISIBLE);
						if (task.isSuccessful()){
							//sign in successful
							passwordController.validatePassword(inputPassword,
									new UiResultHandler<Boolean>(EmailPasswordActivity.this) {
										@Override
										public void onResultUi(@NonNull Boolean result) {
											if (result) {
												setResult(RESULT_OK);
												supportFinishAfterTransition();
												// don't show closing animation,
												// but one for opening NavDrawerActivity
												overridePendingTransition(R.anim.screen_new_in,
														R.anim.screen_old_out);
											} else {
												tryAgain();
											}
										}
									});
							String tkn = FirebaseInstanceId.getInstance().getToken();
							Log.d("TOKEN_REFRESH_login", tkn);
						} else {
							//there was an error
							tryAgain();
						}
					}
				});
	}


	//used for original app, we are using validateLogin() for our adjustments instead
	private void validatePassword() {
		hideSoftKeyboard(password);
		signInButton.setVisibility(INVISIBLE);
		progress.setVisibility(VISIBLE);
		passwordController.validatePassword(password.getText().toString(),
				new UiResultHandler<Boolean>(this) {
					@Override
					public void onResultUi(@NonNull Boolean result) {
						if (result) {
							setResult(RESULT_OK);
							supportFinishAfterTransition();
							// don't show closing animation,
							// but one for opening NavDrawerActivity
							overridePendingTransition(R.anim.screen_new_in,
									R.anim.screen_old_out);
						} else {
							tryAgain();
						}
					}
				});
	}

	private void tryAgain() {
		UiUtils.setError(passwordInput, getString(R.string.try_again), true);
		signInButton.setVisibility(VISIBLE);
		progress.setVisibility(INVISIBLE);
		password.setText("");

		// show the keyboard again
		showSoftKeyboard(password);
	}

}

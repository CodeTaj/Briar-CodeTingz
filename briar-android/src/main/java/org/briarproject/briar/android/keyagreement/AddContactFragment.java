package org.briarproject.briar.android.keyagreement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.fragment.BaseFragment;
import org.briarproject.briar.android.util.UiUtils;
import org.briarproject.briar.api.test.TestDataCreator;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class AddContactFragment extends BaseFragment implements TextWatcher,
		OnEditorActionListener, View.OnClickListener {

	private final static String TAG = AddContactFragment.class.getName();
	private static final Logger LOG = Logger.getLogger(TAG);

	private TextInputEditText emailInput;
	private TextInputLayout emailWrapper;
	private Button addContactButton;

	private FirebaseAuth mAuth;

	@Inject
	volatile ContactManager contactManager;

	public static AddContactFragment newInstance() {
		return new AddContactFragment();
	}

	@Inject
	TestDataCreator testDataCreator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle("Add Email Contact");
		View v = inflater.inflate(R.layout.activity_add_contact_by_email,
				container, false);
		emailInput = v.findViewById(R.id.edit_email);
		emailWrapper = v.findViewById(R.id.email_to_add_layout);
		addContactButton = v.findViewById(R.id.btn_add_by_email);
		
		addContactButton.setOnClickListener(this);

		FirebaseApp.initializeApp(this.getContext());
		mAuth = FirebaseAuth.getInstance();

		return v;
	}

	@Override
	public void injectFragment(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	public String getUniqueTag() {
		return TAG;
	}

	//makes sure that contact does not already exists locally
	public boolean checkForDuplicate(String author) throws DbException {
		for (Contact c : contactManager.getActiveContacts()) {
			LOG.info("Printing author: "+ c.getAuthor().getName());
				if(c.getAuthor().getName().equals(author)) {
					return true;
				}
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		String email = emailInput.getText().toString();
		addContactButton.setVisibility(INVISIBLE);

		if(!isEmailValid(email)){
			UiUtils.setError(emailWrapper, "Enter a valid Email", true);
			addContactButton.setVisibility(VISIBLE);
		} else {
			//checks database for existing user
			mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
	              @Override
	              public void onComplete(@NonNull Task<ProviderQueryResult> task) {
		              if (!(task.getResult().getProviders().size() > 0)) {
			              UiUtils.setError(emailWrapper,
					              "This email is not associated to a user",
					              true);
			              addContactButton.setVisibility(VISIBLE);
		              } else {
			              try {
				              if (!checkForDuplicate(email)) {
					              testDataCreator.createNewContact(email);
					              getActivity().finish();
				              } else {
					              UiUtils.setError(emailWrapper,
							              "Contact Already Exists",
							              true);
					              addContactButton.setVisibility(VISIBLE);
				              }
			              } catch (DbException e) {
				              e.printStackTrace();
			              }
		              }
	              }

	          });
		}
	}

	//checks email pattern
	public static boolean isEmailValid(String email) {
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
		onClick(textView);
		return true;
	}

	@Override
	public void afterTextChanged(Editable editable) {
	}
}

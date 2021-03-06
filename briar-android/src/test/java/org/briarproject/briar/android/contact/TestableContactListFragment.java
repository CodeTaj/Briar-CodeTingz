package org.briarproject.briar.android.contact;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TestableContactListFragment extends ContactListFragment {

	public List<ContactListItem> filter(String charText, List<ContactListItem> contactsListItems) {
		List<ContactListItem> contacts = new ArrayList<>();
		final String CHAR_TEXT_LOWER = charText.toLowerCase(Locale.getDefault());
		for (ContactListItem c : contactsListItems) {
			if (c.getContact().getAuthor().getName().toLowerCase(Locale.getDefault())
					.contains(CHAR_TEXT_LOWER)) {
				contacts.add(c);
			}
		}
		return contacts;
	}

	public String getLastMessage(Message mockMessage){
		String username = UserDetails.username;

		if (mockMessage.getFrom().equals(username)) {
			return "You: " + mockMessage.getMessage();
		} else {
			return mockMessage.getMessage();
		}
	}
}

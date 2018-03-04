package org.briarproject.briar.android.contact;

import android.view.View;
import android.widget.SearchView;

import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.identity.Author;
import org.briarproject.bramble.api.identity.AuthorId;
import org.briarproject.bramble.test.TestUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.TestBriarApplication;
import org.briarproject.briar.android.navdrawer.NavDrawerActivity;
import org.briarproject.briar.api.client.MessageTracker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_PUBLIC_KEY_LENGTH;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
		packageName = "org.briarproject.briar")
public class ContactListFragmentTest {

	private final static String AUTHOR_1 = "John";
	private final static String AUTHOR_2 = "Tom";
	private final static String AUTHOR_3 = "Don";

	private final static String[] AUTHORS = {
			AUTHOR_1, AUTHOR_2, AUTHOR_3
	};

	TestableContactListFragment contactListFragment = new TestableContactListFragment();
	private SearchView searchView;

	private List<ContactListItem> getDummyData() {
		ContactListItem[] contactListItems = new ContactListItem[3];
		Random random = new Random();
		Random random2 = new Random();
		for (int i = 0; i < contactListItems.length; i++) {
			// Create dummy authors
			AuthorId authorId = new AuthorId(TestUtils.getRandomId());
			byte[] publicKey = TestUtils.getRandomBytes(MAX_PUBLIC_KEY_LENGTH);
			Author author = new Author(authorId, AUTHORS[i], publicKey);

			//Create dummy contacts
			ContactId contactId = new ContactId(random.nextInt(5));
			Contact contact = new Contact(contactId, author, authorId, true, true);
			MessageTracker.GroupCount count = new MessageTracker.GroupCount(1,0, random2.nextInt(1000000000) );

			//Create dummy contact list items
			contactListItems[i] = new ContactListItem(contact, false, count);
		}
		List<ContactListItem> list = new ArrayList<>(3);
		list.addAll(Arrays.asList(contactListItems));
		return list;
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		startFragment(contactListFragment, NavDrawerActivity.class);

		View v = contactListFragment.getView();
		searchView = v.findViewById(R.id.action_search_contacts);
	}


	@Test
	public void testAmountOfItems() {
		List<ContactListItem> dummyData = getDummyData();
		contactListFragment.getAdapter().setItems(dummyData);
		assertEquals(3, contactListFragment.getAdapter().getItemCount());
	}

	@Test
	public void testFilterAlgorithm() {
		List<ContactListItem> filteredContacts = contactListFragment.filter("Tom", getDummyData());
		assertEquals(1, filteredContacts.size());
		assertEquals("Tom", filteredContacts.get(0).getContact().getAuthor().getName());
	}

}
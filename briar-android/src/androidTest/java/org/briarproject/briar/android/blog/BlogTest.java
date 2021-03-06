package org.briarproject.briar.android.blog;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.briarproject.briar.R;
import org.briarproject.briar.android.LoginTestSetup;
import org.briarproject.briar.android.ToolbarEspressoHelper;
import org.briarproject.briar.android.splash.SplashScreenActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;


@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BlogTest {

	@Rule
	public ActivityTestRule<SplashScreenActivity> mActivityTestRule =
			new ActivityTestRule<>(SplashScreenActivity.class);

	@Before
	public void setUp() {

		// SECTION: Log in if necessary

		Boolean loginTestSetup = LoginTestSetup.isUserAlreadyLoggedIn();

		if(!loginTestSetup) {
			// Allow app to open up properly in emulator
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Select Log in instead
			ViewInteraction login = onView(allOf(withId(R.id.btn_log_in),
					childAtPosition(childAtPosition(withClassName(is("android.widget.ScrollView")),
							0), 5)));
			login.perform(scrollTo(), click());

			// Allow page to be redirected
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Enter email address
			ViewInteraction emailAddress = onView(allOf(withId(R.id.edit_email),
					childAtPosition(childAtPosition(withId(R.id.email_layout), 0), 0)));
			emailAddress.perform(scrollTo(), replaceText("laxman@laxman.lax"), closeSoftKeyboard());

			// Enter password
			ViewInteraction password = onView(allOf(withId(R.id.edit_password),
					childAtPosition(childAtPosition(withId(R.id.password_layout), 0), 0)));
			password.perform(scrollTo(), replaceText("onetwothree"), closeSoftKeyboard());

			// Click log in
			ViewInteraction selectLogin = onView(allOf(withId(R.id.btn_sign_in),
					childAtPosition(childAtPosition(withClassName(is("android.widget.ScrollView")),
							0), 5)));
			selectLogin.perform(scrollTo(), click());
		}
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// SECTION 2: Open Blog in navigation for each test

		// Click navigation menu
		ViewInteraction navigationMenu = onView(allOf(childAtPosition(allOf(
				withId(R.id.toolbar), childAtPosition(withClassName(
						is("android.support.design.widget.AppBarLayout")), 0)), 1)));
		navigationMenu.perform(click());

		// Select the blogs option in the navigation menu
		ViewInteraction navOption = onView(childAtPosition(allOf(withId(
				R.id.design_navigation_view), childAtPosition(
				withId(R.id.navigation), 0)), 4));
		navOption.perform(scrollTo(), click());

		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the toolbar is "Blogs"
		ToolbarEspressoHelper.matchToolbarTitle("Blogs").check(matches(isDisplayed()));
	}

	@Test
	public void a_createBlogPostTest() {

		// Allow the page to load
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the title of the toolbar is "Blogs"
		ToolbarEspressoHelper.matchToolbarTitle("Blogs").check(matches(isDisplayed()));

		// Get the create blog button view
		ViewInteraction createBlogButton = onView(allOf(withId(R.id.action_write_blog_post),
				withContentDescription("Write Blog Post"), childAtPosition(childAtPosition(
						withId(R.id.toolbar), 2), 0)));

		// Assert the the create blog button is displayed
		createBlogButton.check(matches(isDisplayed()));

		// Click the create blog button
		createBlogButton.perform(click());

		// Allow the write blog page to load
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButton = onView(allOf(childAtPosition(allOf(withId(R.id.action_bar),
				childAtPosition(withId(R.id.action_bar_container), 0)), 0)));
		backButton.check(matches(isDisplayed()));

		// Assert that the toolbar title is "Write Blog Post"
		ToolbarEspressoHelper.matchToolbarTitle("Write Blog Post").check(matches(isDisplayed()));

		// Assert that the emoji button is displayed
		ViewInteraction emojiButton = onView(allOf(withId(R.id.emoji_toggle), childAtPosition(
				allOf(withId(R.id.input_layout), childAtPosition(withId(R.id.bodyInput), 0)), 0)));
		emojiButton.check(matches(isDisplayed()));

		// Get the blog post input box view
		ViewInteraction blogPostInputBox = onView(allOf(withId(R.id.input_text), childAtPosition(
				allOf(withId(R.id.input_layout), childAtPosition(withId(R.id.bodyInput), 0)), 1)));

		// Assert that the blog post input box is displayed
		blogPostInputBox.check(matches(isDisplayed()));

		// Write "First Blog" as our post text
		blogPostInputBox.perform(replaceText("First Blog"), closeSoftKeyboard());

		// Wait for the button to be displayed
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Get the publish blog button view
		ViewInteraction publishBlogButton = onView(allOf(withId(R.id.btn_send), withText("Publish"),
				childAtPosition(allOf(withId(R.id.bodyInput), childAtPosition(
						IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class), 0)), 1)));
		// Assert that the send blog post button is displayed
		publishBlogButton.check(matches(isDisplayed()));

		// Click publish blog
		publishBlogButton.perform(click());

		// Wait to be redirect to blog post list
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the blog post is displayed
		ViewInteraction blogMessage = onView(allOf(childAtPosition(childAtPosition(withId(
				R.id.postLayout), 0), 0)));
		blogMessage.check(matches(isDisplayed()));

		// Assert that the author name of the blog post is correct
		ViewInteraction blogAuthor = onView(allOf(withId(R.id.authorName),
				withText("laxman@laxman.lax")));
		blogAuthor.check(matches(withText("laxman@laxman.lax")));

		// Assert that the blog post text is what we typed
		ViewInteraction blogPostMessage = onView(allOf(withId(R.id.bodyView), isDisplayed()));
		blogPostMessage.check(matches(withText("First Blog")));

		// Assert that the reblog button is displayed
		ViewInteraction reblogButton = onView(allOf(withId(R.id.commentView),
						withContentDescription("Add a comment (optional)")));
		reblogButton.check(matches(isDisplayed()));
	}


	@Test
	public void b_reBlogTest() {

		// Allow the page to load
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Get the reblog button view
		ViewInteraction reblogButton = onView(allOf(withId(R.id.commentView), withContentDescription(
				"Add a comment (optional)")));

		// Assert that the reblog button is displayable
		reblogButton.check(matches(isDisplayed()));

		// Click the reblog button
		reblogButton.perform(click());

		// Wait for the reblog post page to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButton = onView(allOf(withContentDescription("Navigate up")));
		backButton.check(matches(isDisplayed()));

		// Assert that the toolbar title is "Write Blog Post"
		ToolbarEspressoHelper.matchToolbarTitle("Reblog").check(matches(isDisplayed()));

		// Assert that the blog post input box is displayed
		ViewInteraction blogPostInputBox = onView(allOf(childAtPosition(childAtPosition(
				withId(R.id.postLayout), 0), 0)));
		blogPostInputBox.check(matches(isDisplayed()));

		// Assert that the emoji button is displayed
		ViewInteraction emojiButton = onView(allOf(withId(R.id.emoji_toggle)));
		emojiButton.check(matches(isDisplayed()));

		// Add comment "Test Reblog" to reblog
		ViewInteraction reblogComment = onView(allOf(withId(R.id.input_text)));
		reblogComment.perform(replaceText("Test Reblog"), closeSoftKeyboard());

		// Assert that the publish blog button is displayed
		ViewInteraction publishBlogButton = onView(allOf(withId(R.id.btn_send), withText("Reblog")));
		publishBlogButton.check(matches(isDisplayed()));

		// Click the publish blog button
		publishBlogButton.perform(click());

		// Wait to be redirect to blog post list
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the blog post message box is displayed
		ViewInteraction blogPostMessage = onView(
				allOf(withId(R.id.postLayout), childAtPosition(allOf(withId(R.id.recyclerView),
						childAtPosition(IsInstanceOf.<View>instanceOf(
								android.widget.RelativeLayout.class), 0)), 0)));
		blogPostMessage.check(matches(isDisplayed()));

		// Assert that the comment to the reblog is correct
		ViewInteraction blogPostComment = onView(allOf(withId(R.id.bodyView), withText("Test Reblog"),
				childAtPosition(childAtPosition(withId(R.id.commentContainer), 0), 2)));
		blogPostComment.check(matches(withText("Test Reblog")));
	}

	@Test
	public void c_importRSSFeedTest() {

		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the options button is displayed
		ViewInteraction optionsButton = onView(allOf(withContentDescription("More options"),
				childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 1)));
		optionsButton.check(matches(isDisplayed()));

		// Open the options menu
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

		// Wait for the options menu to open
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the "Import RSS Feed" is a option
		ViewInteraction selectOption = onView(allOf(withId(R.id.title), withText("Import RSS Feed"),
				childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
						android.widget.LinearLayout.class), 0), 0)));
		selectOption.check(matches(withText("Import RSS Feed")));

		// Click the "Import RSS Feed" option
		ViewInteraction clickOption = onView(allOf(withId(R.id.title), withText(
				"Import RSS Feed"), childAtPosition(childAtPosition(withClassName(is(
						"android.support.v7.view.menu.ListMenuItemView")), 0), 0)));
		clickOption.perform(click());

		// Wait for the import rss feed page to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButton = onView(allOf(withContentDescription("Navigate up")));
		backButton.check(matches(isDisplayed()));

		// Assert that the title of the page is "Import RSS Feed"
		ToolbarEspressoHelper.matchToolbarTitle("Import RSS Feed").check(matches(isDisplayed()));

		// Assert that the rss feed url input box is displayed
		ViewInteraction urlInputBox = onView(allOf(withId(R.id.urlInput)));
		urlInputBox.check(matches(isDisplayed()));

		// Assert that the rss feed import button is displayed
		ViewInteraction rssFeedImportButton = onView(allOf(withId(R.id.importButton), childAtPosition(
				childAtPosition(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class), 0),
				1)));
		rssFeedImportButton.check(matches(isDisplayed()));

		// Click the back button to return to blog list
		ViewInteraction backButtonTwo = onView(allOf(
				withContentDescription("Navigate up")));
		backButtonTwo.perform(click());
	}

	@Test
	public void d_manageRSSFeedTest() {

		// Wait for the page to load
		try {
			Thread.sleep(7500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the option button is displayed
		ViewInteraction optionButton = onView(
				allOf(withContentDescription("More options"), childAtPosition(childAtPosition(
						withId(R.id.toolbar), 2), 1)));
		optionButton.check(matches(isDisplayed()));

		// Open the options
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

		// Wait for the options to load
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that "Manage RSS Feeds" is a option
		ViewInteraction manageRSSoption = onView(allOf(withId(R.id.title), withText("Manage RSS Feeds"),
				childAtPosition(childAtPosition(IsInstanceOf.<View>instanceOf(
						android.widget.LinearLayout.class), 0), 0)));
		manageRSSoption.check(matches(withText("Manage RSS Feeds")));

		// Click the "Manage RSS Feeds" option
		ViewInteraction selectManageOption = onView(allOf(withId(R.id.title), withText(
				"Manage RSS Feeds"), childAtPosition(childAtPosition(withClassName(is(
						"android.support.v7.view.menu.ListMenuItemView")), 0), 0)));
		selectManageOption.perform(click());

		// Wait for the manage rss feeds page to load
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButton = onView(allOf(withContentDescription("Navigate up")));
		backButton.check(matches(isDisplayed()));

		// Assert that the title of the page is "Manage RSS Feeds"
		ToolbarEspressoHelper.matchToolbarTitle("Manage RSS Feeds").check(matches(isDisplayed()));

		// Assert that the import rss feed is displayed
		ViewInteraction importRssFeedButton = onView(allOf(withId(R.id.action_rss_feeds_import),
				withContentDescription("Import RSS Feed"), childAtPosition(childAtPosition(
										withId(R.id.action_bar), 2), 0)));
		importRssFeedButton.check(matches(isDisplayed()));

		// Assert that the feed list is displayed
		ViewInteraction feedList = onView(allOf(childAtPosition(allOf(withId(R.id.feedList),
				childAtPosition(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
						0)), 0)));
		feedList.check(matches(isDisplayed()));

		// Click the import rss feed
		ViewInteraction importRSSFeedButon = onView(allOf(withId(R.id.action_rss_feeds_import),
				withContentDescription("Import RSS Feed"), childAtPosition(childAtPosition(
						withId(R.id.action_bar), 2), 0)));
		importRSSFeedButon.perform(click());

		// Wait for the import rss feed to open
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assert that the back button is displayed
		ViewInteraction backButtonTwo = onView(
				allOf(withContentDescription("Navigate up"), isDisplayed()));
		backButtonTwo.check(matches(isDisplayed()));

		// Assert that the title of the page is "Import RSS Feed"
		ToolbarEspressoHelper.matchToolbarTitle("Import RSS Feed").check(matches(isDisplayed()));

		// Assert that that the import rss input is displayed
		ViewInteraction importRSSInputBox = onView(allOf(withId(R.id.urlInput)));
		importRSSInputBox.check(matches(isDisplayed()));

		// Assert that the import rss feed button is displayed
		ViewInteraction importRSSButton = onView(allOf(withId(R.id.importButton), childAtPosition(
				childAtPosition(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class), 0),
				1)));
		importRSSButton.check(matches(isDisplayed()));

		// Click back to return to blog list
		ViewInteraction backButtonThree = onView(allOf(withContentDescription("Navigate up")));
		backButtonThree.perform(click());
	}

	private static Matcher<View> childAtPosition(
			final Matcher<View> parentMatcher, final int position) {

		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Child at position " + position + " in parent ");
				parentMatcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				ViewParent parent = view.getParent();
				return parent instanceof ViewGroup &&
						parentMatcher.matches(parent) &&
						view.equals(((ViewGroup) parent).getChildAt(position));
			}
		};
	}
}

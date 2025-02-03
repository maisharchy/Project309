package com.example.androidexample;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.androidexample.LoginActivity;
import com.example.androidexample.ForgotPasswordActivity;
import com.example.androidexample.CreateServerActivity;
import com.example.androidexample.JoinServerActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest   // large execution time
public class ClairSystemTest {

    private static final int SIMULATED_DELAY_MS = 500;

    @Rule
    public ActivityScenarioRule<JoinServerActivity> test = new ActivityScenarioRule<>(JoinServerActivity.class);

    @Test
    public void joinServerTest() {
        String userID = "23";
        String serverID = "14";
        String response = "User successfully joined the server as a player";

        test.getScenario().onActivity(activity -> {
            activity.testing = true;
        });

        // Type in testString and send request
        onView(withId(R.id.joinIDEdit)).perform(typeText(serverID), closeSoftKeyboard());
        onView(withId(R.id.btnJoinServer)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        onView(withId(R.id.testText1)).check(matches(withText(endsWith(response))));
    }

}


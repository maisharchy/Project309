package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest   // large execution time
public class ClairSystemTest3 {

    private static final int SIMULATED_DELAY_MS = 500;

    @Rule
//    public ActivityScenarioRule<JoinServerActivity> test = new ActivityScenarioRule<>(JoinServerActivity.class);
//    public ActivityScenarioRule<CreateServerActivity> test = new ActivityScenarioRule<>(CreateServerActivity.class);
    public ActivityScenarioRule<ForgotPasswordActivity> test = new ActivityScenarioRule<>(ForgotPasswordActivity.class);
//    public ActivityScenarioRule<LoginActivity> test = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void forgotPasswordTest(){
        String email = "clariley@iastate.edu";
        String response = "23";

        test.getScenario().onActivity(activity -> {
            activity.testing = true;
        });

        // Type in testString and send request
        onView(withId(R.id.resetEmailEdit)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.btnResetPassword)).perform(click());
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}
        onView(withId(R.id.testText3)).check(matches(withText(endsWith(response))));
    }
}


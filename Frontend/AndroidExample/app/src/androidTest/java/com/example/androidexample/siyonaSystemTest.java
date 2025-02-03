package com.example.androidexample;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.androidexample.LoginActivity;
import com.example.androidexample.ForgotPasswordActivity;
import com.example.androidexample.UserReportActivity;
import com.example.androidexample.TileFlipActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;

import android.app.Instrumentation;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest   // large execution time
public class siyonaSystemTest extends Instrumentation.ActivityMonitor {

    private static final int SIMULATED_DELAY_MS = 500;

    @Rule
    public ActivityScenarioRule<UserReportActivity> test = new ActivityScenarioRule<>(UserReportActivity.class);

    @Test
    public void signUpButtonTest() {
        String username = "user1";
        String password = "pass1";
        String response = "User successfully joined the server as a player";

        Intents.init();

        onView(withId(R.id.btnSignUp)).perform(click());

        test.getScenario().onActivity(activity -> {
            activity.testing = true;
        });

//        // Type in testString and send request
//        onView(withId(R.id.joinIDEdit)).perform(typeText(serverID), closeSoftKeyboard());
//        onView(withId(R.id.btnJoinServer)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        //onView(withId(R.id.testText1)).check(matches(withText(endsWith(response))));

        intended(hasComponent(SignUpActivity.class.getName()));
        Intents.release();
    }

}


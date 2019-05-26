package com.vtg.myvtgph.myvtg;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);
    private LoginActivity loginActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(HomeFrag.class.getName(),null,false);

    @Before
    public void setUp() throws Exception {
        loginActivity  = loginActivityActivityTestRule.getActivity();
    }

    @Test
    public void testLaunchOfLoginToHomePage() {
        assertNotNull(loginActivity.findViewById( R.id.txt_email) );
        assertNotNull(loginActivity.findViewById( R.id.txt_pass) );
        assertNotNull(loginActivity.findViewById( R.id.btn_login) );

        onView(withId(R.id.txt_email)).perform(typeText("ezekielvillaester@gmail.com"));
        onView(withId(R.id.txt_pass)).perform(typeText("123456"));
        onView(withId(R.id.btn_login)).perform(click());

        Activity HomeFrag =  getInstrumentation().waitForMonitorWithTimeout( monitor,5000 );

//        assertNotNull( HomeFrag );
//        HomeFrag.finish();
        }

    @After
    public void tearDown() throws Exception {
        loginActivity = null;
    }
}
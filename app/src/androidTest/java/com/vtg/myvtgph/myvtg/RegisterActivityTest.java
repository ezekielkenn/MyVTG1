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
import static org.junit.Assert.*;

public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> registerActivityActivityTestRule = new ActivityTestRule<RegisterActivity>(RegisterActivity.class);
    private RegisterActivity registerActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(LoginActivity.class.getName(),null,false);

    @Before
    public void setUp() throws Exception {
        registerActivity  = registerActivityActivityTestRule.getActivity();
    }

    @Test
    public void testLaunchOfRegister() {
        assertNotNull(registerActivity.findViewById( R.id.txt_firstname) );
        assertNotNull(registerActivity.findViewById( R.id.txt_lastname) );
        assertNotNull(registerActivity.findViewById( R.id.txt_mi) );
        assertNotNull(registerActivity.findViewById( R.id.txt_regemail) );
        assertNotNull(registerActivity.findViewById( R.id.txt_address) );
        assertNotNull(registerActivity.findViewById( R.id.txt_regpass) );
        assertNotNull(registerActivity.findViewById( R.id.txt_confirmpass) );
        assertNotNull(registerActivity.findViewById( R.id.btn_done) );

        onView(withId(R.id.txt_firstname)).perform(typeText("No name"));
        onView(withId(R.id.txt_lastname)).perform(typeText("asdkahd"));
        onView(withId(R.id.txt_mi)).perform(typeText("F"));
        onView(withId(R.id.txt_regemail)).perform(typeText("try99@yahoo.com"));
        onView(withId(R.id.txt_address)).perform(typeText("Cebu City"));
        onView(withId(R.id.txt_regpass)).perform(typeText("654321"));
        onView(withId(R.id.txt_confirmpass)).perform(typeText("654321"));
        onView(withId(R.id.btn_done)).perform(click());

        Activity LoginActivity =  getInstrumentation().waitForMonitorWithTimeout( monitor,5000 );

//        assertNotNull( LoginActivity );
//        HomeFrag.finish();
    }

    @After
    public void tearDown() throws Exception {
        registerActivity = null;
    }
}
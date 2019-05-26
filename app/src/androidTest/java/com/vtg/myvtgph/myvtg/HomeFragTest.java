package com.vtg.myvtgph.myvtg;

import android.support.constraint.ConstraintLayout;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

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

public class HomeFragTest {

    @Rule
    public ActivityTestRule<TestActivity>activityActivityTestRule = new ActivityTestRule<>( TestActivity.class );

    private TestActivity activity = null;


    @Before
    public void setUp() throws Exception {
        activity = activityActivityTestRule.getActivity();
    }

    @Test
    public void testHomeRes() {
        ConstraintLayout rlContainer = (ConstraintLayout) activity.findViewById(R.id.ic_menu_date);

//        assertNotNull( rlContainer );

        HomeFrag homeFrag = new HomeFrag();
        activity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),new HomeFrag()).commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

        View view = homeFrag.getView().findViewById( R.id.container );
        assertNotNull( view );
        onView(withId(R.id.ic_menu_date)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        activity = null;
    }
}
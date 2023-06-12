package com.example.QAapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAction;

import org.junit.Before;
import org.junit.Test;

public class MainActivityTest {

    @Test
    public void isElementDisplayed(){
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.buttonPage)).check(matches(isDisplayed()));
        onView(withId(R.id.webpage)).check(matches(isDisplayed()));
        onView(withId(R.id.hybridView)).check(matches(isDisplayed()));
        onView(withId(R.id.GPS)).check(matches(isDisplayed()));
    }

    @Test
    public void idElementClickable(){
        ActivityScenario activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.webpage)).perform(click());
        onView(withId(R.id.findButton)).check(matches(isDisplayed()));
        onView(withId(R.id.loadWebPage)).check(matches(isDisplayed()));
    }
}
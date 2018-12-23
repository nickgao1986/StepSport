package com.example.tutorial;

import android.view.ViewGroup;

public interface TutorialActivityInterface {
    ViewGroup getViewRootForTip();

    void onViewRootResume();

    void onViewRootPause();

    boolean isTipShowing();
}

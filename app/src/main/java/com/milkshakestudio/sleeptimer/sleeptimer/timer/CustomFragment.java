package com.milkshakestudio.sleeptimer.sleeptimer.timer;

/**
 * Created by Syndkate on 2017-03-05.
 * --Abhishek--Milkshake Studios
 */

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;


public abstract class CustomFragment extends Fragment implements FabContainer, FabController {


    /** The container that houses the fab and its left and right buttons. */
    private FabContainer mFabContainer;

    public CustomFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // By default return false so event continues to propagate
        return false;
    }

    @Override
    public void onLeftButtonClick(@NonNull Button left) {
        // Do nothing here, only in derived classes
    }

    @Override
    public void onRightButtonClick(@NonNull Button right) {
        // Do nothing here, only in derived classes
    }

    @Override
    public void onMorphFab(@NonNull ImageView fab) {
        // Do nothing here, only in derived classes
    }

    /**
     * @param color the newly installed app window color
     */
    protected void onAppColorChanged(@ColorInt int color) {
        // Do nothing here, only in derived classes
    }

    /**
     * @param fabContainer the container that houses the fab and its left and right buttons
     */
    public final void setFabContainer(FabContainer fabContainer) {
        mFabContainer = fabContainer;
    }

    /**
     * Requests that the parent activity update the fab and buttons.
     *
     * @param updateTypes the manner in which the fab container should be updated
     */
    @Override
    public final void updateFab(@UpdateFabFlag int updateTypes) {
        if (mFabContainer != null) {
            mFabContainer.updateFab(updateTypes);
        }
    }

}
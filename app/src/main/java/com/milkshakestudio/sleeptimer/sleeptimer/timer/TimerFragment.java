package com.milkshakestudio.sleeptimer.sleeptimer.timer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.milkshakestudio.sleeptimer.sleeptimer.R;

/**
 * Created by Syndkate on 2017-03-05.
 * --Abhishek--Milkshake Studios
 */

public final class TimerFragment extends CustomFragment {

    public TimerFragment(){

    }

    TimerSetupView mCreateTimerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.timer_layout, container, false);

//        mCreateTimerView = (TimerSetupView) view.findViewById(R.id.timer_setup);
//        mCreateTimerView.setFabContainer(this);
        return view;
    }

    @Override
    public void onUpdateFab(@NonNull ImageView fab) {

    }

    @Override
    public void onUpdateFabButtons(@NonNull Button left, @NonNull Button right) {

    }

    @Override
    public void onFabClick(@NonNull ImageView fab) {

    }
}

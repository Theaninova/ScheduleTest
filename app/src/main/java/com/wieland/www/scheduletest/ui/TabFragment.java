package com.wieland.www.scheduletest.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wieland.www.scheduletest.R;
import com.wieland.www.scheduletest.schedule.ScheduleHandler;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Wieland on 07.05.2017.
 */

public class TabFragment extends Fragment {

    SwipeRefreshLayout swipeRefresh;
    RecyclerView myList;
    public Context context;
    ProgressBar progressBar;
    View view;
    int index;

    OnHeadlineSelectedListener mCallback;
    public interface OnHeadlineSelectedListener {
        void onRefreshed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_main, container, false);
        return view;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            this.index = (int) savedInstanceState.get("Index");
        }

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh2);
        myList = (RecyclerView) view.findViewById(R.id.theListOfDoom);
        progressBar = view.findViewById(R.id.progressBar);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCallback.onRefreshed();
            }
        });

        context = getContext();
        mCallback = (OnHeadlineSelectedListener) context;

        SetTextTask setText = new SetTextTask(index, context);
        setText.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("Index", index);
    }

    public void refresh() {
        SetTextTask setText = new SetTextTask(index, context);
        setText.execute();
    }

    public void swipeRefreshOff() {
        swipeRefresh.setRefreshing(false);
    }

    public class SetTextTask extends AsyncTask<Void, Void, Boolean> {
        private final Context context;
        private Layout_Row adapter;
        private int index;

        SetTextTask (int index, Context context) {
            this.context = context;
            this.index = index;
            showProgress(true);
        }

        public void showProgress(final boolean show) {
            final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            myList.setVisibility(show ? View.GONE : View.VISIBLE);
            myList.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    myList.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }

        @Override
        public Boolean doInBackground(Void... params) {
            ArrayList<String> willBeSet = new ArrayList<>();

            ArrayList<ArrayList<android.text.Spanned>> listInList = new ArrayList<>();

            ScheduleHandler myHandler = new ScheduleHandler(index, context);
            SharedPreferences pref = context.getSharedPreferences("Tralala", MODE_PRIVATE);
            if(pref.getInt("customizedLayout2", 1) == 2) {
                try {
                    willBeSet = myHandler.getClassListPersonalized();
                } catch (Exception e) {}
            } else if (pref.getInt("customizedLayout2", 1) == 1)
                willBeSet = myHandler.getClassList();
            else
                try {
                    willBeSet = myHandler.getClassListCustom();
                } catch (Exception e) {}

            for (int i = 0; i < willBeSet.size(); i++) {
                if(pref.getInt("customizedLayout2", 1) == 2) {
                    try {
                        listInList.add(myHandler.getClassInfoPersonalized(willBeSet.get(i)));
                    } catch (Exception e) {}
                } else if (pref.getInt("customizedLayout2", 1) == 1)
                    listInList.add(myHandler.getClassInfo(willBeSet.get(i)));
                else
                    try {
                        listInList.add(myHandler.getClassInfoCustom(willBeSet.get(i)));
                    } catch (Exception e) {}
            }

            Layout_Row adapter = new Layout_Row(willBeSet, listInList, this.context);
            this.adapter = adapter;

            return true;
        }

        @Override
        public void onPostExecute(final Boolean success) {
            myList.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this.context);
            myList.setLayoutManager(layoutManager);
            myList.setAdapter(this.adapter);

            SharedPreferences pref = getActivity().getSharedPreferences("Tralala", MODE_PRIVATE);

            //Header set username, just some cosmetic stuff
            NavigationView menu2 = (NavigationView) getActivity().findViewById(R.id.nav_view);
            View mHeaderView = menu2.getHeaderView(0);
            TextView username_view = (TextView) mHeaderView.findViewById(R.id.textView_username);
            username_view.setText(pref.getString("set_username", "[Nutzername]"));
            //setTitle(Schedule.getDate(index, this.context));
            swipeRefresh.setRefreshing(false);
            showProgress(false);
        }
    }
}

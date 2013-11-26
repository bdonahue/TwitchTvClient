package com.wdonahue.rapidparsing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdonahue.rapidparsing.adapters.JustinTvStreamAdapter;
import com.wdonahue.rapidparsing.model.JustinTvStreamData;
import com.wdonahue.rapidparsing.utils.Web;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    /**
     * When this amount of items is left in the ListView yet to be displayed we will start downloading more data (if available).
     */
    private static final int RUNNING_LOW_ON_DATA_THRESHOLD = 10;

    private static final int ITEMS_PER_PAGE = 50;

    private static final int MS_IN_FOUR_HOURS = 14400000;

    private JustinTvStreamAdapter mAdapter;

    private ProgressBar mProgressBar;

    private boolean mIsDownloadInProgress = false;

    private static class ActivityState {
        private int nextPage = 0;

        private List<JustinTvStreamData> streamData = new ArrayList<JustinTvStreamData>();
    }

    /* Holds the state information for this activity. */
    private ActivityState mState = new ActivityState();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getLastNonConfigurationInstance() instanceof ActivityState) {
            mState = (ActivityState) getLastNonConfigurationInstance();
        }

        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Create the array adapter and bind it to the gridview
        GridView gridView = (GridView) findViewById(R.id.grid);
        gridView.setOnScrollListener(mScrollListener);
        mAdapter = new JustinTvStreamAdapter(this, 0, mState.streamData);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = mState.streamData.get(position).getChannel().getTitle();

                Intent viewVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitch.tv/" + title));
                startActivity(viewVideoIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Kick off first download
        if (mState.nextPage == 0) {
            downloadData(mState.nextPage);
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        // Return our state so we can later restore it in onCreate() via getLastNonConfigurationInstance();
        return mState;
    }

    /**
     * Scroll-handler for the ListView which can auto-load the next page of data.
     */
    private AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Nothing to do
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            // Detect if the ListView is running low on data
            if (totalItemCount > 0 && totalItemCount - (visibleItemCount + firstVisibleItem) <= RUNNING_LOW_ON_DATA_THRESHOLD) {
                downloadData(mState.nextPage);
            }
        }
    };

    private void downloadData(final int pageNumber) {
        if (!mIsDownloadInProgress) {
            mIsDownloadInProgress = true;

            mProgressBar.setVisibility(View.VISIBLE);

            // Kick off the download using an AsyncTask
            (new AsyncTask<Void, Void, List<JustinTvStreamData>>() {
                @Override
                protected List<JustinTvStreamData> doInBackground(Void... params) {
                    Gson gson = new Gson();

                    // Download the web site contents into a string
                    String json = Web.getWebsite("http://api.justin.tv/api/stream/list.json?limit=100&offset=" + Integer.toString(pageNumber * ITEMS_PER_PAGE));

                    // Strip off the leading and trailing [] GSON does not like that
                    // data = data.substring(1, data.length() - 1);

                    // Parse the web site contents into our response object
                    // return gson.fromJson(data, JustinTvStreams.class);

                    // See https://sites.google.com/site/gson/gson-user-guide#TOC-Array-Examples for howto
                    // This is extra hoekey because the root of this JSON is an array
                    // If the root of the JSON was an object we could just do
                    // something like return gson.fromJson(data, JustinTvStreams.class);
                    Type collectionType = new TypeToken<List<JustinTvStreamData>>() {
                    }.getType();
                    return gson.fromJson(json, collectionType);
                }

                @Override
                protected void onPostExecute(List<JustinTvStreamData> streams) {
                    super.onPostExecute(streams);

                    if (streams != null) {
                        long currentTime = System.currentTimeMillis();

                        for (JustinTvStreamData stream : streams) {
                            Date uptime = new Date(stream.getUp_time());
                            long uptimeMs = uptime.getTime();

                            if (currentTime - uptimeMs < MS_IN_FOUR_HOURS) {
                                stream.isNew = true;
                            }

                            // Add the found streams to our array to render
                            mState.streamData.addAll(streams);
                        }

                        // Tell the adapter that it needs to rerender
                        mAdapter.notifyDataSetChanged();

                        // Done loading; remove loading indicator
                        mProgressBar.setVisibility(View.GONE);

                        mState.nextPage++;
                    }

                    mIsDownloadInProgress = false;
                }
            }).execute();
        }
    }
}

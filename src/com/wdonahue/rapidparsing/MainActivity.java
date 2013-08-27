package com.wdonahue.rapidparsing;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdonahue.rapidparsing.adapters.JustinTvStreamAdapter;
import com.wdonahue.rapidparsing.model.JustinTvStreamData;
import com.wdonahue.rapidparsing.utils.Web;

public class MainActivity extends Activity {
	private JustinTvStreamAdapter mAdapter;
	private List<JustinTvStreamData> mStreamData = new ArrayList<JustinTvStreamData>();
	private ProgressBar mProgressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		// Create the array adapter and bind it to the gridview
		GridView gridView = (GridView) findViewById(R.id.grid);
		mAdapter = new JustinTvStreamAdapter(this, 0, mStreamData);
		gridView.setAdapter(mAdapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		downloadData();
	}

	private void downloadData() {
		// Kick off the download using an AsyncTask
		(new AsyncTask<Void, Void, List<JustinTvStreamData>>() {
			@Override
			protected List<JustinTvStreamData> doInBackground(Void... params) {
				Gson gson = new Gson();

				// Download the web site contents into a string
				String json = Web.getWebsite("http://api.justin.tv/api/stream/list.json");

				// Strip off the leading and trailing [] GSON does not like that
				// data = data.substring(1, data.length() - 1);

				// Parse the web site contents into our response object
				// return gson.fromJson(data, JustinTvStreams.class);

				// See https://sites.google.com/site/gson/gson-user-guide#TOC-Array-Examples for howto
				// This is extra hoekey because the root of this JSON is an array
				// If the root of the JSON was an object we could just do
				// something like return gson.fromJson(data, JustinTvStreams.class);
				Type collectionType = new TypeToken<List<JustinTvStreamData>>() {}.getType();
				return gson.fromJson(json, collectionType);
			}

			@Override
			protected void onPostExecute(List<JustinTvStreamData> streams) {
				super.onPostExecute(streams);

				if (streams != null) {
					// Add the found streams to our array to render
					mStreamData.clear();
					mStreamData.addAll(streams);
					
					// Tell the adapter that it needs to rerender
					mAdapter.notifyDataSetChanged();
					
					// Done loading; remove loading indicator
					mProgressBar.setVisibility(View.GONE);
				}
			}
		}).execute();
	}
}

package com.wdonahue.twitchtvclient.api;

import com.wdonahue.twitchtvclient.model.JustinTvStreamData;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

public class ApiClient {
    private static TwitchTvApiInterface sTwitchTvService;

    public static TwitchTvApiInterface getTwitchTvApiClient() {
        if (sTwitchTvService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://api.justin.tv/api")
                    .build();

            sTwitchTvService = restAdapter.create(TwitchTvApiInterface.class);
        }

        return sTwitchTvService;
    }

    public interface TwitchTvApiInterface {
        @GET("/stream/list.json")
        void getStreams(@Query("limit") int limit, @Query("offset") int offset, Callback<List<JustinTvStreamData>> callback);
    }
}

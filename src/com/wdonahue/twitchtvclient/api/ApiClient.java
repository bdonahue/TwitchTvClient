package com.wdonahue.twitchtvclient.api;

import com.wdonahue.twitchtvclient.model.JustinTvStreamData;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

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
        @GET("/stream/list.json?limit={limit}&offset={offset}")
        void getStreams(@Path("limit") int limit, @Path("offset") int offset, Callback<List<JustinTvStreamData>> callback);
    }
}

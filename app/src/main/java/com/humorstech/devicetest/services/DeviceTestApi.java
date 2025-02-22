package com.humorstech.devicetest.services;


import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * DeviceTestApi is a helper class that encapsulates API calls for inserting device test data.
 * It uses the loopj asynchronous HTTP client.
 */
public class DeviceTestApi {

    private static final String TAG = "DeviceTestApi";
    private static final String BASE_URL = "https://humorstech.com/humors_app/app_final/device_test/api.php?";

    /**
     * Callback interface for API responses.
     */
    public interface ApiCallback {
        /**
         * Called when the API call is successful.
         *
         * @param response The response returned by the API.
         */
        void onSuccess(String response);

        /**
         * Called when the API call fails.
         *
         * @param error The Throwable error encountered.
         */
        void onFailure(Throwable error);
    }

    /**
     * Inserts device test data by calling the API.
     * <p>
     * This method uses fixed parameters. You can modify it to accept parameters as needed.
     *
     * @param callback A callback to handle the API response.
     */
    public static void insertDeviceTest(ApiCallback callback, String action,
                                        String hardwareId,
                                        String i2c,
                                        String aht_20,
                                        String bmp_180,
                                        String uv_light,
                                        String ags,
                                        String acetone,
                                        String h2,
                                        String all_gases,
                                        String full_system_check,
                                        String test_done_by


                                        ) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        // Set up query parameters
        params.put("action", action);
        params.put("hardware_id", hardwareId);
        params.put("i2c", i2c);
        params.put("aht_20", aht_20);
        params.put("bmp_180", bmp_180);
        params.put("uv_light", uv_light);
        params.put("ags", ags);
        params.put("acetone", acetone);
        params.put("h2", h2);
        params.put("all_gases", all_gases);
        params.put("full_system_check", full_system_check);
        params.put("test_done_by", test_done_by);

        // Execute GET request
        client.get(BASE_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody, "UTF-8");
                    Log.d(TAG, "API Response: " + response);
                    if (callback != null) {
                        callback.onSuccess(response);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing response: " + e.getMessage(), e);
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "API Request failed: " + error.getMessage(), error);
                if (callback != null) {
                    callback.onFailure(error);
                }
            }
        });
    }
}

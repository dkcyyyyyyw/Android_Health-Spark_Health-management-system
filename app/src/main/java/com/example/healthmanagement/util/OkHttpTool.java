package com.example.healthmanagement.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpTool {
    private static final String TAG = "OkHttpTool";
    private static final OkHttpClient myOkHttpClient;

    public interface ResponseCallback {
        void onResponse(boolean success, int statusCode, String response, Exception e);
    }

    static {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.i(TAG, message));
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        myOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                })
                .build();
    }

    public static void httpGet(String url, Map<String, Object> parameters, ResponseCallback responseCallback) {
        doRequest(createGetRequest(url, parameters), responseCallback);
    }

    public static void httpPost(String url, Map<String, Object> parameters, ResponseCallback responseCallback) {
        doRequest(createPostRequest(url, parameters), responseCallback);
    }

    public static void httpPostJson(String url, String json, ResponseCallback responseCallback) {
        doRequest(createPostRequestJson(url, json), responseCallback);
    }

    public static void httpPostWithFile(String url, Map<String, Object> parameters, Map<String, File> files, ResponseCallback responseCallback) {
        doRequest(createPostRequestWithFile(url, parameters, files), responseCallback);
    }

    public static void httpPostWithFileByte(String url, Map<String, Object> parameters, Map<String, byte[]> files, ResponseCallback responseCallback) {
        doRequest(createPostRequestWithFileByte(url, parameters, files), responseCallback);
    }

    // 使用 Glide 加载图片的方法
    public void loadImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        Log.e(TAG, "Image load failed");
                    }
                });
    }

    private static Request createGetRequest(String url, Map<String, Object> parameters) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url);
        if (url.indexOf('?') <= -1) {
            urlBuilder.append("?");
        }
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            urlBuilder.append("&")
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue().toString());
        }
        return getBaseRequest().url(urlBuilder.toString()).build();
    }

    private static Request createPostRequest(String url, Map<String, Object> parameters) {
        FormBody.Builder builder = new FormBody.Builder(Charset.forName("UTF-8"));
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }
        return getBaseRequest().url(url).post(builder.build()).build();
    }

    private static Request createPostRequestJson(String url, String json) {
        return getBaseRequest()
                .url(url)
                .post(RequestBody.create(json, MediaType.parse("application/json;charset=utf-8")))
                .build();
    }

    private static Request createPostRequestWithFile(String url, Map<String, Object> parameters, Map<String, File> files) {
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (files != null) {
            for (Map.Entry<String, File> fileEntry : files.entrySet()) {
                File file = fileEntry.getValue();
                if (file != null) {
                    RequestBody body = RequestBody.create(file, MediaType.parse("application/octet-stream"));
                    requestBody.addFormDataPart(fileEntry.getKey(), file.getName(), body);
                }
            }
        }
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                requestBody.addFormDataPart(entry.getKey(), entry.getValue().toString());
            }
        }
        return getBaseRequest().url(url).post(requestBody.build()).build();
    }

    private static Request createPostRequestWithFileByte(String url, Map<String, Object> parameters, Map<String, byte[]> files) {
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (files != null) {
            for (Map.Entry<String, byte[]> fileEntry : files.entrySet()) {
                byte[] file = fileEntry.getValue();
                if (file != null) {
                    requestBody.addFormDataPart(fileEntry.getKey(), fileEntry.getKey(),
                            RequestBody.create(file, MediaType.parse("application/octet-stream")));
                }
            }
        }
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                requestBody.addFormDataPart(entry.getKey(), entry.getValue().toString());
            }
        }
        return getBaseRequest().url(url).post(requestBody.build()).build();
    }

    private static void doRequest(Request request, final ResponseCallback responseCallback) {
        myOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseCallback.onResponse(false, -1, null, e);
                if (e.getMessage() != null) {
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int responseCode = response.code();
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful() || responseBody == null) {
                    responseCallback.onResponse(false, responseCode, null, null);
                    return;
                }
                responseCallback.onResponse(true, responseCode, responseBody.string(), null);
            }
        });
    }

    private static Request.Builder getBaseRequest() {
        Request.Builder builder = new Request.Builder();
        builder.addHeader("client", "Android");
        return builder;
    }
}

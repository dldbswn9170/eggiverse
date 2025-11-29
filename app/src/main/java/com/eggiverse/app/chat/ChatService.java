package com.eggiverse.app.chat;

import android.util.Log;

import androidx.annotation.NonNull;

import com.eggiverse.app.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChatService {
    private static final String TAG = "ChatService";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String apiKey;

    public interface ChatCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public ChatService() {
        this.apiKey = BuildConfig.OPENAI_API_KEY;

        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void sendMessage(String userMessage, ChatCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("max_tokens", 150);
            jsonBody.put("temperature", 0.8);

            JSONArray messages = new JSONArray();

            // 시스템 프롬프트: 알의 성격 설정
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content",
                    "당신은 귀여운 다마고치 알입니다. " +
                            "짧고 귀엽게 대답하며, 이모지를 적절히 사용합니다. " +
                            "사용자를 주인님이라고 부르고, 애교있게 행동합니다. " +
                            "답변은 1-2문장으로 짧게 해주세요.");
            messages.put(systemMessage);

            // 사용자 메시지
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.put(userMsg);

            jsonBody.put("messages", messages);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "API 호출 실패", e);
                    callback.onError("네트워크 오류가 발생했습니다: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful() || responseBody == null) {
                            Log.e(TAG, "API 오류: " + response);
                            callback.onError("서버 오류 (" + response.code() + ")");
                            return;
                        }

                        String responseBodyString = responseBody.string();
                        JSONObject jsonResponse = new JSONObject(responseBodyString);
                        JSONArray choices = jsonResponse.getJSONArray("choices");

                        if (choices.length() > 0) {
                            JSONObject firstChoice = choices.getJSONObject(0);
                            JSONObject message = firstChoice.getJSONObject("message");
                            String content = message.getString("content");
                            callback.onSuccess(content.trim());
                        } else {
                            callback.onError("응답을 받을 수 없습니다.");
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "JSON 파싱 오류", e);
                        callback.onError("응답 처리 중 오류가 발생했습니다.");
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "요청 생성 실패", e);
            callback.onError("요청 생성 중 오류가 발생했습니다.");
        }
    }
}

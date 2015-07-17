package com.ecolem_test.tp3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class MessageActivity extends AppActivity {
    private String user_token;

    private String gpTitle;
    private String gpInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        String user_email = preferences.getString("ACTIVE_USER", "");
        user_token = preferences.getString(user_email + "_token", "");

        gpTitle = preferences.getString("sGroupTitle", "");
        gpInfo = preferences.getString("sGroupInfo", "");

        messageList(gpInfo);

    }

    private void messageList(String group_id) {

        String url = "http://questioncode.fr:10007/api/groups/"+ group_id +"/messages";

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("TAG", response.toString());

                        try {

                            ArrayList<Message> messages = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject msgObj = (JSONObject) response.get(i);

                                String content = msgObj.getString("content");
                                String id = msgObj.getString("_id");
                                String date = msgObj.getString("creation_date");

                                messages.add(new Message(id, content, date));

                            }

                            Collections.reverse(messages);

                            setMsgListAdapter(messages);

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyLog.d("Error", "Error: " + error.getMessage());
                    }

                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + user_token);
                return headers;
            }
        };
        VolleyApplication.getInstance().getRequestQueue().add(jsObjRequest);
    }

    public void sendMessage(View view) {

        String url = "http://questioncode.fr:10007/api/messages";

        final String content = getEditTextValue(R.id.act_message_new);

        if (!content.isEmpty()) {

            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();

            String json = "{\"group\": \""+ gpInfo +"\", \"content\": [\""+ content +"\"]}";

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, json,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.hide();

                            Intent mainIntent = new Intent(getApplicationContext(), TheAppActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            finish();
                        }

                    },

                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.hide();

                            if (error instanceof NoConnectionError) {
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_connection, Toast.LENGTH_LONG);
                                toast.show();
                            }
                            else {

                                NetworkResponse response = error.networkResponse;

                                if (response != null && response.data != null) {

                                    try {
                                        String responseBody = new String(error.networkResponse.data);
                                        JSONObject jsonObject = new JSONObject(responseBody);
                                        String message = jsonObject.getString("message");
                                        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                                        toast.show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                        Toast toast = Toast.makeText(getApplicationContext(), R.string.error_oups, Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                                else {
                                    Toast toast = Toast.makeText(getApplicationContext(), R.string.error_oups, Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        }

                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + user_token);
                    return headers;
                }
            };
            VolleyApplication.getInstance().getRequestQueue().add(jsObjRequest);

        } else {
            Toast toast = Toast.makeText(this, R.string.empty_msg, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void setMsgListAdapter(ArrayList<Message> messages) {

        MessageListAdapter adapter = new MessageListAdapter(this, messages);

        ListView listView = (ListView) findViewById(R.id.messagesListView);
        listView.setAdapter(adapter);
    }
}

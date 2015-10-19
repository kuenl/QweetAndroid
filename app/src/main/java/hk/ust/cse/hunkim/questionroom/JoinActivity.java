package hk.ust.cse.hunkim.questionroom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;


/**
 * A login screen that offers login via email/password.
 */
public class JoinActivity extends AppCompatActivity {
    private static String TAG = "JoinActivity";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    @Bind(R.id.room_name)
    AutoCompleteTextView roomNameView;

    List<String> rooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_join);
        ButterKnife.bind(this);
        rooms = new ArrayList<>();
        new GetRoomListTask().execute();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.joinButton)
    void onClick() {
        attemptJoin();
    }

    @SuppressWarnings("unused")
    @OnEditorAction(R.id.room_name)
    boolean onEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            attemptJoin();
        }
        return true;
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptJoin() {
        // Reset errors.
        roomNameView.setError(null);

        // Store values at the time of the login attempt.
        String room_name = roomNameView.getText().toString();

        boolean cancel = true;

        // Check for a valid room name.
        if (room_name.isEmpty()) {
            roomNameView.setError(getString(R.string.error_field_required));
        } else if (!isRoomNameValid(room_name)) {
            roomNameView.setError(getString(R.string.error_invalid_room_name));
        } else if (room_name.length() > 20) {
            roomNameView.setError("Length of room name should be less than 20.");
        } else {
            cancel = false;
        }

        if (cancel) {
            roomNameView.setText("");
            roomNameView.requestFocus();
        } else {
            new CheckRoomExistTask().execute(room_name);
        }
    }

    private boolean isRoomNameValid(String room_name) {
        return room_name.matches("^[0-9a-zA-Z]+$");
    }

    private void updateSuggestionList(JSONArray jsonArray) {
        if (jsonArray != null) {
            rooms.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    rooms.add(jsonArray.getJSONObject(i).getString("roomName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, rooms);
            roomNameView.setAdapter(adapter);
        }
    }

    private class GetRoomListTask extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... params) {
            return getList();
        }

        private JSONArray getList() {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = Constant.API_URL + "room";
                Log.d(TAG, url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                return new JSONArray(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            updateSuggestionList(jsonArray);
        }
    }

    private class CheckRoomExistTask extends AsyncTask<String, Void, Boolean> {
        private String roomName;

        @Override
        protected Boolean doInBackground(String... params) {
            roomName = params[0];
            return checkExist(params[0]);
        }

        private Boolean checkExist(String roomName) {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = Constant.API_URL + "room/exist?id=" + roomName;
                Log.d(TAG, url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                return new JSONObject(response.body().string()).getBoolean("exist");
            } catch (IOException e) {
                e.printStackTrace();
                return checkExist(roomName);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean exist) {
            if (exist) {
                join(roomName);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                builder.setTitle("Create room");
                builder.setMessage("The room doesn't exist, do you want to create it?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        join(roomName);
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        }


    }

    private void join(String room_name) {
        Intent intent = new Intent(this, QuestionRoomActivity.class);
        intent.putExtra(Constant.KEY_ROOM_NAME, room_name);
        startActivity(intent);
    }
}


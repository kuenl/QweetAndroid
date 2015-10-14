package hk.ust.cse.hunkim.questionroom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class JoinActivity extends AppCompatActivity {
    private static String TAG = "JoinActivity";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private AutoCompleteTextView roomNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_join);

        // Set up the login form.
        roomNameView = (AutoCompleteTextView) findViewById(R.id.room_name);
        roomNameView.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    attemptJoin();
                }
                return true;
            }
        });

        ImageButton sendBtn = (ImageButton) findViewById(R.id.joinButton);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptJoin();
            }
        });

        new GetRoomListTask().execute();
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
        // http://stackoverflow.com/questions/8248277
        // Make sure alphanumeric characters
        return !room_name.matches("^.*[^a-zA-Z0-9 ].*$");
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
            if (jsonArray != null) {
                List<String> array = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        array.add(jsonArray.getJSONObject(i).getString("roomName"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(JoinActivity.this,
                        android.R.layout.simple_dropdown_item_1line, array);
                roomNameView.setAdapter(adapter);
            }
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


package hk.ust.cse.hunkim.questionroom;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import hk.ust.cse.hunkim.questionroom.datamodel.Room;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_DEFAULT;


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
    @Bind(R.id.joinRootView)
    CoordinatorLayout rootView;
    //@Bind(R.id.progressContainer)
    //LinearLayout progressContainer;
    @Bind(R.id.roomlistProgressbar)
    ProgressBar roomlistProgressbar;
    //@Bind(R.id.joinMainContainer)
    //LinearLayout joinMainContainer;
    @Bind(R.id.joinFieldContainer)
    LinearLayout joinFieldContainer;

    Map<String, String> roomTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_join);
        ButterKnife.bind(this);
        roomTable = null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestAutoCompleteRoomList();
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

    private void requestAutoCompleteRoomList() {
        roomlistProgressbar.setVisibility(View.VISIBLE);
        joinFieldContainer.setVisibility(View.GONE);
        JsonArrayRequest request = new JsonArrayRequest("https://qweet-api.herokuapp.com/room?populate=false",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        updateAutoCompleteRoomList(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(rootView, "Unable to retrieve room list.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestAutoCompleteRoomList();
                            }
                        })
                        .show();
                updateAutoCompleteRoomList(null);
            }
        });
        VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(request);
    }

    private void updateAutoCompleteRoomList(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.length() > 0) {
            List<String> roomNameList = new ArrayList<>();
            roomTable = new Hashtable<>();
            Set<String> roomNameSet = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    String id = jsonArray.getJSONObject(i).getString("id");
                    String name = jsonArray.getJSONObject(i).getString("name");
                    roomTable.put(name, id);
                    roomNameSet.add(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            roomNameList.addAll(roomNameSet);
            Collections.sort(roomNameList);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, roomNameList);
            roomNameView.setAdapter(adapter);
            roomlistProgressbar.setVisibility(View.GONE);
            joinFieldContainer.setVisibility(View.VISIBLE);
        } else {
            roomlistProgressbar.setVisibility(View.GONE);
            joinFieldContainer.setVisibility(View.GONE);
        }
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
        String roomName = roomNameView.getText().toString();

        boolean cancel = true;

        // Check for a valid room name.
        if (roomName.isEmpty()) {
            roomNameView.setError(getString(R.string.error_field_required));
        } else if (!isRoomNameValid(roomName)) {
            roomNameView.setError(getString(R.string.error_invalid_room_name));
        } else if (roomName.length() > 20) {
            roomNameView.setError("Length of room name should be less than 20.");
        } else {
            cancel = false;
        }

        if (cancel) {
            roomNameView.setText("");
            roomNameView.requestFocus();
        } else {
            if (roomTable != null && roomTable.containsKey(roomName)) {
                join(roomTable.get(roomName));
            } else {
                askRoomCreation(roomName);
            }
            //new CheckRoomExistTask().execute(room_name);
        }
    }

    private boolean isRoomNameValid(String room_name) {
        return room_name.matches("^[0-9a-zA-Z]+$");
    }

    private void askRoomCreation(final String roomName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
        builder.setTitle("Create room");
        builder.setMessage("The room doesn't exist, do you want to create it?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestCreateRoom(roomName);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void requestCreateRoom(String roomName) {
        Room room = new Room();
        room.setName(roomName);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(new Gson().toJson(room));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Method.POST, "https://qweet-api.herokuapp.com/room", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Room room = new GsonBuilder()
                        .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                        .create()
                        .fromJson(response.toString(), Room.class);
                final String id = room.getId();
                String adminKey = room.getAdminKey();
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                builder.setTitle("Room Created");
                builder.setMessage("The room has been created. " +
                        "\nPlease remember the private key for administration usage." +
                        "\n" + adminKey);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        join(id);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(request);
    }

    private void join(String roomId) {
        /*
        Intent intent = new Intent(this, QuestionRoomActivity.class);
        intent.putExtra(Constant.KEY_ROOM_ID, roomId);
        startActivity(intent);
        */

        Intent intent = new Intent();
        intent.setAction(ACTION_VIEW);
        intent.addCategory(CATEGORY_DEFAULT);
        intent.setData(Uri.parse("content://qweet.kuenl.com/room/" + roomId));
        startActivity(intent);
    }
}


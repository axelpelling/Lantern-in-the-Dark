package computing.mobile.lanterninthedark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class GameActivity extends Activity implements NetworkingEventHandler {

    private enum Status {TARGET, PLAYING, PLAYED, UNPLAYED, LOADING}
    private Status currentStatus;

    private NetworkingManager manager;

    private Phone phone;
    private GridSystem gridSystem;
    private LinkedHashMap<String, Phone> players;
    private ArrayList<String> playOrder;
    private String playerName;

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        changeStatus(Status.LOADING);

        Intent intent = getIntent();
        playerName = intent.getStringExtra("playerName");

        manager = new NetworkingManager(this, "Group5", playerName);

        manager.loadValueForKeyOfUser("gridSystem", "host");
        manager.loadValueForKeyOfUser("players", "host");
        manager.loadValueForKeyOfUser("playOrder", "host");

        manager.monitorKeyOfUser("gridSystem", "host");

        tv = (TextView) findViewById(R.id.tv);
        tv.setText(playerName);
    }

    @Override
    public void savedValueForKeyOfUser(JSONObject json, String key, String user) {

    }

    @Override
    public void loadedValueForKeyOfUser(JSONObject json, String key, String user) {
        Gson gson = new Gson();

        try {
            if(key.equals("gridSystem") && user.equals("host") && json.get("code").equals("1")){
                String gridSystemString = (String) json.get("value");
                gridSystem = gson.fromJson(gridSystemString, GridSystem.class);
            }
            else if(key.equals("players") && user.equals("host") && json.get("code").equals("1")){
                String playersString = (String) json.get("value");
                players = gson.fromJson(playersString, LinkedHashMap.class);
            }
            else if(key.equals("playOrder") && user.equals("host") && json.get("code").equals("1")){
                String playersString = (String) json.get("value");
                playOrder = gson.fromJson(playersString, ArrayList.class);
                if(playOrder.get(0).equals(playerName)){
                    changeStatus(Status.PLAYING);
                }
                else if(playOrder.get(1).equals(playerName)){
                    changeStatus(Status.TARGET);
                }
                else{
                    changeStatus(Status.UNPLAYED);
                }
            }

        } catch (JSONException e) {
            Log.e(NetworkingManager.TAG_ERROR, e.getMessage());
        }
    }

    @Override
    public void deletedKeyOfUser(JSONObject json, String key, String user) {

    }

    @Override
    public void monitoringKeyOfUser(JSONObject json, String key, String user) {

    }

    @Override
    public void ignoringKeyOfUser(JSONObject json, String key, String user) {

    }

    @Override
    public void valueChangedForKeyOfUser(JSONObject json, String key, String user) {

    }

    @Override
    public void lockedKeyofUser(JSONObject json, String key, String user) {

    }

    @Override
    public void unlockedKeyOfUser(JSONObject json, String key, String user) {

    }

    public void changeStatus(Status status){
        currentStatus = status;
        switch (currentStatus){
            case LOADING:

                break;
            case PLAYING:

                break;
            case TARGET:

                break;
            case PLAYED:

                break;
            case UNPLAYED:

                break;
        }
    }
}

package computing.mobile.lanterninthedark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

        setStatus(Status.LOADING);

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
        if(key.equals("target") && user.equals("host")){
            manager.unlockKeyOfUser("target", "host");
        }
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
                    setStatus(Status.PLAYING);
                    for(String playername : players.keySet()){
                        Log.d("crash", playername);
                    }
                    phone = players.get(playerName);
                    phone.setPosition(1, 1);
                    gridSystem.addPhone(phone);
                }
                else if(playOrder.get(1).equals(playerName)){
                    setStatus(Status.TARGET);
                }
                else{
                    setStatus(Status.UNPLAYED);
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
        Gson gson = new Gson();
        Log.d(NetworkingManager.TAG_EVENT_COMPLETE, "JSONOBject retreived in method valueChanged + " +
                "forKeyOfUser: " +  json.toString());
        try {
            if(key.equals("gridSystem") && user.equals("host")){
                String gridSystemString = (String) json.get("value");
                gridSystem = gson.fromJson(gridSystemString, GridSystem.class);
                gridSystem.printGrid();

            }

        } catch (JSONException e) {
            Log.e(NetworkingManager.TAG_ERROR, e.getMessage());
        }
    }

    @Override
    public void lockedKeyofUser(JSONObject json, String key, String user) {
        try {
            if(key.equals("gridSystem") && user.equals("host") && json.get("code").equals("1")){
                Gson gson = new Gson();
                String gridSystemString = gson.toJson(gridSystem);
                manager.saveValueForKeyOfUser("gridSystem", "host", gridSystemString);
            }

        } catch (JSONException e) {
            Log.e(NetworkingManager.TAG_ERROR, e.getMessage());
        }
    }

    @Override
    public void unlockedKeyOfUser(JSONObject json, String key, String user) {

    }

    public void setStatus(Status status){
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


    public void downButton(View view) {
        if (currentStatus.equals(Status.PLAYING)){
            Phone targetPhone = players.get(playOrder.get(1));
            targetPhone.setPosition(phone.getX(), phone.getY() + 1);
            gridSystem.addPhone(targetPhone);
            manager.lockKeyOfUser("gridSystem", "host");
        }

    }

    public void rightButton(View view) {

    }

    public void leftButton(View view) {

    }

    public void upButton(View view) {

    }

    @Override
    protected void onPause(){
        super.onPause();

        manager.ignoreKeyOfUser("gridSystem", "host");
    }

    @Override
    protected void onResume(){
        super.onResume();

        manager.monitorKeyOfUser("gridSystem", "host");
    }
}

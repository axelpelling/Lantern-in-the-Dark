package computing.mobile.lanterninthedark;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedHashMap;


public class ClientActivity extends Activity implements NetworkingEventHandler {

    private NetworkingManager manager;
    private GridSystem gridSystem;
    private LinkedHashMap<String, Phone> players;
    private Phone clientPhone;
    private String clientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);


        Intent intent = getIntent();
        clientName = intent.getStringExtra("clientName");

        manager = new NetworkingManager(this, "Group5", clientName);

        manager.loadValueForKeyOfUser("players", "host");
        //manager.lockKeyOfUser("players", "host");

        manager.monitorKeyOfUser("startGame", "host");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void savedValueForKeyOfUser(JSONObject json, String key, String user) {

    }

    @Override
    public void loadedValueForKeyOfUser(JSONObject json, String key, String user) {
        Gson gson = new Gson();

        try {
            if(key.equals("players") && user.equals("host") && json.get("code").equals("1")){
                String playersString = (String) json.get("value");
                players = gson.fromJson(playersString, LinkedHashMap.class);
                int id = players.size() + 1;
                clientPhone = new Phone(id);
                players.put(clientName, clientPhone);

                String playerHashMapString = gson.toJson(players);
                manager.saveValueForKeyOfUser("players", "host", playerHashMapString);
                Log.d("players", players.toString());
                manager.unlockKeyOfUser("players", "host");
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
        Log.d(NetworkingManager.TAG_EVENT_COMPLETE, "JSONOBject retreived in method valueChanged + " +
                "forKeyOfUser: " +  json.toString());
        try {
            if(key.equals("startGame") && user.equals("host")){
                if(json.getJSONArray("records").getJSONObject(0).getString("value").equals("true")){
                    Log.d("test1", "starting game, client");

                    Intent intent = new Intent(this, GameActivity.class);
                    intent.putExtra("playerName", clientName);
                    startActivity(intent);
                }
            }

        } catch (JSONException e) {
            Log.e(NetworkingManager.TAG_ERROR, e.getMessage());
        }
    }

    @Override
    public void lockedKeyofUser(JSONObject json, String key, String user) {

        try {
            if(key.equals("players") && user.equals("host") && json.get("code").equals("1")){
                manager.loadValueForKeyOfUser("players", "host");
            }
            else if(key.equals("players") && user.equals("host") && json.get("code").equals("2")){
                manager.lockKeyOfUser("players", "host");
            }

        } catch (JSONException e) {
            Log.e(NetworkingManager.TAG_ERROR, e.getMessage());
        }
    }

    @Override
    public void unlockedKeyOfUser(JSONObject json, String key, String user) {

    }

    /*@Override
    protected void onPause(){
        super.onPause();

        manager.ignoreKeyOfUser("startGame", "host");
    }

    @Override
    protected void onResume(){
        super.onResume();

        manager.monitorKeyOfUser("startGame", "host");
    }*/
}

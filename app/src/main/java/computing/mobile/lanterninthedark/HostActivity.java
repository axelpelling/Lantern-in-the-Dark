package computing.mobile.lanterninthedark;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;


public class HostActivity extends Activity implements NetworkingEventHandler{

    private NetworkingManager manager;
    private GridSystem gridSystem;
    private Phone hostPhone;
    private LinkedHashMap<String, Phone> players;
    private ArrayList<String> playerNames;
    private ArrayAdapter<String> adapter;
    private String hostName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        Intent intent = getIntent();
        hostName = intent.getStringExtra("hostName");

        manager = new NetworkingManager(this, "Group5", hostName);
        hostPhone = new Phone(1);
        gridSystem = GridSystem.getInstance();

        //Save gridSystem to server
        Gson gson = new Gson();
        String gridSystemJson = gson.toJson(gridSystem);
        manager.saveValueForKeyOfUser("gridSystem", "host", gridSystemJson);

        //Save host player name to players list
        players = new LinkedHashMap<String, Phone>();
        players.put(hostName, hostPhone);
        String playerHashMapString = gson.toJson(players);
        manager.saveValueForKeyOfUser("players", "host", playerHashMapString);

        //Set adapter to view users
        playerNames = new ArrayList<String>();
        String[] playerNamesTemp =  Arrays.asList(players.keySet().toArray()).toArray(new String[players.keySet().toArray().length]);
        for(String player : playerNamesTemp){
            playerNames.add(player);
        }
        ListView listView = (ListView) findViewById(android.R.id.list);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                playerNames);
        listView.setAdapter(adapter);

        //Key for the clients to monitor for when to start the game.
        manager.saveValueForKeyOfUser("startGame", "host", "false");

        //Monitor players key to check for clients
        manager.monitorKeyOfUser("players", "host");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host, menu);
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
        if(key.equals("playOrder") && user.equals("host")){
            manager.saveValueForKeyOfUser("startGame", "host", "true");
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("playerName", hostName);
            startActivity(intent);
        }
    }

    @Override
    public void loadedValueForKeyOfUser(JSONObject json, String key, String user) {

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
            if(key.equals("players") && user.equals("host")){
                Log.d("Records: ", "test1" + json.getJSONArray("records").toString());

                JSONArray records = json.getJSONArray("records");
                for (int i=0; i <= records.length(); i++){
                    if(records.getJSONObject(i).get("key").equals("players")){
                        String playersString = (String) json.getJSONArray("records").getJSONObject(i).get("value");
                        players = gson.fromJson(playersString, LinkedHashMap.class);

                        playerNames.clear();
                        String[] playerNamesTemp = Arrays.asList(players.keySet().toArray()).
                                            toArray(new String[players.keySet().toArray().length]);
                        for(String player : playerNamesTemp){
                            playerNames.add(player);
                        }

                        adapter.notifyDataSetChanged();
                        break;
                    }
                }

            }

        } catch (JSONException e) {
            Log.e(NetworkingManager.TAG_ERROR, e.getMessage());
        }
    }

    @Override
    public void lockedKeyofUser(JSONObject json, String key, String user) {

    }

    @Override
    public void unlockedKeyOfUser(JSONObject json, String key, String user) {

    }

    public void startGame(View view) {
        if(playerNames.size() >= 2){
            Log.d("test1", "starting game, host");
            manager.ignoreKeyOfUser("players", "host");

            ArrayList<String> playOrder = playerNames;
            //Don't randomize order for now
            //Collections.shuffle(playOrder);
            Gson gson = new Gson();
            String playOrderString = gson.toJson(playOrder);
            manager.saveValueForKeyOfUser("playOrder", "host", playOrderString);

            //Linked tree map error thing again
            players.get(playOrder.get(0)).setPosition(1, 1);
            players.get(playOrder.get(0)).setPlayed(true);

            String playerHashMapString = gson.toJson(players);
            manager.saveValueForKeyOfUser("players", "host", playerHashMapString);


        }
        else{
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this.getApplicationContext(), "More than 1 player required to start.", duration);
            toast.show();
        }
    }

    /*@Override
    protected void onPause(){
        super.onPause();

        manager.ignoreKeyOfUser("players", "host");
    }

    @Override
    protected void onResume(){
        super.onResume();

        manager.monitorKeyOfUser("players", "host");
    }*/
}

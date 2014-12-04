package computing.mobile.lanterninthedark;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;


public class ClientActivity extends ActionBarActivity implements NetworkingEventHandler {

    NetworkingManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        manager = new NetworkingManager(this, "Group5", "Client");

        manager.loadValueForKeyOfUser("gridSystem", "Host");

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

        if(key.equals("gridSystem") && user.equals("Host")){
            Log.d("test1", "success");
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
}

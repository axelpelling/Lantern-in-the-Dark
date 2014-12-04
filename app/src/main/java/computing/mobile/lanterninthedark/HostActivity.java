package computing.mobile.lanterninthedark;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import org.json.JSONObject;


public class HostActivity extends ActionBarActivity implements NetworkingEventHandler{

    NetworkingManager manager;
    GridSystem gridSystem;
    Phone hostPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);



        manager = new NetworkingManager(this, "Group5", "Host");
        hostPhone = new Phone(1, 0, 0);
        gridSystem = GridSystem.getInstance();
        gridSystem.addPhone(hostPhone);

        Gson gson = new Gson();
        String gridSystemJson = gson.toJson(gridSystem);
        manager.saveValueForKeyOfUser("gridSystem", "Host", gridSystemJson);


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

    }

    @Override
    public void lockedKeyofUser(JSONObject json, String key, String user) {

    }

    @Override
    public void unlockedKeyOfUser(JSONObject json, String key, String user) {

    }
}

package computing.mobile.lanterninthedark;

import android.os.NetworkOnMainThreadException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity implements NetworkingEventHandler {

    private GridSystem gridSystem;
    private NetworkingManager userManager;
    private NetworkingManager gridManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gridSystem = GridSystem.getInstance();
        userManager = new NetworkingManager(this, "Group5", "me");
        gridManager = new NetworkingManager(this, "Group5", "gridSystem");


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

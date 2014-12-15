package computing.mobile.lanterninthedark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;


public class LoginActivity extends Activity {

    private EditText userLoginEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        userLoginEditText = (EditText) findViewById(R.id.userLoginEditText);
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

    public void userHostLogin(View view) {
        Intent intent = new Intent(this, HostActivity.class);
        intent.putExtra("hostName", userLoginEditText.getText().toString());
        startActivity(intent);
    }

    public void userClientLogin(View view) {
        Intent intent = new Intent(this, ClientActivity.class);
        intent.putExtra("clientName", userLoginEditText.getText().toString());
        startActivity(intent);
    }
}

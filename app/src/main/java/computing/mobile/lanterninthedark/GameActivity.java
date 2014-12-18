package computing.mobile.lanterninthedark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.graphics.drawable.AnimationDrawable;

import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;


public class GameActivity extends Activity implements NetworkingEventHandler{

    //Statuses of phones
    private enum Status {TARGET, PLAYING, PLAYED, UNPLAYED, LOADING, FINISHED}
    private Status currentStatus;

    //Network manager
    private NetworkingManager manager;

    //Game elements and stuff
    private Phone phone;
    private GridSystem gridSystem;
    private LinkedHashMap<String, Phone> players;
    private ArrayList<String> playOrder;
    private String playerName;

    //Android Views and stuff
    private TextView tv;
    private ImageButton upButton;
    private ImageButton downButton;
    private ImageButton leftButton;
    private ImageButton rightButton;
    private ImageView characterImageView;
    private AnimationDrawable walkingCharacter;
    private ImageView gradientImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tv = (TextView) findViewById(R.id.tv);
        upButton = (ImageButton) findViewById(R.id.upButton);
        downButton = (ImageButton) findViewById(R.id.downButton);
        leftButton = (ImageButton) findViewById(R.id.leftButton);
        rightButton = (ImageButton) findViewById(R.id.rightButton);

        characterImageView = (ImageView) findViewById(R.id.characterImageView);
        gradientImageView = (ImageView) findViewById(R.id.gradientImageView);

        characterImageView.setBackgroundResource(R.drawable.animation_character);
        walkingCharacter = (AnimationDrawable) characterImageView.getBackground();

        setStatus(Status.LOADING);

        Intent intent = getIntent();
        playerName = intent.getStringExtra("playerName");

        manager = new NetworkingManager(this, "Group5", playerName);

        manager.loadValueForKeyOfUser("gridSystem", "host");
        manager.loadValueForKeyOfUser("players", "host");
        manager.loadValueForKeyOfUser("playOrder", "host");

        manager.monitorKeyOfUser("gridSystem", "host");

    }

    @Override
    public void savedValueForKeyOfUser(JSONObject json, String key, String user) {
        if(key.equals("gridSystem") && user.equals("host")) {
            manager.unlockKeyOfUser("gridSystem", "host");
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
                Type linkedHashMapType = new TypeToken<LinkedHashMap<String, Phone>>() {}.getType();
                String playersString = (String) json.get("value");
                players = gson.fromJson(playersString, linkedHashMapType);
                phone = players.get(playerName);
            }
            else if(key.equals("playOrder") && user.equals("host") && json.get("code").equals("1")){
                String playersString = (String) json.get("value");
                playOrder = gson.fromJson(playersString, ArrayList.class);

                //Initial check of play order
                if(playOrder.get(0).equals(playerName)){
                    setStatus(Status.PLAYING);
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

                Log.d("Records: ", json.getJSONArray("records").toString());

                JSONArray records = json.getJSONArray("records");
                for (int i=0; i <= records.length(); i++){
                    if(records.getJSONObject(i).get("key").equals("gridSystem")){
                        String gridSystemString = (String) records.getJSONObject(i).get("value");
                        gridSystem = gson.fromJson(gridSystemString, GridSystem.class);
                        gridSystem.printGrid();

                        break;
                    }
                }


                //Update phone position
                int[] phonePosition = gridSystem.getPhonePosition(phone);
                if(phonePosition[0] != -1 && phonePosition[1] != -1){
                    phone.setPosition(phonePosition[0], phonePosition[1]);
                }

                //Update play order
                String justPlayed = playOrder.get(0);
                playOrder.remove(0);
                playOrder.add(justPlayed);

                //Check if game is finished and update status
                //Otherwise check if your phone is next in playOrder and change status accordingly
                if(gridSystem.isGameFinished()){
                    setStatus(Status.FINISHED);
                }
                else if(playOrder.get(0).equals(playerName)){
                    setStatus(Status.PLAYING);
                }
                else if(playOrder.get(1).equals(playerName)){
                    setStatus(Status.TARGET);
                }
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
                Log.d("lockedGridSystem", "Locked grid system");
            }
            else if (key.equals("gridSystem") && user.equals("host") && json.get("code").equals("2")) {
                manager.lockKeyOfUser("gridSystem", "host");
                Log.d("lockedGridSystem", "Grid system was already in use");
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

                hideArrows();
                tv.setText("LOADING");
                break;
            case PLAYING:

                //Checks if you are at the edge of the grid or if there are other phones near you
                //and sets arrow visibilities
                setArrowVisibilities();

                characterImageView.setRotation(0);
                characterImageView.setRotation(gridSystem.getRotation());
                tv.setText("PLAYING");
                break;
            case TARGET:

                hideArrows();
                tv.setText("TARGET");
                break;
            case PLAYED:

                hideArrows();
                tv.setText("PLAYED");
                break;
            case UNPLAYED:

                hideArrows();
                tv.setText("UNPLAYED");
                break;
            case FINISHED:

                hideArrows();
                tv.setText("GAME FINISHED");
                Log.d("finished", "game finished");
                manager.ignoreKeyOfUser("gridSystem", "host");
                break;
        }
    }


    public void goUpButton(View view) {
        if (currentStatus.equals(Status.PLAYING)){
            //Get targetPhone and set its position to the clicked direction
            Phone targetPhone = players.get(playOrder.get(1));
            targetPhone.setPosition(phone.getX(), phone.getY() - 1);
            //Set the phone's old position to 0 if it has been played before
            if(targetPhone.getPlayed()){
                gridSystem.resetPhonePosition(targetPhone);
            }
            else {
                targetPhone.setPlayed(true);
            }


            //Animate the character to turn
            float rotation = 0f;
            if(gridSystem.getRotation() == 90f){
                rotation = -90f;
            }
            else if(gridSystem.getRotation() == 180){
                rotation = 180f;
            }
            else if(gridSystem.getRotation() == -90){
                rotation = 90f;
            }
            Animation upAnimation = new RotateAnimation(gridSystem.getRotation(), rotation, characterImageView.getWidth()/2, characterImageView.getHeight()/2);

            // Set the animation's parameters
            upAnimation.setDuration(1000);
            upAnimation.setRepeatCount(0);
            upAnimation.setFillAfter(true);

            characterImageView.setAnimation(upAnimation);
            gridSystem.setRotation(characterImageView.getRotation());
            Log.d("rotation", "rotation: " + rotation);
            Log.d("rotation", "imageView rotation: " + characterImageView.getRotation());
            Log.d("rotation", "gridSystem rotation: " + gridSystem.getRotation());

            //translation
            translationAnimation(characterImageView.getX(), characterImageView.getY(),characterImageView.getX(), 0);


            //Check if the targetPhone has reached the Sven's home then add the phone to the grid
            gridSystem.checkGameFinished(targetPhone.getX(), targetPhone.getY());
            gridSystem.addPhone(targetPhone);

            //Lastly, update the server's gridSystem
            manager.lockKeyOfUser("gridSystem", "host");
            setStatus(Status.PLAYED);
        }
    }


    public void goDownButton(View view) {
        if (currentStatus.equals(Status.PLAYING)){
            Phone targetPhone = players.get(playOrder.get(1));
            targetPhone.setPosition(phone.getX(), phone.getY() + 1);
            if(targetPhone.getPlayed()){
                gridSystem.resetPhonePosition(targetPhone);
            }
            else {
                targetPhone.setPlayed(true);
            }

            //Animate the character to turn
            float rotation = 180f;
            if(gridSystem.getRotation() == 90f){
                rotation = 90f;
            }
            else if(gridSystem.getRotation() == 180){
                rotation = 0f;
            }
            else if(gridSystem.getRotation() == -90){
                rotation = -90f;
            }
            Animation downAnimation = new RotateAnimation(gridSystem.getRotation(), rotation, characterImageView.getWidth()/2, characterImageView.getHeight()/2);

            // Set the animation's parameters
            downAnimation.setDuration(1000);
            downAnimation.setRepeatCount(0);
            downAnimation.setFillAfter(true);

            characterImageView.setAnimation(downAnimation);
            gridSystem.setRotation(characterImageView.getRotation());
            Log.d("rotation", "rotation: " + rotation);
            Log.d("rotation", "imageView rotation: " + characterImageView.getRotation());
            Log.d("rotation", "gridSystem rotation: " + gridSystem.getRotation());

           translationAnimation(characterImageView.getX(), characterImageView.getY(),characterImageView.getX(), characterImageView.getY()*2);


            gridSystem.checkGameFinished(targetPhone.getX(), targetPhone.getY());
            gridSystem.addPhone(targetPhone);

            manager.lockKeyOfUser("gridSystem", "host");
            setStatus(Status.PLAYED);
        }
    }


    public void goRightButton(View view) {
        if (currentStatus.equals(Status.PLAYING)){
            Phone targetPhone = players.get(playOrder.get(1));
            targetPhone.setPosition(phone.getX() + 1, phone.getY());
            if(targetPhone.getPlayed()){
                gridSystem.resetPhonePosition(targetPhone);
            }
            else {
                targetPhone.setPlayed(true);
            }


            //Animate the character to turn
            float rotation = 90f;
            if(gridSystem.getRotation() == 90f){
                rotation = 0f;
            }
            else if(gridSystem.getRotation() == 180){
                rotation = -90f;
            }
            else if(gridSystem.getRotation() == -90){
                rotation = 180f;
            }
            Animation rightAnimation = new RotateAnimation(gridSystem.getRotation(), rotation, characterImageView.getWidth()/2, characterImageView.getHeight()/2);

            // Set the animation's parameters
            rightAnimation.setDuration(1000);
            rightAnimation.setRepeatCount(0);
            rightAnimation.setFillAfter(true);

            characterImageView.setAnimation(rightAnimation);
            gridSystem.setRotation(characterImageView.getRotation());
            Log.d("rotation", "rotation: " + rotation);
            Log.d("rotation", "imageView rotation: " + characterImageView.getRotation());
            Log.d("rotation", "gridSystem rotation: " + gridSystem.getRotation());

            translationAnimation(characterImageView.getX(), characterImageView.getY(),characterImageView.getX()*2, characterImageView.getY());


            gridSystem.checkGameFinished(targetPhone.getX(), targetPhone.getY());
            gridSystem.addPhone(targetPhone);

            manager.lockKeyOfUser("gridSystem", "host");
            setStatus(Status.PLAYED);
        }
    }

    public void goLeftButton(View view) {
        if (currentStatus.equals(Status.PLAYING)){
            Phone targetPhone = players.get(playOrder.get(1));
            targetPhone.setPosition(phone.getX() - 1, phone.getY());
            if(targetPhone.getPlayed()){
                gridSystem.resetPhonePosition(targetPhone);
            }
            else {
                targetPhone.setPlayed(true);
            }

            //Animate the character to turn
            float rotation = -90f;
            if(gridSystem.getRotation() == 90f){
                rotation = 180f;
            }
            else if(gridSystem.getRotation() == 180){
                rotation = 90f;
            }
            else if(gridSystem.getRotation() == -90){
                rotation = 0f;
            }
            Animation leftAnimation = new RotateAnimation(gridSystem.getRotation(), rotation, characterImageView.getWidth()/2, characterImageView.getHeight()/2);

            // Set the animation's parameters
            leftAnimation.setDuration(1000);
            leftAnimation.setRepeatCount(0);
            leftAnimation.setFillAfter(true);

            characterImageView.setAnimation(leftAnimation);
            gridSystem.setRotation(characterImageView.getRotation());
            Log.d("rotation", "rotation: " + rotation);
            Log.d("rotation", "imageView rotation: " + characterImageView.getRotation());
            Log.d("rotation", "gridSystem rotation: " + gridSystem.getRotation());



            translationAnimation(characterImageView.getX(), characterImageView.getY(),0, characterImageView.getY());



            gridSystem.checkGameFinished(targetPhone.getX(), targetPhone.getY());
            gridSystem.addPhone(targetPhone);

            manager.lockKeyOfUser("gridSystem", "host");
            setStatus(Status.PLAYED);
        }
    }

    public void translationAnimation (float startX, float startY, float endX, float endY){

        walkingCharacter.start();
        Animation translateAnimation = new TranslateAnimation(startX,startY, endX, endY);
        translateAnimation.setDuration(2000);
        translateAnimation.setFillEnabled(true);
        translateAnimation.setFillAfter(true);
        characterImageView.startAnimation(translateAnimation);

    }




    public void setArrowVisibilities(){
        int[][] grid = gridSystem.getGrid();
        int gridLength = grid.length;
        int x = phone.getX();
        int y = phone.getY();

        //Check up button
        if( y - 1 < 0 || (grid[x][y-1] != 0 && grid[x][y-1] != 9001)){
            upButton.setVisibility(View.INVISIBLE);
        }
        else {
            upButton.setVisibility(View.VISIBLE);
        }

        //Check downButton
        if( y + 1 >= gridLength || (grid[x][y+1] != 0 && grid[x][y+1] != 9001)){
            downButton.setVisibility(View.INVISIBLE);
        }
        else {
            downButton.setVisibility(View.VISIBLE);
        }

        //Check right button
        if(x + 1 >= gridLength || (grid[x+1][y] != 0 && grid[x+1][y] != 9001)){
            rightButton.setVisibility(View.INVISIBLE);
        }
        else {
            rightButton.setVisibility(View.VISIBLE);
        }

        //Check left button
        if(x - 1 < 0 || (grid[x-1][y] != 0 && grid[x-1][y] != 9001)){
            leftButton.setVisibility(View.INVISIBLE);
        }
        else {
            leftButton.setVisibility(View.VISIBLE);
        }
    }

    public void hideArrows(){
        upButton.setVisibility(View.INVISIBLE);
        downButton.setVisibility(View.INVISIBLE);
        rightButton.setVisibility(View.INVISIBLE);
        leftButton.setVisibility(View.INVISIBLE);
    }

   /* @Override
    protected void onPause(){
        super.onPause();

        manager.ignoreKeyOfUser("gridSystem", "host");
    }

    @Override
    protected void onResume(){
        super.onResume();

        manager.monitorKeyOfUser("gridSystem", "host");
    }*/
}

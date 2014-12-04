package computing.mobile.lanterninthedark;

import android.util.Log;

/**
 * Created by Axel on 2014-12-03.
 */
public class GridSystem {

    private int[][] grid;

    private static GridSystem ourInstance = new GridSystem();

    public static GridSystem getInstance() {
        return ourInstance;
    }

    private GridSystem() {
        grid = new int[5][5];
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[i].length; j++){
                grid[i][j] = 0;
            }
        }
    }

    public void addPhone(Phone phone){
        grid[phone.getX()][phone.getY()] = phone.getId();
    }

    public int[][] getGrid(){
        return grid;
    }

    public void printGrid(){
        int i = 0;
        for (int[] array : grid){
            Log.d("arrayLog", "Array " + i + ":");
            i++;
            for (int j : array){

                //plant = Arrays.toString(plants[i]);
                //color = Arrays.deepToString(plants[j]);
                //System.out.println(plant + " " + color);
                Log.d("arrayLog", "" + j);

            }
        }
    }
}

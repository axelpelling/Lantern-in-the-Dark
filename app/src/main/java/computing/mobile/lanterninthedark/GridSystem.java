package computing.mobile.lanterninthedark;

import android.util.Log;

import java.util.Arrays;

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

    public int[] getPhonePosition(Phone phone) {
        int phoneId = phone.getId();
        int[] phonePosition = new int[] {-1,-1};
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid.length; x++) {
                if (phoneId == grid[x][y]) {
                    phonePosition[0] = x;
                    phonePosition[1] = y;
                }
            }
        }
        return phonePosition;
    }

    public void resetPhonePosition(Phone phone){
        int phoneId = phone.getId();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid.length; x++) {
                if (phoneId == grid[x][y]) {
                    grid[x][y] = 0;
                }
            }
        }
    }

    public int[][] getGrid(){
        return grid;
    }

    public void printGrid(){
        int[] temp = new int[grid.length];
        Log.d("printGrid", "Grid:");
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid.length; x++) {
                temp[x] = grid[x][y];
            }
            Log.d("printGrid", "" + Arrays.toString(temp));

        }
    }
}

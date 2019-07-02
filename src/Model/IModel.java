package Model;

import algorithms.mazeGenerators.Maze;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public interface IModel {
    void generateMaze(int width, int height);
    void moveCharacter(KeyCode movement);
    Maze getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    //void SaveMaze();
    void openFile();
    void solve();
    void stop();
}

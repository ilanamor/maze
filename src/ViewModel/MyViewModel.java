package ViewModel;

import Model.IModel;
import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

import static View.View.sendToShow;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public class MyViewModel extends Observable implements Observer {

    public IModel getModel() {
        return model;
    }

    private IModel model;

    private int characterPositionRow; //For Binding
    private int characterPositionColumn; //For Binding

    public MyViewModel(IModel model){
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            characterPositionRow = model.getCharacterPositionRow();
            characterPositionColumn = model.getCharacterPositionColumn();
            setChanged();
            notifyObservers();
        }
    }

    public void save (File filename) throws IOException{
        FileOutputStream out = new FileOutputStream(filename);
        ObjectOutputStream OS = new ObjectOutputStream(out);
        OS.writeObject(getMaze());
        int[] location = {this.getCharacterPositionRow(), this.getCharacterPositionColumn()};
        OS.writeObject(location);
        sendToShow("successfully save!");
    }


    public void open (){ model.openFile();}
    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return model.getCharacterPositionRow();
    }

    public int getCharacterPositionColumn() { return model.getCharacterPositionColumn(); }

    public void solve() {model.solve();}

    public void stop(){model.stop();}
}

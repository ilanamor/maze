package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import View.View;
import ViewModel.MyViewModel;
import Client.*;
import Server.*;
import algorithms.mazeGenerators.AMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public class MyModel extends Observable implements IModel {
    private Server mazeGeneratingServer;

    public Server getMazeGeneratingServer() {
        return mazeGeneratingServer;
    }

    public Server getSolveSearchProblemServer() {
        return solveSearchProblemServer;
    }

    private Server solveSearchProblemServer;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Maze maze;
    private int characterPositionRow;
    private int characterPositionColumn;

    public Solution getS() {
        return s;
    }

    private Solution s;

    public MyModel() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        //Raise the servers
    }

    private void CommunicateWithServer_MazeGenerating(int row, int col) {
        try {
            Client e = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream e = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        e.flush();
                        int[] mazeDimensions = new int[]{row, col};
                        e.writeObject(mazeDimensions);
                        e.flush();
                        byte[] compressedMaze = (byte[])((byte[])fromServer.readObject());
                        MyDecompressorInputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[100000];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                        characterPositionRow=maze.getStartPosition().getRowIndex();
                        characterPositionColumn=maze.getStartPosition().getColumnIndex();
                        //maze.print();
                        s=new Solution();
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            e.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }

    }

    private void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client e = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream e = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        e.flush();
                        //MyMazeGenerator mg = new MyMazeGenerator();
                        //Maze maze = mg.generate(row, col);
                        //maze.print();
                        maze.getStartPosition().setRowIndex(characterPositionRow);
                        maze.getStartPosition().setColumnIndex(characterPositionColumn);
                        e.writeObject(maze);
                        e.flush();
                        s = (Solution)fromServer.readObject();
                        System.out.println(String.format("Solution steps: %s", new Object[]{s}));
                        ArrayList mazeSolutionSteps = s.getSolutionPath();

                        /*for(int i = 0; i < mazeSolutionSteps.size(); ++i) {
                            System.out.println(String.format("%s. %s", new Object[]{Integer.valueOf(i), ((AState)mazeSolutionSteps.get(i)).toString()}));
                        }*/
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            e.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }

    }

    public void startServers() {
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();

    }

    public void stopServers() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
        //Platform.exit();
        System.exit(0);
    }

@Override
public  void openFile() {
    //this.s= new Solution();
    FileChooser choose = new FileChooser();
    choose.setTitle("open file");
    choose.setInitialDirectory(new File("./AllMaze"));
    File f = choose.showOpenDialog(null);
    if (f != null) {
        String s = f.getName();
        try {
            InputStream in = new FileInputStream("AllMaze/" + s);
            ObjectInputStream OS = new ObjectInputStream(in);
            Maze m = ((Maze) (OS.readObject()));
            int[] location = ((int[]) (OS.readObject()));
            this.maze = m;
            characterPositionRow = location[0];
            characterPositionColumn = location[1];
            setChanged();
            notifyObservers();
            View.sendToShow("successfully load!");

        } catch (IOException e) {
            View.sendToShow("Error while load!");
        } catch (ClassNotFoundException e1) {
            View.sendToShow("Error while load!");
        }
    }
}

    public void solve() {
        threadPool.execute(() -> {
            CommunicateWithServer_SolveSearchProblem();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers();
        });
    }

    @Override
    public void generateMaze(int width, int height) {
        //Generate maze
        threadPool.execute(() -> {
            //generateRandomMaze(width,height);
            CommunicateWithServer_MazeGenerating(width, height);

            //characterPositionRow=maze.getStartPosition().getRowIndex();
            //characterPositionColumn=maze.getStartPosition().getColumnIndex();
            maze.print();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }

           setChanged();
            notifyObservers();

       });
    }

    /*private Maze generateRandomMaze(int width, int height) {
        AMazeGenerator mazeGenerator = new MyMazeGenerator();
       maze = mazeGenerator.generate(width, height);
       characterPositionRow=maze.getStartPosition().getRowIndex();
       characterPositionColumn=maze.getStartPosition().getColumnIndex();
       maze.print();
       return maze;

    }*/
    public void stop(){
        stopServers();
    }
    @Override
    public Maze getMaze() {
        return maze;
    }

    public void setS(Solution s) {
        this.s = s;
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        int r = characterPositionRow;
        int c = characterPositionColumn;
        try {
            if ((movement == KeyCode.UP) && (maze.getMaze()[r - 1][c] != 1) && (r - 1 >= 0)) {
                characterPositionRow--;
            }
            if (movement == KeyCode.DOWN && (maze.getMaze()[r + 1][c] != 1) && (r + 1 < maze.getMaze().length)) {
                characterPositionRow++;

            }
            if (movement == KeyCode.RIGHT && (maze.getMaze()[r][c + 1] != 1) && (c + 1 < maze.getMaze()[0].length)) {
                characterPositionColumn++;
            }
            if (movement == KeyCode.LEFT && (maze.getMaze()[r][c - 1] != 1) && (c - 1 >= 0)) {
                characterPositionColumn--;
            }
            setChanged();
            notifyObservers();
        }
        catch(Exception e){}
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }
}

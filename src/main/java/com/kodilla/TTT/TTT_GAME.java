package com.kodilla.TTT;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class TTT_GAME extends Application {

    //all GUI variable
    private GridPane gridPane = new GridPane();
    private BorderPane borderPane = new BorderPane();
    private Label title = new Label("TTT Game");
    private Button restart = new Button("Restart");
    private Button result = new Button("Load Game");
    private Button save = new Button("Save Game");
    Font font = Font.font("VERDANA", FontWeight.EXTRA_BOLD, 23);
    Font font1 = Font.font("VERDANA", FontWeight.EXTRA_BOLD, 20);
    Font font2 = Font.font("VERDANA", FontWeight.EXTRA_BOLD, 16);
    private int scoreX = 0;
    private int scoreO = 0;
    private Random random = new Random();
    //array of buttons
    private Button[] butttons = new Button[9];
    //all logic game variables
    boolean gameOver = false;
    private LinkedList<Button> listEmptyButtons = new LinkedList<>();
    int activePlayer = 1;
    int gameState[] = {9, 9, 9, 9, 9, 9, 9, 9, 9};
    int winPosition[][] = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };

    @Override
    public void start(Stage primaryStage) throws Exception {

        // title and restart button
        borderPane.setTop(title);
        borderPane.setBottom(restart);
        borderPane.setRight(result);
        borderPane.setLeft(save);
        borderPane.setAlignment(title, Pos.CENTER);
        borderPane.setAlignment(restart, Pos.CENTER);
        borderPane.setAlignment(result, Pos.TOP_RIGHT);
        borderPane.setAlignment(save, Pos.TOP_LEFT);
        title.setFont(font);
        restart.setFont(font1);
        result.setFont(font2);
        save.setFont(font2);
        restart.setDisable(true);
        borderPane.setPadding(new Insets(15, 15, 30, 35));

        //adding 9 buttons
        int number = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                Button button = createButton(number);
                gridPane.setAlignment(Pos.CENTER);
                button.setUserData("*");
                butttons[number] = button;
                listEmptyButtons.add(button);
                gridPane.add(button, j, i);
                number = number + 1;
            }
        }
        borderPane.setCenter(gridPane);

        this.handleEvent();

        Scene scene = new Scene(borderPane, 700, 650, Color.WHITE);
        primaryStage.setTitle("TIC TAC TOE");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createButton(int number) {

        Button button = new Button();
        button.setId(number + "");
        button.setPrefHeight(150);
        button.setPrefWidth(150);
        return button;
    }

    //method for hand event
    private void handleEvent() {

        //restart button
        restart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                for (int i = 0; i < 9; i++) {
                    gameState[i] = 9;
                    butttons[i].setGraphic(null);
                    listEmptyButtons.add(butttons[i]);
                    gameOver = false;
                    restart.setDisable(true);
                }
            }
        });
        //Save state button
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    File saveGame = new File("gameState.txt");
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveGame));
                    SaveGameData saveGameData = new SaveGameData();
                    int x = 0;
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            saveGameData.addButton(butttons[x], i, j);
                            x++;
                        }
                    }
                    oos.writeObject(saveGameData);
                    oos.close();
                    System.out.println("Serialized data is saved in Game State");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //Load  file button
        result.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream("gameState.txt"));
                    SaveGameData read = (SaveGameData) ois.readObject();
                    ois.close();
                    int x = 0;
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            read.addButton(butttons[x], i, j);
                        }
                    }
                    System.out.println(read);
                } catch (Exception e) {

                }
            }
        });

        for (Button listButton : butttons) {
            listButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Button temporaryButton = (Button) actionEvent.getSource();
                    listEmptyButtons.remove(temporaryButton);
                    String idStringButton = temporaryButton.getId();
                    int idIntButton = Integer.parseInt(idStringButton);

                    if (gameOver) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("Game over!  Restart the game");
                        alert.show();
                    } else {
                        //check for player

                        if (gameState[idIntButton] == 9) {
                            //continue playing


                            if (activePlayer == 1) {
                                //queue 1

                                temporaryButton.setGraphic(new ImageView(new Image("file:src/main/resources/x.png")));
                                temporaryButton.setUserData("X");
                                gameState[idIntButton] = activePlayer;
                                checkWinner();
                                if (listEmptyButtons.size() > 0) {
                                    int generateNumber = random.nextInt(listEmptyButtons.size());

                                    activePlayer = 0;
                                    listEmptyButtons.get(generateNumber).fire();
                                }
                            } else {
                                //queue 0
                                // if (activePlayer == 0) {
                                temporaryButton.setGraphic(new ImageView(new Image("file:src/main/resources/o.png")));
                                temporaryButton.setUserData("O");
                                gameState[idIntButton] = activePlayer;
                                checkWinner();
                                // }

                                activePlayer = 1;
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Change position");
                            alert.show();
                        }
                    }
                }
            });
        }
    }

    // this method is checking winner
    private void checkWinner() {
        if (listEmptyButtons.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(("Try again..."));
            alert.show();
            gameOver = false;
            restart.setDisable(false);
        }
        if (!gameOver) {

            for (int wp[] : winPosition) {

                if (gameState[wp[0]] == gameState[wp[1]] && gameState[wp[1]] == gameState[wp[2]] && gameState[wp[1]] != 9) {
                    //activePlayer has winner

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText(activePlayer + " : " + "win the game!!!");
                    alert.show();
                    gameOver = true;
                    restart.setDisable(false);
                }
                break;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

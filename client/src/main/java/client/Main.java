package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final Object lock = new Object();
    private static volatile char currentLetter = 'A';
    private static final int numbers = 5;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 500, 300));
        primaryStage.show();
    }


    public static void main(String[] args) {
        Thread t1 = new Thread(Main::printA);
        Thread t2 = new Thread(Main::printB);
        Thread t3 = new Thread(Main::printC);
        t1.start();
        t2.start();
        t3.start();

        launch(args);
    }

    public static void printA() {
        for (int i = 0; i < numbers; i++) {
            synchronized (lock) {
                try {

                    while (currentLetter != 'A') {
                        lock.wait();
                    }
                    System.out.print("A");
                    currentLetter = 'B';
                    lock.notifyAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void printB() {
        for (int i = 0; i < numbers; i++) {
            synchronized (lock) {
                try {

                    while (currentLetter != 'B') {
                        lock.wait();
                    }
                    System.out.print("B");
                    currentLetter = 'C';
                    lock.notifyAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void printC() {
        for (int i = 0; i < numbers; i++) {
            synchronized (lock) {
                try {
                    while (currentLetter != 'C') {
                        lock.wait();
                    }
                    System.out.print("C\n");
                    currentLetter = 'A';
                    lock.notifyAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

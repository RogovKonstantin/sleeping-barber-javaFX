package com.example.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class BarberShop extends Application {
    final static int CLIENTS = 17;
    static Circle circle = new Circle(50);
    static Circle circle1 = new Circle(50);
    static Circle circle2 = new Circle(50);

    public static void main(String[] args) throws InterruptedException {
        Thread javaFx = new Thread(() -> launch(args));
        javaFx.start();
        Semaphore chair = new Semaphore(3);
        Semaphore barber = new Semaphore(1, false);

        int a = 0;
        for (int i = 0; i <= CLIENTS; i++) {
            Random random = new Random();
            int delay = random.nextInt(2000);
            Visitor visitor = new Visitor();
            if (a == 0) {
                visitor.chairs = chair;
                visitor.barber = barber;
                visitor.circle = BarberShop.circle;
                a++;
            } else if (a == 1) {
                visitor.chairs = chair;
                visitor.barber = barber;
                visitor.circle = circle1;
                a++;
            } else if (a == 2) {
                visitor.chairs = chair;
                visitor.barber = barber;
                visitor.circle = circle2;
                a = 0;
            }
            visitor.start();
            Visitor.sleep(delay);

        }

    }

    @Override
    public void start(Stage primaryStage) {
        circle.setCenterX(100);
        circle.setCenterY(240);
        circle1.setCenterX(300);
        circle1.setCenterY(240);
        circle2.setCenterX(500);
        circle2.setCenterY(240);
        circle.setFill(Color.PAPAYAWHIP);
        circle1.setFill(Color.PAPAYAWHIP);
        circle2.setFill(Color.PAPAYAWHIP);

        Group root = new Group();
        root.getChildren().addAll(circle, circle1, circle2);
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setTitle("Barbershop");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    static class Visitor extends Thread {
        private Circle circle;
        private Semaphore chairs;
        private Semaphore barber;


        @Override
        public void run() {

            try {
                if (chairs.tryAcquire(1, 6000, TimeUnit.MILLISECONDS)) {
                    sleep(1500);
                    System.out.println("Клиент " + this.getName() + " занял место");
                    circle.setFill(Color.YELLOW);
                    sleep(1500);
                    System.out.println("Клиент " + this.getName() + " ожидает стрижки");
                    barber.acquire(1);
                    System.out.println(this.getName() + " стригут");
                    circle.setFill(Color.RED);
                    sleep(1500);
                    System.out.println("Клиента " + this.getName() + " постригли");
                    circle.setFill(Color.PAPAYAWHIP);
                    barber.release(1);
                    chairs.release(1);
                    sleep(1500);
                } else {
                    System.out.println(this.getName() + " Не было мест, ушел");
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

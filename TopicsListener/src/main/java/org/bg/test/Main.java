package org.bg.test;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ConcurrentHashMap<String, TopicStatus> stringLongTopics = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, TopicStatus> stringStringTopics = new ConcurrentHashMap<>();
        ArrayList<String> stringStringTopicNames = new ArrayList<>();
        stringStringTopicNames.add("user-activities-count");       // for que 2
        stringStringTopicNames.add("page-activities-count");       // for que 3
        stringStringTopicNames.stream().forEach(s -> {
            stringStringTopics.put(s, new TopicStatus(s));
        });

        ArrayList<String> stringLongTopicNames = new ArrayList<>();
        stringLongTopicNames.add("page-creation-count");         // for que 1.a
        stringLongTopicNames.add("page-update-count");           // for que 1.b
        stringLongTopicNames.add("page-revert-action-count");    // for que 1.c
        stringLongTopicNames.stream().forEach(s -> {
            stringLongTopics.put(s, new TopicStatus(s));
        });

        try {
            ExecutorService service = Executors.newFixedThreadPool(2);
            final BasicConsumeLoop<String, String> stringStringConsumer =
                    new BasicConsumeLoop<>(BasicConsumeLoop.stringStringConsumerConfig(), stringStringTopicNames, stringStringTopics);
            final BasicConsumeLoop<String, Long> stringLongConsumer =
                    new BasicConsumeLoop<>(BasicConsumeLoop.stringLongConsumerConfig(), stringLongTopicNames, stringLongTopics);
            service.execute(stringStringConsumer);
            service.execute(stringLongConsumer);

            printGreeting();
            Scanner in = new Scanner(System.in);
            int choose = in.nextInt();
            while (choose != 99) {
                handleUserRequest(stringStringTopics, stringLongTopics, choose);
                choose = in.nextInt();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    stringStringConsumer.close();
                }
            }));
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    stringLongConsumer.close();
                }
            }));
            service.shutdown();

            System.out.println("Bye Bye");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Exception has been caught :O, it's message is: " + e.getMessage());
            System.exit(0);
        }
    }

    private static void handleUserRequest(ConcurrentHashMap<String, TopicStatus> stringStringTopicStatus,
                                          ConcurrentHashMap<String, TopicStatus> stringLongTopicStatus, int choose) {
        printMenuAndExecuteCommand(stringStringTopicStatus, stringLongTopicStatus, choose);
        printGreeting();
    }

    private static void printMenuAndExecuteCommand(ConcurrentHashMap<String, TopicStatus> topicsToStatus,
                                                   ConcurrentHashMap<String, TopicStatus> stringLongTopicStatus,
                                                   int choose) {
        switch (choose) {
            case 1:
                System.out.println("Number of pages created is: " + stringLongTopicStatus.get("page-creation-count").getCounter());
                break;
            case 2:
                System.out.println("Number of pages updated is: " + stringLongTopicStatus.get("page-update-count").getCounter());
                break;
            case 3:
                System.out.println("Number of pages reverted is: " + stringLongTopicStatus.get("page-revert-action-count").getCounter());
                break;
            case 4:
                System.out.println("most active users are \n: " + topicsToStatus.get("user-activities-count"));
                break;
            case 5:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            default:
                System.out.println("Unknown option.. please try again...");
        }
    }

    private static void printGreeting() {
        System.out.println("===================================================");
        System.out.println("Hello user! this is our super statistics system");
        System.out.println("Press 1 for total pages count");
        System.out.println("Press 2 for total pages changes count");
        System.out.println("Press 3 for total pages revert count");
        System.out.println("Press 4 for most active users");
        System.out.println("Press 5 for most active pages");
        System.out.println("Enter 99 to stop and exit!");
        System.out.println("===================================================");
    }
}

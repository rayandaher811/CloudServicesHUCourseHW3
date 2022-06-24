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

        stringStringTopicNames.add("page-activities-hourly-count");     // for que 1 low
        stringStringTopicNames.add("user-activities-hourly-count");     // for que 1 low
        stringStringTopicNames.add("page-activities-daily-count");      // for que 1 low
        stringStringTopicNames.add("user-activities-daily-count");      // for que 1 low
        stringStringTopicNames.add("page-activities-weekly-count");     // for que 1 low
        stringStringTopicNames.add("user-activities-weekly-count");     // for que 1 low
        stringStringTopicNames.add("page-activities-monthly-count");    // for que 1 low
        stringStringTopicNames.add("user-activities-monthly-count");    // for que 1 low
        stringStringTopicNames.stream().forEach(s -> {
            stringStringTopics.put(s, new TopicStatus(s));
        });

        ArrayList<String> stringLongTopicNames = new ArrayList<>();
        stringLongTopicNames.add("page-creation-count");         // for que 1.a
        stringLongTopicNames.add("page-update-count");           // for que 1.b
        stringLongTopicNames.add("page-revert-action-count");    // for que 1.c

        stringLongTopicNames.add("page-creation-hourly-count");         // for que 1 low
        stringLongTopicNames.add("page-revert-action-hourly-count");    // for que 1 low
        stringLongTopicNames.add("page-update-hourly-count");           // for que 1 low
        stringLongTopicNames.add("page-creation-daily-count");          // for que 1 low
        stringLongTopicNames.add("page-revert-action-daily-count");     // for que 1 low
        stringLongTopicNames.add("page-update-daily-count");            // for que 1 low
        stringLongTopicNames.add("page-creation-weekly-count");         // for que 1 low
        stringLongTopicNames.add("page-revert-action-weekly-count");    // for que 1 low
        stringLongTopicNames.add("page-update-weekly-count");           // for que 1 low
        stringLongTopicNames.add("page-creation-monthly-count");        // for que 1 low
        stringLongTopicNames.add("page-revert-action-monthly-count");   // for que 1 low
        stringLongTopicNames.add("page-update-monthly-count");          // for que 1 low

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
            case 6:
                System.out.println("most active pages for last hour are: \n" + topicsToStatus.get("page-activities-hourly-count"));
                break;
            case 7:
                System.out.println("most active users for last hour are: \n" + topicsToStatus.get("user-activities-hourly-count"));
                break;
            case 8:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 9:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 10:
                System.out.println("most active pages last week are: \n" + topicsToStatus.get("page-activities-weekly-count"));
                break;
            case 11:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 12:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 13:
                System.out.println("most active users monthly are: \n" + topicsToStatus.get("user-activities-monthly-count"));
                break;
            case 14:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 15:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 16:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 17:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 18:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 19:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 20:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 21:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 22:
                System.out.println("number of update events in passed week: \n" + stringLongTopicStatus.get("page-update-weekly-count").getCounter());
                break;
            case 23:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 24:
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 25:
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
        System.out.println("Press 6 for page activities count hourly");
        System.out.println("Press 7 for user activities count hourly");
        System.out.println("Press 8 for page activities count daily");
        System.out.println("Press 9 for user activities count daily");
        System.out.println("Press 10 for page activities count weekly");
        System.out.println("Press 11 for user activities count weekly");
        System.out.println("Press 12 for page activities count monthly");
        System.out.println("Press 13 for user activities count monthly");
        System.out.println("Press 14 for page creation hourly count");
        System.out.println("Press 15 for page revert hourly count");
        System.out.println("Press 16 for page update hourly count");
        System.out.println("Press 17 for page creation count daily");
        System.out.println("Press 18 for page revert daily count");
        System.out.println("Press 19 for page update daily count");
        System.out.println("Press 20 for page creation weekly count");
        System.out.println("Press 21 for page revert weekly count");
        System.out.println("Press 22 for page update weekly count");
        System.out.println("Press 23 for page creation monthly count");
        System.out.println("Press 24 for page revert action monthly count");
        System.out.println("Press 25 for page update monthly count");
        System.out.println("Enter 99 to stop and exit!");
        System.out.println("===================================================");
    }
}

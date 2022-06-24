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

        ConcurrentHashMap<String, TopicStatus> botsNonBotsStringLongTopics = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, TopicStatus> botsNonBotsStringStringTopics = new ConcurrentHashMap<>();
        ArrayList<String> botsNonBotsStringStringTopicNames = new ArrayList<>();
        botsNonBotsStringStringTopicNames.add("user-activities-bots-count");        // for que 2.a
        botsNonBotsStringStringTopicNames.add("user-activities-none-bots-count");   // for que 2.a
        botsNonBotsStringStringTopicNames.add("page-activities-bots-count");        // for que 2.a
        botsNonBotsStringStringTopicNames.add("page-activities-none-bots-count");   // for que 2.a
        botsNonBotsStringStringTopicNames.stream().forEach(s -> {
            botsNonBotsStringStringTopics.put(s, new TopicStatus(s));
        });

        ArrayList<String> botsNonBotsStringLongTopicNames = new ArrayList<>();
        botsNonBotsStringLongTopicNames.add("page-creation-bots-count");            // for que 2.a
        botsNonBotsStringLongTopicNames.add("page-creation-none-bots-count");       // for que 2.a
        botsNonBotsStringLongTopicNames.add("page-revert-action-bots-count");       // for que 2.a
        botsNonBotsStringLongTopicNames.add("page-revert-action-none-bots-count");  // for que 2.a
        botsNonBotsStringLongTopicNames.add("page-update-bots-count");              // for que 2.a
        botsNonBotsStringLongTopicNames.add("page-update-none-bots-count");         // for que 2.a
        botsNonBotsStringLongTopicNames.stream().forEach(s -> {
            botsNonBotsStringLongTopics.put(s, new TopicStatus(s));
        });

        ConcurrentHashMap<String, TopicStatus> languagesStringStringTopics = new ConcurrentHashMap<>();
        ArrayList<String> languagesStringStringTopicNames = new ArrayList<>();
        languagesStringStringTopicNames.add("page-activities-language-count");    // for que 3.a + b
        languagesStringStringTopicNames.add("user-activities-language-count");
        languagesStringStringTopicNames.stream().forEach(s -> {
            languagesStringStringTopics.put(s, new TopicStatus(s));
        });

        ArrayList<String> languagesStringLongTopicNames = new ArrayList<>();
        ConcurrentHashMap<String, TopicStatus> languagesStringLongTopics = new ConcurrentHashMap<>();
        languagesStringLongTopicNames.add("page-creation-language-count");    // for que 3.a + b
        languagesStringLongTopicNames.add("page-revert-action-language-count");
        languagesStringLongTopicNames.add("page-update-language-count");
        languagesStringLongTopicNames.stream().forEach(s -> {
            languagesStringLongTopics.put(s, new TopicStatus(s));
        });

        try {
            ExecutorService service = Executors.newCachedThreadPool();
            final BasicConsumeLoop<String, String> stringStringConsumer =
                    new BasicConsumeLoop<>(BasicConsumeLoop.stringStringConsumerConfig(), stringStringTopicNames, stringStringTopics);
            final BasicConsumeLoop<String, Long> stringLongConsumer =
                    new BasicConsumeLoop<>(BasicConsumeLoop.stringLongConsumerConfig(), stringLongTopicNames, stringLongTopics);
            final BasicConsumeLoop<String, String> botsNonBotsStringStringConsumer =
                    new BasicConsumeLoop<>(BasicConsumeLoop.stringStringConsumerConfig(), botsNonBotsStringStringTopicNames, botsNonBotsStringStringTopics);
            final BasicConsumeLoop<String, Long> botsNonBotsStringLongConsumer =
                    new BasicConsumeLoop<>(BasicConsumeLoop.stringLongConsumerConfig(), botsNonBotsStringLongTopicNames, botsNonBotsStringLongTopics);
            final BasicConsumeLoop<String, String> languagesStringStringConsumer =
                    new BasicConsumeLoop<>(BasicConsumeLoop.stringStringConsumerConfig(), languagesStringStringTopicNames, languagesStringStringTopics);
            final BasicConsumeLoop<String, String> languagesStringLongConsumer =
                    new BasicConsumeLoop<>(BasicConsumeLoop.stringLongConsumerConfig(), languagesStringLongTopicNames, languagesStringLongTopics);

            service.execute(stringStringConsumer);
            service.execute(stringLongConsumer);
            service.execute(botsNonBotsStringStringConsumer);
            service.execute(botsNonBotsStringLongConsumer);
            service.execute(languagesStringStringConsumer);
            service.execute(languagesStringLongConsumer);

            printGreeting();
            Scanner in = new Scanner(System.in);
            int choose = in.nextInt();
            while (choose != 99) {
                handleUserRequest(stringStringTopics, stringLongTopics, botsNonBotsStringStringTopics,
                        botsNonBotsStringLongTopics, languagesStringStringTopics, languagesStringLongTopics, choose);
                choose = in.nextInt();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(stringStringConsumer::close));
            Runtime.getRuntime().addShutdownHook(new Thread(stringLongConsumer::close));
            Runtime.getRuntime().addShutdownHook(new Thread(botsNonBotsStringLongConsumer::close));
            Runtime.getRuntime().addShutdownHook(new Thread(botsNonBotsStringStringConsumer::close));
            Runtime.getRuntime().addShutdownHook(new Thread(languagesStringStringConsumer::close));
            service.shutdown();

            System.out.println("Bye Bye");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Exception has been caught :O, it's message is: " + e.getMessage());
            System.exit(0);
        }
    }

    private static void handleUserRequest(ConcurrentHashMap<String, TopicStatus> stringStringTopicStatus,
                                          ConcurrentHashMap<String, TopicStatus> stringLongTopicStatus,
                                          ConcurrentHashMap<String, TopicStatus> botsNonBotsStringStringTopicStatus,
                                          ConcurrentHashMap<String, TopicStatus> botsNonBotsStringLongTopicStatus,
                                          ConcurrentHashMap<String, TopicStatus> languagesStringStringTopicStatus,
                                          ConcurrentHashMap<String, TopicStatus> languagesStringLongTopicStatus,
                                          int choose) {
        printMenuAndExecuteCommand(stringStringTopicStatus, stringLongTopicStatus, botsNonBotsStringStringTopicStatus,
                botsNonBotsStringLongTopicStatus, languagesStringStringTopicStatus, languagesStringLongTopicStatus, choose);
        printGreeting();
    }

    private static void printMenuAndExecuteCommand(ConcurrentHashMap<String, TopicStatus> topicsToStatus,
                                                   ConcurrentHashMap<String, TopicStatus> stringLongTopicStatus,
                                                   ConcurrentHashMap<String, TopicStatus> botsNonBotsStringStringTopicStatus,
                                                   ConcurrentHashMap<String, TopicStatus> botsNonBotsStringLongTopicStatus,
                                                   ConcurrentHashMap<String, TopicStatus> languagesStringStringTopicStatus,
                                                   ConcurrentHashMap<String, TopicStatus> languagesStringLongTopicStatus,
                                                   int choose) {
        switch (choose) {
            case 1:
                System.out.println("Total pages created: " + stringLongTopicStatus.get("page-creation-count").getCounter());
                System.out.println("Total pages updated: " + stringLongTopicStatus.get("page-update-count").getCounter());
                System.out.println("Total number of reverts: " + stringLongTopicStatus.get("page-revert-action-count").getCounter());
                System.out.println("most active users are :\n " + topicsToStatus.get("user-activities-count"));
                System.out.println("most active pages are: \n" + topicsToStatus.get("page-activities-count"));
                break;
            case 2:
                System.out.println("Last hour statistics:");
                System.out.println("most active pages : \n" + topicsToStatus.get("page-activities-hourly-count"));
                System.out.println("most active users: \n" + topicsToStatus.get("user-activities-hourly-count"));
                System.out.println("Number of pages created: \n" + stringLongTopicStatus.get("page-creation-hourly-count").getCounter());
                System.out.println("Number of reverts: \n" + stringLongTopicStatus.get("page-revert-action-hourly-count").getCounter());
                System.out.println("Number of updates: \n" + stringLongTopicStatus.get("page-update-hourly-count").getCounter());
                break;
            case 3:
                System.out.println("Last Day statistics:");
                System.out.println("most active pages : \n" + topicsToStatus.get("page-activities-daily-count"));
                System.out.println("most active users: \n" + topicsToStatus.get("user-activities-daily-count"));
                System.out.println("Number of pages created: \n" + stringLongTopicStatus.get("page-creation-daily-count").getCounter());
                System.out.println("Number of reverts: \n" + stringLongTopicStatus.get("page-revert-action-daily-count").getCounter());
                System.out.println("Number of updates: \n" + stringLongTopicStatus.get("page-update-daily-count").getCounter());
                break;
            case 4:
                System.out.println("Weekly statistics:");
                System.out.println("most active pages : \n" + topicsToStatus.get("page-activities-weekly-count"));
                System.out.println("most active users: \n" + topicsToStatus.get("user-activities-weekly-count"));
                System.out.println("Number of pages created: \n" + stringLongTopicStatus.get("page-creation-weekly-count").getCounter());
                System.out.println("Number of reverts: \n" + stringLongTopicStatus.get("page-revert-action-weekly-count").getCounter());
                System.out.println("Number of updates: \n" + stringLongTopicStatus.get("page-update-weekly-count").getCounter());
                break;
            case 5:
                System.out.println("Monthly statistics:");
                System.out.println("most active pages : \n" + topicsToStatus.get("page-activities-weekly-count"));
                System.out.println("most active users: \n" + topicsToStatus.get("user-activities-weekly-count"));
                System.out.println("Number of pages created: \n" + stringLongTopicStatus.get("page-creation-weekly-count").getCounter());
                System.out.println("Number of reverts: \n" + stringLongTopicStatus.get("page-revert-action-weekly-count").getCounter());
                System.out.println("Number of updates: \n" + stringLongTopicStatus.get("page-update-weekly-count").getCounter());
                break;
            case 6:
                System.out.println("Bots vs non-bots comparison:");

                System.out.println("\nMost active human users:");
                botsNonBotsStringStringTopicStatus.get("user-activities-none-bots-count").getKeyValueMapper().forEach((s, aLong) -> {
                    System.out.println("User: " + s + ", Events: " + aLong);
                });

                System.out.println("\nMost active bots:");
                botsNonBotsStringStringTopicStatus.get("user-activities-bots-count").getKeyValueMapper().forEach((s, aLong) -> {
                    System.out.println("Bot: " + s + ", Events: " + aLong);
                });

                System.out.println("\nMost active pages thanks to bots:");
                botsNonBotsStringStringTopicStatus.get("page-activities-bots-count").getKeyValueMapper().forEach((s, aLong) -> {
                    System.out.println("Page: " + s + ", Events: " + aLong);
                });
                System.out.println("\nMost active pages thanks to humans:");
                botsNonBotsStringStringTopicStatus.get("page-activities-none-bots-count").getKeyValueMapper().forEach((s, aLong) -> {
                    System.out.println("Page: " + s + ", Events: " + aLong);
                });
                printDataAndCalculateRatio("\nPage creations",
                        botsNonBotsStringLongTopicStatus.get("page-creation-none-bots-count").getCounter(),
                        botsNonBotsStringLongTopicStatus.get("page-creation-bots-count").getCounter());
                printDataAndCalculateRatio("\nPages reverts",
                        botsNonBotsStringLongTopicStatus.get("page-revert-action-none-bots-count").getCounter(),
                        botsNonBotsStringLongTopicStatus.get("page-revert-action-bots-count").getCounter());
                printDataAndCalculateRatio("\nPages changes",
                        botsNonBotsStringLongTopicStatus.get("page-update-none-bots-count").getCounter(),
                        botsNonBotsStringLongTopicStatus.get("page-update-bots-count").getCounter());
                break;
            case 7:
                System.out.println("Pages creation by languages:"); // 3.1.a
                languagesStringLongTopicStatus.get("page-creation-language-count").getKeyValueMapper().forEach((s, aLong) -> {
                    System.out.println("Language: " + s + ", pages: " + aLong);
                });
                System.out.println("\nPage changes by language:"); // 3.1.b
                languagesStringLongTopicStatus.get("page-update-language-count").getKeyValueMapper().forEach((s, aLong) -> {
                    System.out.println("Language: " + s + ", pages: " + aLong);
                });
                System.out.println("\nReverts by languages:"); // 3.1.c
                languagesStringLongTopicStatus.get("page-revert-action-language-count").getKeyValueMapper().forEach((s, aLong) -> {
                    System.out.println("Language: " + s + ", pages: " + aLong);
                });
                break;
            case 8:
                System.out.println("\nMost active users in each language: \n" + languagesStringStringTopicStatus.get("user-activities-language-count").getLanguageMapperAsString()); // 3.2
                break;
            case 9:
                System.out.println("\nMost active pages in each language: \n" + languagesStringStringTopicStatus.get("page-activities-language-count").getLanguageMapperAsString()); // 3.3
                break;
            default:
                System.out.println("Unknown option.. please try again...");
        }
    }

    private static void printDataAndCalculateRatio(String eventName, Long regularUserEvents, Long botUserEvents) {
        Long sum = regularUserEvents + botUserEvents;
        System.out.println(eventName + ", total events: " + sum
                + ", human : " + regularUserEvents + ", bot: " + botUserEvents);
    }

    private static void printGreeting() {
        System.out.println("===================================================");
        System.out.println("Hello user! this is our super statistics system");
        System.out.println("Press 1 for all-time statistics");
        System.out.println("Press 2 for hourly statistics");
        System.out.println("Press 3 for daily statistics");
        System.out.println("Press 4 for weekly statistics");
        System.out.println("Press 5 for monthly statistics");
        System.out.println("Press 6 for bots and human users statistics");
        System.out.println("Press 7 for language statistics");
        System.out.println("Press 8 for most active users in each language");
        System.out.println("Press 9 for most active pages in each language");
        System.out.println("Enter 99 to stop and exit!");
        System.out.println("===================================================");
    }
}

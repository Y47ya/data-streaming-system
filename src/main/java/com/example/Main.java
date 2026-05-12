package com.example;

import java.io.File;
import java.util.Scanner;

import com.example.CSVTCPSender;
import com.example.ParquetTCPSender;
import com.example.Sender;

public class Main {
    public static void main(String[] args) {

        System.setProperty("hadoop.home.dir", "/");

        String address = "127.0.0.1";
        int port = 1212;

        String folderPath = "data";
        File folder = new File(folderPath);

        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("Folder is empty!");
            return;
        }

        System.out.println("Available datasets:");
        for (int i = 0; i < files.length; i++) {
            System.out.println(i + ": " + files[i].getName());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of the dataset to use: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 0 || choice >= files.length) {
            System.out.println("Invalid choice!");
            scanner.close();
            return;
        }

        String selectedDataPath = files[choice].getPath();

        System.out.print("CSV separator (default ','): ");
        String separator = scanner.nextLine();
        if (separator.isEmpty()) {
            separator = ",";
        }

        System.out.print("Enter the streamingDelay (ms): ");
        int streamingDelay = scanner.nextInt();

        scanner.close();

        Sender sender;
        if (selectedDataPath.endsWith(".csv")) {
            sender = new CSVTCPSender(selectedDataPath, address, port, separator, streamingDelay);
        } else if (selectedDataPath.endsWith(".parquet")) {
            sender = new ParquetTCPSender(selectedDataPath, address, port, separator, streamingDelay);
        } else {
            System.out.println("Unsupported file format");
            return;
        }

        Thread thread = new Thread((Runnable) sender);
        thread.start();
    }
}
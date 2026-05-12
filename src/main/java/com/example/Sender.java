package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;


public class Sender{

    private Socket socket;
    private int port;
    private String dataPath;
    private String separator;
    private int streamingDelay;

    public Sender(String dataPath, int port, String separator, int streamingDelay) {
        this.dataPath = dataPath;
        this.port = port;
        this.separator = separator;
        this.streamingDelay = streamingDelay;

        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("Waiting for Spark to connect on port " + this.port + " ...");

            this.socket = serverSocket.accept();

            System.out.println("Spark connected!");
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
    }

    public Socket getSocket(){
        return this.socket;
    }

    public int getPort() { return this.port; }

    public String getSeparator() { return this.separator; }

    public String getDataPath() { return this.dataPath; }

    public int getStreamingDelay() { return this.streamingDelay; }

    public ArrayList<String> getDataColsNames(BufferedReader data) {

        ArrayList<String> cols = null;

        try {
            String line = data.readLine();

            cols = new ArrayList<>();

            for (String col : line.split(",")) {
                String cleanCol = col.trim()
                        .toLowerCase()
                        .replaceAll(" ", "_");
                cols.add(cleanCol);
            }

        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }

        return cols;

    }

}





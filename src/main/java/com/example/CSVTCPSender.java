package com.example;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CSVTCPSender extends Sender implements Runnable {


    public CSVTCPSender(String localDataPath, String address, int port, String seperator, int streamingDelay) {
        super(localDataPath, port, seperator, streamingDelay);
    }

    public BufferedReader readCSVData(){

        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new FileReader(this.getDataPath()));

        } catch (IOException exception) {
            System.out.println(exception);
        }

        return bufferedReader;
    }

    public Map<String, String> toJsonString(String line, ArrayList<String> headers){
        String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        Map<String, String> jsonMap = new HashMap<>();
        for (int i = 0; i < headers.size() && i < values.length; i++){
            String cleanValue = values[i].replaceAll("^\"|\"$", "").trim();
            jsonMap.put(headers.get(i), cleanValue);
        }
        return jsonMap;
    }

    @Override
    public void run(){

        BufferedReader data = this.readCSVData();
        ArrayList<String> headers = this.getDataColsNames(data);

        if (data == null){
            System.out.println("Invalid data path!");
            return;
        }

        String line;

        try {

            System.out.println("Sending data...");

            while ((line = data.readLine()) != null) {

                Gson gson = new Gson();

                Map<String, String> event = this.toJsonString(line, headers);

                String jsonLine = gson.toJson(event);

                OutputStream out = this.getSocket().getOutputStream();
                out.write((jsonLine + "\n").getBytes(StandardCharsets.UTF_8));
                out.flush();

                int streamingDelay = this.getStreamingDelay();

                Thread.sleep(streamingDelay);

            }
        } catch (IOException exception){
            System.out.println(exception.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
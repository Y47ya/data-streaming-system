package com.example;

import com.example.Sender;
import com.google.gson.Gson;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ParquetTCPSender extends Sender implements Runnable{

    public ParquetTCPSender(String dataPath, String address, int port, String separator, int streamingDelay) {
        super(dataPath, port, separator, streamingDelay);
    }

    public ParquetReader<Group> readParquet() {


        try {

            Path path = new Path(this.getDataPath());
            System.out.println(path);
            Configuration conf = new Configuration();

            ParquetReader<Group> reader = ParquetReader
                    .builder(new GroupReadSupport(), path)
                    .withConf(conf)
                    .build();

            return reader;

        } catch (IOException exception) {
            System.out.println(exception);
            return null;
        }


    }

    @Override
    public void run() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            this.getSocket().getOutputStream(),
                            StandardCharsets.UTF_8
                    )
            );

            ParquetReader<Group> reader = this.readParquet();
            Gson gson = new Gson();
            Group group;

            while ((group = reader.read()) != null) {

                Map<String, Object> rowData = new HashMap<>();

                int fieldCount = group.getType().getFieldCount();

                for (int i = 0; i < fieldCount; i++) {
                    String fieldName = group.getType().getFieldName(i);
                    String value;
                    try {
                        value = group.getValueToString(i, 0);
                    } catch (Exception e) {
                        value = "";
                    }

                    rowData.put(fieldName, value);
                }


                String jsonOutput = gson.toJson(rowData);


                bufferedWriter.write(jsonOutput);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                System.out.println("Sent: " + jsonOutput);

                int streamingDelay = this.getStreamingDelay();

                Thread.sleep(streamingDelay);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error in streamer: " + e.getMessage());
        }
    }

}

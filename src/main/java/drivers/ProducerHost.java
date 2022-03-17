package drivers;

import framework.Producer;
import utils.Config;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ProducerHost {

    public static void main(String args[]){
        if (args.length != 4){
            System.out.println("Usage of the application is: java drivers ProducerHost <hostName> ");
            System.exit(1);
        }

        String producerHostName = args[3];
        String file = Config.producerAndFile.get(producerHostName);
        Producer producer = new Producer("broker", producerHostName);
        run(producer, file);
    }

    public static void run(Producer producer, String file){
        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1))) {
            String line;

            while ((line = br.readLine()) != null) {
                byte[] data = line.getBytes(StandardCharsets.UTF_8);
                String topic = Config.topics.get(file);
                producer.send(topic, data);

            }
            producer.close();

        }catch (FileNotFoundException e) {
            System.out.println("File does not exist!");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

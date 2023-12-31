package br.com.ecommerce.service;

import br.com.ecommerce.Main;
import br.com.ecommerce.service.contract.KafkaConsumerInterface;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;

import static br.com.ecommerce.Main.ECOMMERCE_NEW_ORDER;
import static br.com.ecommerce.Main.threadStarting;
import static br.com.ecommerce.config.KafkaProperties.consumerProperties;

public class FraudDetectorService extends Thread implements KafkaConsumerInterface {

    public FraudDetectorService() {
        threadStarting(FraudDetectorService.class);
        this.start();
    }

    public void run() {
        try (var consumer = new KafkaConsumer<String, String>(consumerProperties(FraudDetectorService.class))) {
            consumer.subscribe(Collections.singletonList(ECOMMERCE_NEW_ORDER));
            while (true) {

                if (!this.isAlive()) {
                    return;
                }

                var records = consumer.poll(Duration.ofMillis(100));
                if (!records.isEmpty()) {
                    System.out.println("Found records");
                    records.forEach(record -> {
                        System.out.println("-------------------------------------");
                        System.out.println("Processing new order, checking for fraud");
                        System.out.println(record.key());
                        System.out.println(record.value());
                        Main.sleep();
                        System.out.println("Order processed");
                    });
                }
            }
        } catch (Exception ex) {
            System.out.println("ERROR with kafka consumer");
        }
    }
}

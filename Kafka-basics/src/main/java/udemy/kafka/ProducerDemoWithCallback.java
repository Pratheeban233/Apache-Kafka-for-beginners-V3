package udemy.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.stream.IntStream;

public class ProducerDemoWithCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerDemoWithCallback.class);

    public static void main(String[] args) {

        // kafka properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //kafka producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        //sending batch
        IntStream.rangeClosed(1, 10).forEach(i -> {
            //producerRecord
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_kafka", "Hello world! " + i);

            //send data
            producer.send(producerRecord, (recordMetadata, e) -> {
                if(e == null) {
                    LOGGER.info("Received new metadata --> \n" +
                            "Topic :" + recordMetadata.topic() + "\n" +
                            "Partition :" + recordMetadata.partition() + "\n" +
                            "offset :" + recordMetadata.offset() + "\n" +
                            "timestamp :" + recordMetadata.timestamp());
                } else {
                    LOGGER.info("Error while processing ", e);
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        //flush
        producer.flush();

        //close
        producer.close();

    }
}

package udemy.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerDemo.class);

    public static void main(String[] args) {

        // kafka properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //kafka producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        //producerRecord
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo_kafka", "Hello Dhiya bae!!!");

        //send data
        producer.send(producerRecord);

        //flush
        producer.flush();

        //close
        producer.close();

    }
}

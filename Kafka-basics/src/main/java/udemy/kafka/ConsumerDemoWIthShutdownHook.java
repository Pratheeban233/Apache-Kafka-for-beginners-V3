package udemy.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoWIthShutdownHook {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDemoWIthShutdownHook.class);

    public static void main(String[] args) {

        String topic = "first_topic";
        String group = "my_third_consumer_group";

        // kafka properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        consumer.subscribe(Arrays.asList(topic));

        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread("worker_thread") {
            @Override
            public void run() {
                LOGGER.info("Detected a shutdown, let's exit by call consumer.wakeup()");
                consumer.wakeup();

                //join the mainThread which allows the execution of the code with main thread
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            while (true) {

                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    LOGGER.info("Key : " + record.key() + ", value : " + record.value());
                    LOGGER.info("Partition : " + record.partition() + ", offset : " + record.offset());
                }
            }
        } catch (WakeupException e) {
            LOGGER.info("As expected wakeup exception!!!");
        } catch (Exception e) {
            LOGGER.error("unexpected error");
        } finally {
            consumer.close();
            LOGGER.info("The consumer is now gracefully closed.");
        }
    }
}

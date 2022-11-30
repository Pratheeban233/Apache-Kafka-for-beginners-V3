package udemy.kafka.wikimedia;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class WikimediaChangesProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikimediaChangesProducer.class);

    public static void main(String[] args) throws InterruptedException {

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //setting high throughput mechanism
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        String topic = "wikimedia.recent.change";
//        String url = "https://stream.wikimedia.org/v2/stream/recentchange";
//        EventHandler eventHandler = new WikimediaChangesHandler(producer, topic);
//        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));
//        EventSource eventSource = builder.build();
//
//        //run in sepate thread
//        eventSource.start();

        // trying this due to not able to connect wikimedia from code
        IntStream.rangeClosed(1, Integer.MAX_VALUE).forEach(value -> {
            String str = UUID.randomUUID() + "_" + value;
            JSONObject object = new JSONObject()
                    .put("id", value)
                    .put("UUID", str);
            producer.send(new ProducerRecord<>(topic, object.toString()));
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
        });
        TimeUnit.MINUTES.sleep(10);

        producer.flush();
        producer.close();

        //keytool -importcert -file C:\Users\EK512HV\Downloads\Softwares\wikimedia.cer -alias wikimedia -keystore "C:\Program Files\Java\jdk1.8.0_321\jre\lib\security\cacerts"
        //keytool -importcert -file C:\Users\EK512HV\Downloads\Softwares\wikimedia.cer -alias wikimedia -keystore "C:\Program Files\Java\jdk-11.0.17\lib\security\cacerts"
    }
}

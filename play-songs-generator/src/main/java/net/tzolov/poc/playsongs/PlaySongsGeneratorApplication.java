package net.tzolov.poc.playsongs;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import net.tzolov.poc.playsongs.avro.PlayEvent;
import net.tzolov.poc.playsongs.avro.Song;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

@SpringBootApplication
public class PlaySongsGeneratorApplication implements CommandLineRunner {

    // in topics
    public static final String PLAY_EVENTS_TOPIC = "play-events";
    public static final String SONGS_TOPIC = "song-feed";
    // out topics
    public static final String TOP_K_SONGS_PER_GENRE_TOPIC = "top-five-songs-per-genre";
    public static final String TOP_K_SONGS_TOPIC = "top-five-songs";
    public static final String PLAY_EVENTS_GENRE_JOIN_TOPIC = "play-events-genre-join";

    //public static final String KAFKA_SERVER = "localhost:29092";
    public static final String KAFKA_SERVER = "localhost:9094";
    public static final String SCHEMA_REGISTRY_SERVER = "http://localhost:8081";
    public static final int RESHUFFLE_COUNT = 100;
    public static final Long MIN_CHARTABLE_DURATION = 30 * 1000L;

    private static final Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(PlaySongsGeneratorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final Map<String, String> serdeConfig = Collections.singletonMap(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_SERVER);
        final SpecificAvroSerializer<PlayEvent> playEventSerializer = new SpecificAvroSerializer<>();
        playEventSerializer.configure(serdeConfig, false);
        final SpecificAvroSerializer<Song> songSerializer = new SpecificAvroSerializer<>();
        songSerializer.configure(serdeConfig, false);

        final List<Song> songs = Arrays.asList(
                new Song(1L,
                        "Fresh Fruit For Rotting Vegetables",
                        "Dead Kennedys",
                        "Chemical Warfare",
                        "Punk"),
                new Song(2L,
                        "We Are the League",
                        "Anti-Nowhere League",
                        "Animal",
                        "Punk"),
                new Song(3L,
                        "Live In A Dive",
                        "Subhumans",
                        "All Gone Dead",
                        "Punk"),
                new Song(4L,
                        "PSI",
                        "Wheres The Pope?",
                        "Fear Of God",
                        "Punk"),
                new Song(5L,
                        "Totally Exploited",
                        "The Exploited",
                        "Punks Not Dead",
                        "Punk"),
                new Song(6L,
                        "The Audacity Of Hype",
                        "Jello Biafra And The Guantanamo School Of "
                                + "Medicine",
                        "Three Strikes",
                        "Punk"),
                new Song(7L,
                        "Licensed to Ill",
                        "The Beastie Boys",
                        "Fight For Your Right",
                        "Hip Hop"),
                new Song(8L,
                        "De La Soul Is Dead",
                        "De La Soul",
                        "Oodles Of O's",
                        "Hip Hop"),
                new Song(9L,
                        "Straight Outta Compton",
                        "N.W.A",
                        "Gangsta Gangsta",
                        "Hip Hop"),
                new Song(10L,
                        "Fear Of A Black Planet",
                        "Public Enemy",
                        "911 Is A Joke",
                        "Hip Hop"),
                new Song(11L,
                        "Curtain Call - The Hits",
                        "Eminem",
                        "Fack",
                        "Hip Hop"),
                new Song(12L,
                        "The Calling",
                        "Hilltop Hoods",
                        "The Calling",
                        "Hip Hop")
        );

        Map<String, Object> props = new HashMap<>();
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_SERVER);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, playEventSerializer.getClass());


        Map<String, Object> props1 = new HashMap<>(props);
        props1.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props1.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, songSerializer.getClass());

        DefaultKafkaProducerFactory<Long, Song> pf1 = new DefaultKafkaProducerFactory<>(props1);
        KafkaTemplate<Long, Song> template1 = new KafkaTemplate<>(pf1, true);
        template1.setDefaultTopic(SONGS_TOPIC);

        songs.forEach(song -> {
            System.out.println("Writing song information for '" + song.getName() + "' to input topic " + SONGS_TOPIC);
            //template1.sendDefault(song.getId(), song);
        });

        DefaultKafkaProducerFactory<String, PlayEvent> pf = new DefaultKafkaProducerFactory<>(props);
        KafkaTemplate<String, PlayEvent> template = new KafkaTemplate<>(pf, true);
        template.setDefaultTopic(PLAY_EVENTS_TOPIC);

        final long duration = MIN_CHARTABLE_DURATION * 3;

        int shuffleCount = 0;
        // send a play event every 100 milliseconds
        int i = 0;
        while (true) {
//            final Song song = songs.get(i);
//            i = (i + 1) % songs.size();
            final Song song = songs.get(random.nextInt(songs.size()));

            shuffleCount = (shuffleCount + 1) % RESHUFFLE_COUNT;
            if (shuffleCount == 0) {
                Collections.shuffle(songs);
                System.out.println("\n <<<<  RESHUFFLE [" + new Date() + "] >>>>");
            }

            System.out.print(song.getId() + ".");
            //System.out.println("Writing play event for song " + song.getName() + " to input topic " + PLAY_EVENTS);
            template.sendDefault(song.getId() + "", new PlayEvent(song.getId(), duration));
            //template.sendDefault("uk", new PlayEvent(song.getId(), duration));
            Thread.sleep(500L);
        }
    }
}

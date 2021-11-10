package net.tzolov.poc.sqlaggregator;

import org.apache.flink.configuration.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamStatementSet;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.time.Duration;

@SpringBootApplication
@EnableConfigurationProperties(SqlAggregatorApplicationProperties.class)
public class SqlAggregatorApplication implements CommandLineRunner {

    @Autowired
    private SqlAggregatorApplicationProperties properties;

    public static void main(String[] args) {
        SpringApplication.run(SqlAggregatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < properties.getExecuteSql().size(); i++) {
            System.out.println("sql[" + i + "] = " + properties.getExecuteSql().get(i));
        }

        Configuration strConf = new Configuration();
        strConf.setInteger(RestOptions.PORT, 8089);
        strConf.setString(RestOptions.BIND_PORT, "8088-8090");
        strConf.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse("1728", MemorySize.MemoryUnit.MEGA_BYTES));
        strConf.set(JobManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse("1600", MemorySize.MemoryUnit.MEGA_BYTES));
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(strConf);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // access flink configuration
        Configuration tableConfiguration = tableEnv.getConfig().getConfiguration();
        tableConfiguration.setString("table.exec.source.idle-timeout", "5 min");
        //tableEnv.getConfig().setIdleStateRetention(Duration.ofMinutes(13));

        for (String sql : properties.getExecuteSql()) {
            tableEnv.executeSql(sql);

        }


//        StreamStatementSet statementSet = tableEnv.createStatementSet();
//
//        statementSet.addInsertSql("INSERT INTO SongPlays " +
//                        "SELECT Plays.song_id, Songs.album, Songs.artist, Songs.name, Songs.genre, Plays.duration, Plays.event_time " +
//                        "FROM (SELECT * FROM PlayEvents WHERE duration >= 30000) AS Plays " +
//                        "INNER JOIN Songs ON Plays.song_id = Songs.id ");
//
//        statementSet.addInsertSql("INSERT INTO TopKSongsPerGenre " +
//                        "SELECT window_start, window_end, song_id, name, genre, play_count  " +
//                        "FROM ( " +
//                        " SELECT *, " +
//                        "    ROW_NUMBER() OVER (PARTITION BY window_start, window_end, genre ORDER BY play_count DESC) AS row_num  " +
//                        " FROM (  " +
//                        "    SELECT window_start, window_end, song_id, name, genre, COUNT(*) AS play_count " +
//                        "    FROM TABLE( " +
//                        "      TUMBLE(TABLE SongPlays, DESCRIPTOR(event_time), INTERVAL '60' SECONDS)) " +
//                        "    GROUP BY window_start, window_end, song_id, name, genre " +
//                        " )" +
//                        ") WHERE row_num <= 3");
//
//        statementSet.execute();

//        tableEnv.sqlQuery("SELECT * from TopKSongsPerGenre").execute().print();

//        tableEnv.sqlQuery("" +
//                "   SELECT window_start, window_end, song_id, name, genre, play_count " +
//                "   FROM (" +
//                "    SELECT *, " +
//                "       ROW_NUMBER() OVER (PARTITION BY window_start, window_end, genre ORDER BY play_count DESC) AS row_num " +
//                "    FROM (" +
//                "       SELECT window_start, window_end, song_id, name, genre, COUNT(*) AS play_count " +
//                "       FROM TABLE( " +
//                "         TUMBLE(TABLE SongPlays, DESCRIPTOR(event_time), INTERVAL '60' SECONDS)) " +
//                "       GROUP BY window_start, window_end, song_id, name, genre " +
//                "    ) " +
//                ") WHERE row_num <= 4 "
//        ).execute().print();

    }
}

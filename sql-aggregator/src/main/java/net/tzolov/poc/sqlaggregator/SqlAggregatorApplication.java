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

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(1, strConf);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Configuration tableConfiguration = tableEnv.getConfig().getConfiguration();
        tableConfiguration.setString("table.exec.source.idle-timeout", "5 min");

        System.out.println("Parallelism: " + env.getConfig().getParallelism());

        Thread.sleep(5000);

        for (String sql : properties.getExecuteSql()) {
            tableEnv.executeSql(sql);
        }

        tableEnv.sqlQuery("SELECT * from TopKSongsPerGenre").execute().print();
    }
}

package net.tzolov.poc.sqlaggregator;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("sql.aggregation")
public class SqlAggregatorApplicationProperties {

    private String kafkaServer;

    private String schemaRegistry;

    private String playEventsTopic;

    private String songsTopic;

    private String outputSqlAggregateTopic;

    private List<String> executeSql;

    public String getKafkaServer() {
        return kafkaServer;
    }

    public void setKafkaServer(String kafkaServer) {
        this.kafkaServer = kafkaServer;
    }

    public String getSchemaRegistry() {
        return schemaRegistry;
    }

    public void setSchemaRegistry(String schemaRegistry) {
        this.schemaRegistry = schemaRegistry;
    }

    public String getPlayEventsTopic() {
        return playEventsTopic;
    }

    public void setPlayEventsTopic(String playEventsTopic) {
        this.playEventsTopic = playEventsTopic;
    }

    public String getSongsTopic() {
        return songsTopic;
    }

    public void setSongsTopic(String songsTopic) {
        this.songsTopic = songsTopic;
    }

    public String getOutputSqlAggregateTopic() {
        return outputSqlAggregateTopic;
    }

    public void setOutputSqlAggregateTopic(String outputSqlAggregateTopic) {
        this.outputSqlAggregateTopic = outputSqlAggregateTopic;
    }

    public List<String> getExecuteSql() {
        return executeSql;
    }

    public void setExecuteSql(List<String> executeSql) {
        this.executeSql = executeSql;
    }
}

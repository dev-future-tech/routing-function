package com.ikeda.pulsar.functions;

import org.apache.pulsar.common.schema.KeyValue;
import org.apache.pulsar.functions.api.Context;
import org.apache.pulsar.functions.api.Function;
import org.slf4j.Logger;

import java.util.stream.Collectors;

public class TransformRouteFunction implements Function<KeyValue<String, String>, Void> {

    private String personDestTopic;

    private String transformPerson;
    private String companyDestTopic;

    @Override
    public void initialize(Context context) throws Exception {
        Logger log = context.getLogger();
        context.getUserConfigValue("PublishPersonTopic").ifPresent(value -> {
            this.personDestTopic = (String) value;
            log.info("Set personDestTopic value to {}", value);
        });

        context.getUserConfigValue("PublishCompanyTopic").ifPresent(value -> {
            this.companyDestTopic = (String) value;
            log.info("Set companyDestTopic value to {}", value);
        });

        context.getUserConfigValue("TransformPerson").ifPresent(value -> {
            this.transformPerson = (String) value;
            log.info("Set transformPerson to {}", value);
        });

        Function.super.initialize(context);
    }

    @Override
    public Void process(KeyValue<String, String> input, Context context) throws Exception {
        Logger log = context.getLogger();

        log.info("Incoming message: {}, {}", input.getKey(), input.getValue());
        String inputTopics = context.getInputTopics().stream().collect(Collectors.joining(", "));
        String functionName = context.getFunctionName();

        String logMessage = String.format(
                "A message with a value of \"%s\" has arrived on one of the following topics: %s\n",
                input,
                inputTopics);

        log.info(logMessage);

        String metricName = String.format("function-%s-messages-received", functionName);
        context.recordMetric(metricName, 1);
        /*
         * if(input.length() > 15) {
         * String fruitTopic = (String)
         * context.getUserConfigValueOrDefault("PublishFruitTopic",
         * "persistent://test/test-namespace/fruit");
         * context.newOutputMessage(fruitTopic, Schema.STRING)
         * .value(String.format("Hello Fruit: %s", input)).send();
         * } else if(input.length() < 10) {
         * String vegetableTopic = (String)
         * context.getUserConfigValueOrDefault("PublishVegetableTopic",
         * "persistent://test/test-namespace/vegetable");
         * context.newOutputMessage(vegetableTopic, Schema.STRING)
         * .value(String.format("Hello Vegetable: %s", input)).send();
         * }
         */

        return null;
    }

}

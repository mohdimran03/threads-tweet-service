package com.example.tweet_service.kafka;

import com.example.tweet_service.kafka.events.NotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Sends a NotificationEvent message to the specified Kafka topic.
     *
     * @param topic           the Kafka topic to send the message to
     * @param notificationEvent the NotificationEvent object to be sent
     */
    public void sendMessage(String topic, NotificationEvent notificationEvent) {
        try {
            // Convert NotificationEvent object to JSON string
            String message = objectMapper.writeValueAsString(notificationEvent);
            kafkaTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            logger.error("error", e);
        }
    }
}
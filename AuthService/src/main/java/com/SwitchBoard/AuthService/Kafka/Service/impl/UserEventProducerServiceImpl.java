package com.SwitchBoard.AuthService.Kafka.Service.impl;

import com.SwitchBoard.AuthService.DTO.USER_ROLE;
import com.SwitchBoard.AuthService.Kafka.Service.UserEventProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.switchboard.schemas.UserCreatedEvent;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducerServiceImpl implements UserEventProducerService {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    @Value("${app.topic.user-created}")
    private String topic;

    @Override
    public void publishUserCreated(String name, String emailId,Map<CharSequence, CharSequence> metadata) {
        log.info("UserEventProducerServiceImpl : publishUserCreated : Publishing user created event for email - {}", emailId);
        
        try {
            UserCreatedEvent event = new UserCreatedEvent(name, emailId, USER_ROLE.USER.toString(), metadata);
            log.debug("UserEventProducerServiceImpl : publishUserCreated : Created UserCreatedEvent with name={}, role={}", name, USER_ROLE.USER);

            kafkaTemplate.send(topic, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("UserEventProducerServiceImpl : publishUserCreated : Successfully published UserCreatedEvent to {} with offset {}",
                                    topic, result.getRecordMetadata().offset());
                        } else {
                            log.error("UserEventProducerServiceImpl : publishUserCreated : Failed to publish UserCreatedEvent: {}", ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("UserEventProducerServiceImpl : publishUserCreated : Error creating or publishing UserCreatedEvent: {}", e.getMessage(), e);
            throw e;
        }
    }
}

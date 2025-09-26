package com.SwitchBoard.AccountService.Kafka.Service;

import com.SwitchBoard.AccountService.Dto.AccountDto;
import com.SwitchBoard.AccountService.Service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.switchboard.schemas.UserCreatedEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserEventConsumerService {

    private final AccountService accountService;

    @KafkaListener(
            topics = "${user.created.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(UserCreatedEvent event, Acknowledgment ack) {
        log.info("UserEventConsumerService : consume : Received user created event - {}", event.getEmailId());
        try {
            log.debug("UserEventConsumerService : consume : Processing event details - Name: {}, Email: {}",
                    event.getName(), event.getEmailId());

            AccountDto accountDto = AccountDto.builder()
                    .name(event.getName().toString())
                    .email(event.getEmailId().toString())
                    .build();

            try {
                accountService.createProfile(accountDto);
                log.info("UserEventConsumerService : consume : Successfully created account - {}", event.getEmailId());
            } catch (IllegalArgumentException e) {
                log.warn("UserEventConsumerService : consume : Failed to create account - {}: {}",
                        event.getEmailId(), e.getMessage());
            }


            ack.acknowledge();

        } catch (Exception e) {
            log.error("UserEventConsumerService : consume : Error processing user created event - {}: {}",
                    event.getEmailId(), e.getMessage(), e);

        }
    }

}


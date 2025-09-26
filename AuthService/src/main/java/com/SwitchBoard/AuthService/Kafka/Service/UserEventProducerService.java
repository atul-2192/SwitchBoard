package com.SwitchBoard.AuthService.Kafka.Service;



import java.util.Map;

public interface UserEventProducerService {
    void publishUserCreated(String name, String emailId, Map<CharSequence, CharSequence> metadata );
}

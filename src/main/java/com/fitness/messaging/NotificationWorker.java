package com.fitness.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationWorker {

    // @RabbitListener continuously polls the queue in a separate background thread
    @RabbitListener(queues = "${messaging.queue}")
    public void handleActivityCreated(ActivityCreatedEvent event) {
        System.out.println("==================================================");
        System.out.println("[BACKGROUND WORKER] - Received Event!");
        System.out
                .println("[BACKGROUND WORKER] - Generating GPS Maps & Notifying Followers for User: " + event.userId());

        try {
            // Simulating a heavy 3-second API call to Firebase/Apple Push servers
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("[BACKGROUND WORKER] - Tasks complete! Notifications sent successfully.");
        System.out.println("==================================================");
    }
}
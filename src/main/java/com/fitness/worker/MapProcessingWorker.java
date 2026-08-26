package com.fitness.worker;

import com.fitness.activity.Activity;
import com.fitness.activity.ActivityRepository;
import com.fitness.activity.StaticMapService;
import com.fitness.storage.ImageStorageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fitness.messaging.ActivityCreatedEvent;

import java.util.UUID;

@Component
public class MapProcessingWorker {

    private final ActivityRepository activityRepository;
    private final StaticMapService staticMapService;
    private final ImageStorageService imageStorageService;

    public MapProcessingWorker(ActivityRepository activityRepository,
            StaticMapService staticMapService,
            ImageStorageService imageStorageService) {
        this.activityRepository = activityRepository;
        this.staticMapService = staticMapService;
        this.imageStorageService = imageStorageService;
    }

    @RabbitListener(queues = "${messaging.map-queue}")
    @Transactional
    public void processNewActivityMap(ActivityCreatedEvent event) {
        String activityIdStr = event.activityId().toString();
        System.out.println("[WORKER] - Picked up activity " + activityIdStr + " for map generation.");

        try {
            UUID activityId = UUID.fromString(activityIdStr);

            System.out.println("[WORKER] - Fetching activity from database...");
            Activity activity = activityRepository.findById(activityId).orElse(null);

            System.out.println("[WORKER] - Activity found: " + activity);

            if (activity != null && activity.getRouteData() != null) {
                // 1. Generate Map
                System.out.println("[WORKER] - Generating map...");

                byte[] mapBytes = staticMapService.generateMapImage(activity.getRouteData());

                System.out.println("[WORKER] - Map bytes length: " + mapBytes.length);

                if (mapBytes.length > 0) { // Ensure we got a real image back
                    // 2. Upload to MinIO
                    System.out.println("[WORKER] - Uploading map to MinIO...");
                    String s3Url = imageStorageService.uploadImageBytes(mapBytes, ".png", "image/png");

                    System.out.println("[WORKER] - Uploaded map to MinIO: " + s3Url);
                    // 3. Update Database
                    activity.setMapImageUrl(s3Url);

                    System.out.println("[WORKER] - Saving activity to database...");
                    activityRepository.save(activity);

                    System.out.println("[WORKER] - Activity updated in DB: " + activity.getId());

                    System.out.println("[WORKER] - Successfully attached map image: " + s3Url);
                }
            }
        } catch (Exception e) {
            System.err.println("[WORKER ERROR] - " + e.getMessage());
        }
    }
}
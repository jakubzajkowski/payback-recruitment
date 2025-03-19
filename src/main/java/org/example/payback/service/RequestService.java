package org.example.payback.service;

import org.example.payback.dto.Request;
import org.example.payback.entity.RequestEntity;
import org.example.payback.repository.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class RequestService {
    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);
    private final BlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    @Value("${request.logFilePath}")
    private String logFilePath;

    @Autowired
    RequestRepository requestRepository;

    public void addRequest(Request request) {
        try {
            requestQueue.put(request);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Failed to add request to the queue", e);
        }
    }

    public void handleRequest(Request request) {
        if (request == null || request.getType() == null) {
            logger.warn("Received a request with null type or content.");
            return;
        }

        switch (request.getType()) {
            case TYPE4:
                logRequest(request);
                break;
            case TYPE2:
                rejectRequest(request);
                break;
            case TYPE3:
                saveToFile(request);
                break;
            case TYPE1:
                saveToDatabase(request);
                break;
            default:
                logger.warn("Unknown request type: {}", request.getType());
        }
    }

    @Async
    public void startRequestProcessor() {
        while (running) {
            try {
                Request request = requestQueue.take();
                handleRequest(request);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Request processing interrupted", e);
                break;
            } catch (Exception e) {
                logger.error("Error while processing request", e);
            }
        }
    }

    public void stopRequestProcessor() {
        running = false;
        logger.info("Stopping request processor...");
    }

    private void logRequest(Request request) {
        logger.info("Log: {}", request.getContent());
    }

    private void rejectRequest(Request request) {
        logger.info("Request Rejected: {}", request.getContent());
    }

    public void saveToFile(Request request) {
        try (FileWriter writer = new FileWriter(logFilePath, true)) {
            writer.write(request.getContent() + "\n");
        } catch (IOException e) {
            logger.error("Error saving request to file: {}", request.getContent(), e);
            throw new RuntimeException("Error saving to file", e);
        }
    }
    public void saveToDatabase(Request request){
        try {
            RequestEntity requestEntity = new RequestEntity(request.getType(),request.getContent());
            requestRepository.save(requestEntity);
            logger.info("Request saved to database: {}", request.getContent());
        }catch (Exception e){
            logger.error("Error saving request to database: {}", request.getContent(), e);
            throw new RuntimeException("Error saving to database", e);
        }
    }

    public BlockingQueue<Request> getRequestQueue() {
        return requestQueue;
    }

    public boolean isRunning() {
        return running;
    }
}

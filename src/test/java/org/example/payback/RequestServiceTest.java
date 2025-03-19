package org.example.payback;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.example.payback.dto.Request;
import org.example.payback.dto.RequestType;
import org.example.payback.entity.RequestEntity;
import org.example.payback.repository.RequestRepository;
import org.example.payback.service.RequestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;


class RequestServiceTest {

    private final String testFilePath = "./test-log.txt";

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(requestService, "logFilePath", testFilePath);
    }

    @AfterEach
    void tearDown() {
        new File(testFilePath).delete();
    }

    @Test
    void testAddRequest() {
        Request request = new Request(RequestType.TYPE1, "Test content");
        requestService.addRequest(request);
        assertFalse(requestService.getRequestQueue().isEmpty());
    }

    @Test
    void testHandleRequest_Type1_SaveToDatabase() {
        Request request = new Request(RequestType.TYPE1, "Save to DB");
        requestService.handleRequest(request);
        verify(requestRepository, times(1)).save(any(RequestEntity.class));
    }

    @Test
    void testHandleRequest_Type2_RejectRequest() {
        Request request = new Request(RequestType.TYPE2, "Reject me");
        requestService.handleRequest(request);
    }

    @Test
    void testHandleRequest_Type3_SaveToFile() throws IOException {
        Request request = new Request(RequestType.TYPE3, "Write to file");
        requestService.handleRequest(request);

        File file = new File(testFilePath);
        assertTrue(file.exists());

        try (var reader = new java.util.Scanner(file)) {
            assertTrue(reader.hasNextLine());
            assertEquals("Write to file", reader.nextLine());
        }
    }

    @Test
    void testHandleRequest_Type4_LogRequest() {
        Request request = new Request(RequestType.TYPE4, "Log this");
        requestService.handleRequest(request);
    }

    @Test
    void testStopRequestProcessor() {
        requestService.stopRequestProcessor();
        assertFalse(requestService.isRunning());
    }

    @Test
    void testSaveToFile_ExceptionHandling() {
        Request request = new Request(RequestType.TYPE3, "Cause Exception");
        ReflectionTestUtils.setField(requestService, "logFilePath", "/invalid/path/to/file.txt");
        assertThrows(RuntimeException.class, () -> requestService.saveToFile(request));
    }

    @Test
    void testSaveToDatabase_ExceptionHandling() {
        Request request = new Request(RequestType.TYPE1, "Cause DB Error");
        doThrow(new RuntimeException("DB Error")).when(requestRepository).save(any(RequestEntity.class));
        assertThrows(RuntimeException.class, () -> requestService.saveToDatabase(request));
    }
}
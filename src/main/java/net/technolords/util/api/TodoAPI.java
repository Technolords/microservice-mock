package net.technolords.util.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoAPI.class);
    public static final String END_POINT = "/api/v1/todo";

    @GetMapping(value = END_POINT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity todo() {
        try {
            LOGGER.info("About to get hello world...");
            return ResponseEntity.ok().body("{\"text\": \"hello world\"}");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

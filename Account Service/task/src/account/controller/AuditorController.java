package account.controller;

import account.dao.SecurityLoggerRepository;
import account.entity.SecurityLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/api/security")
public class AuditorController {

    private final SecurityLoggerRepository securityLoggerRepository;

    @Autowired
    public AuditorController(SecurityLoggerRepository securityLoggerRepository) {
        this.securityLoggerRepository = securityLoggerRepository;
    }

    @GetMapping("/events")
    public ResponseEntity<Object> getEvents() {
        List<SecurityLogs> eventsList = securityLoggerRepository.findAllByOrderByIdAsc();
        return new ResponseEntity<>(Objects.requireNonNullElse(eventsList, "[]"), HttpStatus.OK);
    }
}
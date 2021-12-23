package account.service;

import account.dao.SecurityLoggerRepository;
import account.entity.SecurityLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoggerService {

    private final SecurityLoggerRepository logger;

    @Autowired
    public LoggerService(SecurityLoggerRepository logger) {
        this.logger = logger;
    }

    public List<SecurityLogs> getEvents() {
        return logger.findAllByOrderByIdAsc();
    }

    public void saveEvent(SecurityLogs securityLogs) {
        logger.save(securityLogs);
    }
}

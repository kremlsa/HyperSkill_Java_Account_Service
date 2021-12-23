package account.dao;


import account.entity.SecurityLogs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecurityLoggerRepository extends JpaRepository<SecurityLogs, Long> {
    List<SecurityLogs> findAllByOrderByIdAsc();

}
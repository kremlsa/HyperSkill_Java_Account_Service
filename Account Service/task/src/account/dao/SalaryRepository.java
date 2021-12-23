package account.dao;

import account.entity.Salaries;
import account.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salaries, Long> {
    Optional<Salaries> findAllByUsername(String username);
    Optional<Salaries> findByPeriod(LocalDate period);
    Optional<Salaries> findByUsernameAndPeriod(String username, LocalDate period);
    Optional<Salaries> findById(Long id);
    Optional<List<Salaries>> findAllByUsernameIgnoreCaseOrderByPeriodDesc(String username);
    Optional<List<Salaries>> findAllByUsernameIgnoreCaseOrderByPeriodAsc(String username);
}
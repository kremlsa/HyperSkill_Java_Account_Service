package account.service;

import account.exceptions.DefaultException;
import account.dao.SalaryRepository;
import account.entity.Salaries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalaryService {
    private final SalaryRepository salaryRepository;

    @Autowired
    public SalaryService(SalaryRepository salaryRepository) {
        this.salaryRepository = salaryRepository;
    }

    public List<Salaries> findAllByEmployee(String username) throws UsernameNotFoundException {
        return salaryRepository.findAllByUsernameIgnoreCaseOrderByPeriodDesc(username)
                .orElse(new ArrayList<>());
    }



    public Salaries findByPeriod(LocalDate period) throws UsernameNotFoundException{
        return salaryRepository.findByPeriod(period)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("No payments %s found for period ", period)));
    }

    public Salaries findByUsernameAndPeriod(String username, LocalDate period) {
        return salaryRepository.findByUsernameAndPeriod(username, period)
                .orElse(null);
    }

    @Transactional
    public void addPayment(List<Salaries> salaries) {
        for (Salaries s : salaries)
            try {
                salaryRepository.save(s);
            } catch (DataIntegrityViolationException e) {
                throw new DefaultException();
            }
    }
}
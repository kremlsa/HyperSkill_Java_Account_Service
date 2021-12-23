package account.controller;

import account.dto.SalaryDTO;
import account.entity.Salaries;
import account.exceptions.DefaultException;
import account.payload.DefaultResponse;
import account.service.SalaryService;
import account.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequestMapping("/api")
public class SalaryController {

    private final SalaryService salaryService;
    private final UserServiceImpl userService;

    @Autowired
    public SalaryController(SalaryService salaryService, UserServiceImpl service) {
        this.salaryService = salaryService;
        this.userService = service;
    }

    @PostMapping(path = "/acct/payments")
    public ResponseEntity<DefaultResponse> addPayments(@RequestBody List<@Valid SalaryDTO> payments) {

        List<Salaries> salaries = new ArrayList<>();

        for (SalaryDTO payment : payments) {
            Salaries salary = new Salaries();
            salary.setSalary(payment.getSalary());
            salary.setUsername(payment.getEmployee().toLowerCase());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
            YearMonth ym = YearMonth.parse(payment.getPeriod(), formatter);
            LocalDate date = ym.atEndOfMonth();
            salary.setPeriod(date);
            userService.findByUserName(payment.getEmployee().toLowerCase());
            if (salaryService.findByUsernameAndPeriod(payment.getEmployee().toLowerCase(), date) != null) {
                throw new DefaultException();
            }
            salaries.add(salary);
        }
        salaryService.addPayment(salaries);
        return new ResponseEntity<>(new DefaultResponse("Successfully added!"), HttpStatus.OK);
    }

    @PutMapping(path = "/acct/payments", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<DefaultResponse> updatePayments(@RequestBody @Valid SalaryDTO payment) {
        try {
            List<Salaries> salaries = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
            YearMonth ym = YearMonth.parse(payment.getPeriod(), formatter);
            LocalDate date = ym.atEndOfMonth();
            Salaries salary = salaryService.findByUsernameAndPeriod(payment.getEmployee().toLowerCase(), date);
            salary.setSalary(payment.getSalary());
            salaries.add(salary);
            salaryService.addPayment(salaries);
            return new ResponseEntity<>(new DefaultResponse("Successfully updated!"), HttpStatus.OK);
        } catch (Exception e) {
            throw new DefaultException();
        }
    }
}
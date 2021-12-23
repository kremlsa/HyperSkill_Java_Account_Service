package account.controller;

import account.entity.Salaries;
import account.entity.User;
import account.exceptions.DefaultException;
import account.payload.PaymentResponse;
import account.service.SalaryService;
import account.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class EmplController {

    private final UserServiceImpl userService;
    private final SalaryService salaryService;

    @Autowired
    public EmplController(SalaryService salaryService, UserServiceImpl service) {
        this.salaryService = salaryService;
        this.userService = service;
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<Object> getEmployee(@Autowired Principal principal,
                                              @RequestParam(required = false) Map<String,String> params) {
        User user = userService.findByUserName(principal.getName().toLowerCase());
        if (params.size() == 0) {
            List<Salaries> salariesList = salaryService.findAllByEmployee(principal.getName().toLowerCase());
            List<PaymentResponse> paymentResponseList = new ArrayList<>();
            for (Salaries sal : salariesList) {
                paymentResponseList.add(new PaymentResponse(user.getName(), user.getLastname(),
                        sal.getPeriod(), sal.getSalary()));
            }
            return new ResponseEntity<>(Objects.requireNonNullElse(paymentResponseList, "[]"), HttpStatus.OK);
        } else if (params.size() == 1) {
            Map.Entry<String,String> entry = params.entrySet().iterator().next();
            if (!entry.getKey().equalsIgnoreCase("period")) {
                throw new DefaultException();
            }
            String value = entry.getValue();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
            try {
                YearMonth ym = YearMonth.parse(value, formatter);
                LocalDate date = ym.atEndOfMonth();
                Salaries salaries = salaryService.findByUsernameAndPeriod(principal.getName().toLowerCase(),
                        date);
                PaymentResponse paymentResponse = new PaymentResponse(user.getName(), user.getLastname(),
                        date, salaries.getSalary());
                return new ResponseEntity<>(Objects.requireNonNullElse(paymentResponse, "{}"), HttpStatus.OK);
            } catch (Exception e) {
                throw new DefaultException();
            }
        } else {
            throw new DefaultException();
        }
    }
}
package account.payload;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PaymentResponse {
    String name;
    String lastname;
    String email;
    String period;
    String salary;

    public PaymentResponse() {
    }

    public PaymentResponse(String name, String lastname, LocalDate period, Long salaryLong) {
        this.name = name;
        this.lastname = lastname;
        this.period = convertPeriod(period);
        this.salary = convertSalary(salaryLong);
    }

    private String convertPeriod(LocalDate period) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-yyyy", new Locale("en"));
        return period.format(formatter);
    }

    private String convertSalary(Long salaryLong) {
        String dollars = String.valueOf(salaryLong / 100L);
        String cents = String.valueOf(salaryLong % 100L);
        return dollars + " dollar(s) " + cents + " cent(s)";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
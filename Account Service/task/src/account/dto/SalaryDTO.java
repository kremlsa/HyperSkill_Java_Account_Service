package account.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class SalaryDTO {
    private Long id;
    @NotEmpty
    @Pattern(regexp="^(1[0-2]|0[1-9])-\\d{4}$", message = "Wrong date!")
    private String period;
    @NotEmpty
    private String employee;
    @Min(value = 0, message = "Salary must be non negative!")
    private Long salary;

    public SalaryDTO() {
    }

    public SalaryDTO(Long id, String period, String username, Long salary) {
        this.id = id;
        this.period = period;
        this.employee = username;
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
}
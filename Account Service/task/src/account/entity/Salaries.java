package account.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "salaries", uniqueConstraints = @UniqueConstraint(columnNames = {"period", "username"}))
public class Salaries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="period")
    private LocalDate period;
    @Column(name="username", nullable = false)
    private String username;
    @Column(name="salary", nullable = false)
    private Long salary;

    public Salaries() {
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate date) {
        this.period = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
}
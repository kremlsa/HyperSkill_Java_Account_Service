package account.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SECURITY_LOGS")
public class SecurityLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name="DATE")
    private LocalDateTime date = LocalDateTime.now();
    @Column(name = "ACTION")
    private String action;
    @Column(name = "SUBJECT")
    private String subject;
    @Column(name = "OBJECT")
    private String object;
    @Column(name = "PATH")
    private String path;


    public SecurityLogs() {
    }

    public SecurityLogs(String action, String subject, String object, String path) {
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
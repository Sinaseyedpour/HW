package ir.maktabsharif.smspanel.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A registered user of the SMS panel.
 */
public class User {

    private long id;
    private String name;
    private String username;
    private String password;
    private BigDecimal credit;
    private LocalDateTime registrationDate;
    private UserStatus status;

    public User() {
    }

    public User(long id, String name, String username, String password,
                BigDecimal credit, LocalDateTime registrationDate, UserStatus status) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.credit = credit;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * @return {@code true} when the account is allowed to log in.
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}

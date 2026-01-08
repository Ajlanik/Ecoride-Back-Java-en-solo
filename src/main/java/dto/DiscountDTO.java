package dto;

import java.io.Serializable;
import java.util.Date;

public class DiscountDTO implements Serializable {

    private Integer id;
    private String code;
    private int percentage;
    private String description;
    private Date expiresAt;

    public DiscountDTO() {
    }

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getPercentage() { return percentage; }
    public void setPercentage(int percentage) { this.percentage = percentage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
}
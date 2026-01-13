/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author ajlan
 */
import java.io.Serializable;

public class UserStatsDTO implements Serializable {

    private Double rating;
    private Integer credits;
    private Double co2Saved;
    private Integer ridesCount;

    public UserStatsDTO() {
    }

    public UserStatsDTO(Double rating, Integer credits, Double co2Saved, Integer ridesCount) {
        this.rating = rating;
        this.credits = credits;
        this.co2Saved = co2Saved;
        this.ridesCount = ridesCount;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Double getCo2Saved() {
        return co2Saved;
    }

    public void setCo2Saved(Double co2Saved) {
        this.co2Saved = co2Saved;
    }

    public Integer getRidesCount() {
        return ridesCount;
    }

    public void setRidesCount(Integer ridesCount) {
        this.ridesCount = ridesCount;
    }
}

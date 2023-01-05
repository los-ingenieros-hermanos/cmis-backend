package com.los.cmisbackend.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="date")
public class Date implements Comparable<Date> {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="day")
    private Integer year = Integer.valueOf(2023);

    @Column(name="month")
    private Integer month = Integer.valueOf(1);

    @Column(name="year")
    private Integer day = Integer.valueOf(1);

    @Column(name="hour")
    private Integer hour = Integer.valueOf(0);

    @Column(name="minute")
    private Integer minute = Integer.valueOf(0);

    public Date() {

    }

    public Date(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return Objects.equals(id, date.id) && Objects.equals(year, date.year) && Objects.equals(month, date.month) && Objects.equals(day, date.day) && Objects.equals(hour, date.hour) && Objects.equals(minute, date.minute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, year, month, day, hour, minute);
    }

    // add compareTo method
    @Override
    public int compareTo(Date date) {
        if (this.year > date.year) {
            return 1;
        } else if (this.year < date.year) {
            return -1;
        } else {
            if (this.month > date.month) {
                return 1;
            } else if (this.month < date.month) {
                return -1;
            } else {
                if (this.day > date.day) {
                    return 1;
                } else if (this.day < date.day) {
                    return -1;
                } else {
                    if (this.hour > date.hour) {
                        return 1;
                    } else if (this.hour < date.hour) {
                        return -1;
                    } else {
                        if (this.minute > date.minute) {
                            return 1;
                        } else if (this.minute < date.minute) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        }
    }
}

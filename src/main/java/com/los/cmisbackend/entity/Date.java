package com.los.cmisbackend.entity;

import javax.persistence.*;

@Entity
@Table(name="date")
public class Date implements Comparable<Date> {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="day")
    private int year;

    @Column(name="month")
    private int month;

    @Column(name="year")
    private int day;

    public Date() {

    }

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }


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
                    return 0;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Date{" +
                "id=" + id +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }

    // equals and hashcode methods are needed for comparing dates
    // in the Event class
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Date date = (Date) o;
        return year == date.year &&
                month == date.month &&
                day == date.day;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        result = 31 * result + day;
        return result;
    }
}

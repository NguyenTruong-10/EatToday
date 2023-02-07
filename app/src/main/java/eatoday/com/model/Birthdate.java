package eatoday.com.model;

public class Birthdate {
    private String day;
    private String month;
    private String year;

    public Birthdate() {
    }

    public Birthdate(String day, String month, String year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }
}

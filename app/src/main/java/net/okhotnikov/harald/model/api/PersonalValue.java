package net.okhotnikov.harald.model.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

import static net.okhotnikov.harald.model.processing.HartData.SIMPLE_DATE_FORMAT;

public class PersonalValue <T> {
    public String person;
    public T value;
    public Date date;

    public PersonalValue() {
    }

    public PersonalValue(String person, T value) {
        this.person = person;
        this.value = value;
        date = new Date();
    }

    public PersonalValue(String person, T value, Date date) {
        this.person = person;
        this.value = value;
        this.date = date;
    }

    public String getTime(){
        return SIMPLE_DATE_FORMAT.format(date);
    }

    @JsonIgnore
    public Date getDate() {
        return date;
    }

    @JsonIgnore
    public void setDate(Date date) {
        this.date = date;
    }
}

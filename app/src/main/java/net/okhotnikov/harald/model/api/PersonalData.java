package net.okhotnikov.harald.model.api;

import java.util.List;

public class PersonalData <T> {
    public String person;
    public List<T> data;

    public PersonalData() {
    }

    public PersonalData(String person, List<T> data) {
        this.person = person;
        this.data = data;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}

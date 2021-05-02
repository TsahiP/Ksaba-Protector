package com.example.ksabaprotector;

public class workers {
    private String name;
    private String car;
    private String email;
    private String inwork;

    public workers() {
    }

    public workers(String name, String car, String email,String inwork) {
        this.name = name;
        this.car = car;
        this.email = email;
        this.inwork = inwork;
    }
    public workers(workers wk) {
        this.name = wk.name;
        this.car = wk.car;
        this.email = wk.email;
        this.inwork = wk.inwork;
    }

    public String getInwork() {
        return inwork;
    }

    public void setInwork(String inwork) {
        this.inwork = inwork;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return "workers{" +
                "name='" + name + '\'' +
                ", car='" + car + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}


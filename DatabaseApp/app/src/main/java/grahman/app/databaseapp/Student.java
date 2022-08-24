package grahman.app.databaseapp;

public class Student {
    int id;
    String name, dept, enrolDate;
    double mark;

    public Student(int id, String name, String dept, String enrolDate, double mark) {
        this.id = id;
        this.name = name;
        this.dept = dept;
        this.enrolDate = enrolDate;
        this.mark = mark;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDept() {
        return dept;
    }

    public String getEnrolDate() {
        return enrolDate;
    }

    public double getMark() {
        return mark;
    }
}

package pojo;

import java.util.ArrayList;
import java.util.List;

public class Grade {
    private int id ;
    private List<Student> studentList = new ArrayList<Student>();

    public Grade(int id, List<Student> studentList) {
        this.id = id;
        this.studentList = studentList;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

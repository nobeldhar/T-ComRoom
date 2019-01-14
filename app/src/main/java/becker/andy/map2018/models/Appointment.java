package becker.andy.map2018.models;

import com.google.gson.annotations.SerializedName;

public class Appointment {

    @SerializedName("student_id")
    private int Student_id;
    @SerializedName("teacher_id")
    private int Teacher_id;
    @SerializedName("name")
    private String StudentName;
    @SerializedName("teacher_name")
    private String TeacherName;
    @SerializedName("reg")
    private String Reg;
    @SerializedName("department")
    private String Department;
    @SerializedName("year_semester")
    private String Year_Semester;
    @SerializedName("institution")
    private String Institution;
    @SerializedName("phone")
    private String Phone;
    @SerializedName("subject")
    private String Subject;
    @SerializedName("description")
    private String Description;
    @SerializedName("message")
    private String Message;
    @SerializedName("date")
    private String Date;
    @SerializedName("time")
    private String Time;

    public String getTeacherName() {
        return TeacherName;
    }

    public void setTeacherName(String teacherName) {
        TeacherName = teacherName;
    }

    public int getTeacher_id() {
        return Teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        Teacher_id = teacher_id;
    }

    public int getStudent_id() {
        return Student_id;
    }

    public void setStudent_id(int student_id) {
        Student_id = student_id;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getReg() {
        return Reg;
    }

    public void setReg(String reg) {
        Reg = reg;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getYear_Semester() {
        return Year_Semester;
    }

    public void setYear_Semester(String year_Semester) {
        Year_Semester = year_Semester;
    }

    public String getInstitution() {
        return Institution;
    }

    public void setInstitution(String institution) {
        Institution = institution;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}

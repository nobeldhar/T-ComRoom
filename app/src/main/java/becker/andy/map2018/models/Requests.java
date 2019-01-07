package becker.andy.map2018.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Requests implements Parcelable {

    @SerializedName("student_id")
    private int Student_id;
    @SerializedName("name")
    private String StudentName;
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

    protected Requests(Parcel in) {
        Student_id = in.readInt();
        StudentName = in.readString();
        Reg = in.readString();
        Department = in.readString();
        Year_Semester = in.readString();
        Institution = in.readString();
        Phone = in.readString();
        Subject = in.readString();
        Description = in.readString();
    }

    public static final Creator<Requests> CREATOR = new Creator<Requests>() {
        @Override
        public Requests createFromParcel(Parcel in) {
            return new Requests(in);
        }

        @Override
        public Requests[] newArray(int size) {
            return new Requests[size];
        }
    };

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

    @Override
    public String toString() {
        return "Reg='" + Reg + '\'' +
                ", Dept='" + Department + '\'' +
                ", Year/Sem='" + Year_Semester + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Student_id);
        dest.writeString(StudentName);
        dest.writeString(Reg);
        dest.writeString(Department);
        dest.writeString(Year_Semester);
        dest.writeString(Institution);
        dest.writeString(Phone);
        dest.writeString(Subject);
        dest.writeString(Description);
    }
}

package becker.andy.map2018.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Teacher implements Parcelable {

    @SerializedName("teacher_id")
    private int Teacher_id;
    @SerializedName("name")
    private String TeacherName;
    @SerializedName("department")
    private String Department;
    @SerializedName("institution")
    private String Institution;
    @SerializedName("phone")
    private String Phone;

    protected Teacher(Parcel in) {
        Teacher_id = in.readInt();
        TeacherName = in.readString();
        Department = in.readString();
        Institution = in.readString();
        Phone = in.readString();
    }

    public static final Creator<Teacher> CREATOR = new Creator<Teacher>() {
        @Override
        public Teacher createFromParcel(Parcel in) {
            return new Teacher(in);
        }

        @Override
        public Teacher[] newArray(int size) {
            return new Teacher[size];
        }
    };

    public int getTeacher_id() {
        return Teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        Teacher_id = teacher_id;
    }

    public String getTeacherName() {
        return TeacherName;
    }

    public void setTeacherName(String teacherName) {
        TeacherName = teacherName;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Teacher_id);
        dest.writeString(TeacherName);
        dest.writeString(Department);
        dest.writeString(Institution);
        dest.writeString(Phone);
    }

    @Override
    public String toString() {
        return
                "Department='" + Department + '\'' +
                ", Institution='" + Institution;
    }
}

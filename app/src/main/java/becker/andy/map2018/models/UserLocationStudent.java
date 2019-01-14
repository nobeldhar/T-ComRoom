package becker.andy.map2018.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocationStudent implements Parcelable {

    private GeoPoint geo_point;
    private @ServerTimestamp Date timestamp;
    private Teacher teacher;
    public UserLocationStudent(GeoPoint geo_point, Date timestamp, Teacher teacher) {
        this.geo_point = geo_point;
        this.timestamp = timestamp;
        this.teacher = teacher;
    }

    public UserLocationStudent() {

    }

    protected UserLocationStudent(Parcel in) {
        teacher = in.readParcelable(Teacher.class.getClassLoader());
    }

    public static final Creator<UserLocationStudent> CREATOR = new Creator<UserLocationStudent>() {
        @Override
        public UserLocationStudent createFromParcel(Parcel in) {
            return new UserLocationStudent(in);
        }

        @Override
        public UserLocationStudent[] newArray(int size) {
            return new UserLocationStudent[size];
        }
    };

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(teacher, flags);
    }
}

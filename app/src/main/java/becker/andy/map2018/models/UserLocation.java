package becker.andy.map2018.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class UserLocation implements Parcelable {

    private GeoPoint geo_point;
    private @ServerTimestamp Date timestamp;
    private Requests requests;


    public UserLocation(GeoPoint geo_point, Date timestamp, Requests requests) {
        this.geo_point = geo_point;
        this.timestamp = timestamp;
        this.requests = requests;
    }
    public UserLocation() {

    }

    protected UserLocation(Parcel in) {
    }

    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            return new UserLocation(in);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
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

    public Requests getRequests() {
        return requests;
    }

    public void setRequests(Requests requests) {
        this.requests = requests;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}

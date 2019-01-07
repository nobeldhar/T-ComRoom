package becker.andy.map2018.retrofit;

import java.util.List;

import becker.andy.map2018.models.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("login.php")
    Call<User> performLogin(@Query("type") String type, @Query("admin_email") String email, @Query("admin_password") String password);

    @GET ("register_stu.php")
    Call<User> performRegisterStudent (@Query("email") String email,@Query("password") String password,@Query("name") String name,
                                      @Query("institution") String institution,@Query("department") String department,@Query("reg") String reg,@Query("year_semester") String year_semester,@Query("phone") String phone);
    @GET ("register_teach.php")
    Call<User> performRegisterTeacher (@Query("email") String email,@Query("password") String password,@Query("name") String name,
                                       @Query("institution") String institution,@Query("department") String department,@Query("phone") String phone);

    @GET("reperterRerports.php")
    Call<List<User>> getPendingDrivers(@Query("task") String task);

}

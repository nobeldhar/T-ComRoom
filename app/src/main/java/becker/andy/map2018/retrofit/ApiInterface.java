package becker.andy.map2018.retrofit;

import java.util.List;

import becker.andy.map2018.models.Appointment;
import becker.andy.map2018.models.Requests;
import becker.andy.map2018.models.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("login.php")
    Call<User> performLogin(@Query("email") String email, @Query("password") String password);

    @GET("set_appointment.php")
    Call<User> setAppointment(@Query("teacher_id") int teacher_id,@Query("student_id") int student_id,
                              @Query("subject") String subject,@Query("description") String description,
                              @Query("message") String message,@Query("date") String date, @Query("time") String time);

    @GET ("register_stu.php")
    Call<User> performRegisterStudent (@Query("email") String email,@Query("password") String password,@Query("name") String name,
                                      @Query("institution") String institution,@Query("department") String department,@Query("reg") String reg,@Query("year_semester") String year_semester,@Query("phone") String phone);
    @GET ("register_teach.php")
    Call<User> performRegisterTeacher (@Query("email") String email,@Query("password") String password,@Query("name") String name,
                                       @Query("institution") String institution,@Query("department") String department,@Query("phone") String phone);

    @GET("get_requests.php")
    Call<List<Requests>> getRequests(@Query("user_id") int user_id);

    @GET("get_appointments.php")
    Call<List<Appointment>> getAppointments(@Query("user_id") int user_id);

    @GET("delete_appointment.php")
    Call<User> deleteAppointment(@Query("teacher_id") int teacher_id,@Query("student_id") int student_id);

}

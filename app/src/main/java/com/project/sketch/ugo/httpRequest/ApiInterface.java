package com.project.sketch.ugo.httpRequest;

import com.project.sketch.ugo.httpRequest.apiModel.ForgotPassResponce;
import com.project.sketch.ugo.httpRequest.apiModel.LoginResponce;
import com.project.sketch.ugo.httpRequest.apiModel.OtpVerifyResponce;
import com.project.sketch.ugo.httpRequest.apiModel.RegistrationResponce;
import com.project.sketch.ugo.httpRequest.apiModel.ResetPassResponce;
import com.project.sketch.ugo.httpRequest.apiModel2.CabList;
import com.project.sketch.ugo.httpRequest.apiModel2.ConfirmBookResponce;
import com.project.sketch.ugo.httpRequest.apiModel2.DriverResponce;
import com.project.sketch.ugo.httpRequest.apiModel2.EstimateValueResponce;
import com.project.sketch.ugo.httpRequest.apiModel2.OutstationEstimateResponse;
import com.project.sketch.ugo.httpRequest.apiModel2.SpecialFareEstimateResponse;
import com.project.sketch.ugo.httpRequest.apiModel3.BookingHistoryAll;
import com.project.sketch.ugo.httpRequest.apiModel4.CancelBooking;
import com.project.sketch.ugo.httpRequest.apiModel4.FeedbackRatingResponse;
import com.project.sketch.ugo.httpRequest.apiModel5.RentalRateList;
import com.project.sketch.ugo.httpRequest.apiModel6.CouponModel;
import com.project.sketch.ugo.httpRequest.apiModel6.ResendOtpResponse;
import com.project.sketch.ugo.httpRequest.apiModel7.CabDetailsResponse;
import com.project.sketch.ugo.httpRequest.apiModel7.SpecialFareResponse;
import com.project.sketch.ugo.httpRequest.apiModel8.FareDetails;
import com.project.sketch.ugo.utils.Constants;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Developer on 1/22/18.
 */

public interface ApiInterface {



    @FormUrlEncoded
    @POST(Constants.registration)
    Call<RegistrationResponce> userRegistration(@Field(Constants.name) String name,
                                                @Field(Constants.email) String email,
                                                @Field(Constants.phone) String phone,
                                                @Field(Constants.password) String password,
                                                @Field(Constants.device_type) String device_type,
                                                @Field(Constants.device_id) String device_id,
                                                @Field(Constants.fcm_reg_token) String fcm_reg_token);



    @FormUrlEncoded
    @POST(Constants.verifyotp)
    Call<OtpVerifyResponce> verify_otp(@Field(Constants.phone) String phone,
                                       @Field(Constants.otp) String otp);



    @FormUrlEncoded
    @POST(Constants.login)
    Call<LoginResponce> login(@Field(Constants.phone) String phone,
                              @Field(Constants.password) String password,
                              @Field(Constants.device_type) String device_type,
                              @Field(Constants.device_id) String device_id,
                              @Field(Constants.fcm_reg_token) String fcm_reg_token);



    @FormUrlEncoded
    @POST(Constants.forgetpass)
    Call<ForgotPassResponce> forgotPassword(@Field(Constants.phone) String phone);



    @FormUrlEncoded
    @POST(Constants.resetpass)
    Call<ResetPassResponce> resetPassword(@Field(Constants.id) String id,
                                          @Field(Constants.password) String password);



    @FormUrlEncoded
    @POST(Constants.changepassword)
    Call<ResetPassResponce> changePassword(@Field(Constants.id) String id,
                                           @Field(Constants.oldpassword) String oldpassword,
                                           @Field(Constants.password) String password);



    @Multipart
    @POST(Constants.updateprofile)
    Call<LoginResponce> updateProfile(@Part(Constants.id) String id,
                                      @Part(Constants.name) String name,
                                      @Part(Constants.email) String email,
                                      @Part(Constants.phone) String phone,
                                      @Part MultipartBody.Part file);



    @FormUrlEncoded
    @POST(Constants.getnearestdriver)
    Call<CabList> getNearestDriver(@Field(Constants.lat) String lat,
                                   @Field(Constants.lng) String lng);



    @FormUrlEncoded
    @POST(Constants.confirmbooking)
    Call<ConfirmBookResponce> confirmBooking(@Field(Constants.id) String id,
                                             @Field(Constants.booking_type) String booking_type,
                                             @Field(Constants.outstation_type) String outstation_type,
                                             @Field(Constants.pickup_address) String pickup_address,
                                             @Field(Constants.pickup_latitude) double pickup_latitude,
                                             @Field(Constants.pickup_longitude) double pickup_longitude,
                                             @Field(Constants.drop_address) String drop_address,
                                             @Field(Constants.drop_latitude) double drop_latitude,
                                             @Field(Constants.drop_longitude) double drop_longitude,
                                             @Field(Constants.cab_variant_id) String cab_variant_id,
                                             @Field(Constants.payment_mode) String payment_mode,
                                             @Field(Constants.guide_option) String guide_option,
                                             @Field(Constants.scheduled_date) String scheduled_date,
                                             @Field(Constants.scheduled_time) String scheduled_time,
                                             @Field(Constants.later) String later,
                                             @Field(Constants.outstation_trip_starttime) String outstation_trip_starttime,
                                             @Field(Constants.rental_distance_package) String rental_distance_package,
                                             @Field(Constants.coupon_applied) String coupon_applied,
                                             @Field(Constants.coupon_code) String coupon_code,
                                             @Field(Constants.coupon_amount) float coupon_amount);



    @FormUrlEncoded
    @POST(Constants.get_booking_history)
    Call<BookingHistoryAll> getBookingHistory(@Field(Constants.id) String id);



    @FormUrlEncoded
    @POST(Constants.get_driver_location)
    Call<DriverResponce> getDriverLocation(@Field(Constants.id) String id);



    @FormUrlEncoded
    @POST(Constants.estimate_fare)
    Call<EstimateValueResponce> getEstimateCost(@Field(Constants.pickup_latitude) double pickup_latitude,
                                                @Field(Constants.pickup_longitude) double pickup_longitude,
                                                @Field(Constants.drop_latitude) double drop_latitude,
                                                @Field(Constants.drop_longitude) double drop_longitude,
                                                @Field(Constants.cab_variant_id) String cab_variant_id);



    @FormUrlEncoded
    @POST(Constants.cancel_booking)
    Call<CancelBooking> cancelBooking(@Field(Constants.id) String id,
                                        @Field(Constants.booking_id) String booking_id,
                                        @Field(Constants.cancel_reason) String cancel_reason);



    @FormUrlEncoded
    @POST(Constants.customer_feedback)
    Call<FeedbackRatingResponse> feedbackRating(@Field(Constants.id) String id,
                                                @Field(Constants.booking_id) String booking_id,
                                                @Field(Constants.rate) String rate,
                                                @Field(Constants.comment) String comment);



    @FormUrlEncoded
    @POST(Constants.help)
    Call<CancelBooking> helpMail(@Field(Constants.id) String id,
                                 @Field(Constants.booking_id) String booking_id,
                                 @Field(Constants.subject) String subject);



    @FormUrlEncoded
    @POST(Constants.bigform)
    Call<CancelBooking> bigform(@Field(Constants.name) String name,
                                 @Field(Constants.email) String email,
                                 @Field(Constants.mobile) String mobile,
                                 @Field(Constants.message) String message);



    @FormUrlEncoded
    @POST(Constants.rental_fare_estimate)
    Call<RentalRateList> rental_fare(@Field(Constants.pickup_latitude) double pickup_latitude,
                                 @Field(Constants.pickup_longitude) double pickup_longitude,
                                 @Field(Constants.cab_variant_id) String cab_variant_id);



    @FormUrlEncoded
    @POST(Constants.outstation_estimate_fare)
    Call<OutstationEstimateResponse> getOutstationEstimateCost(@Field(Constants.pickup_latitude) double pickup_latitude,
                                                               @Field(Constants.pickup_longitude) double pickup_longitude,
                                                               @Field(Constants.drop_latitude) double drop_latitude,
                                                               @Field(Constants.drop_longitude) double drop_longitude,
                                                               @Field(Constants.cab_variant_id) String cab_variant_id);



    @FormUrlEncoded
    @POST(Constants.getpromocodeamount)
    Call<CouponModel> getpromocodeamount(@Field(Constants.user_id) String user_id,
                                         @Field(Constants.promocode) String promocode);



    @FormUrlEncoded
    @POST(Constants.specialfarelocationcitytaxi)
    Call<SpecialFareResponse> specialfarelocationcitytaxi(@Field(Constants.user_id) String user_id);



    @FormUrlEncoded
    @POST(Constants.specialfarelocationoutstation)
    Call<SpecialFareResponse> specialfarelocationoutstation(@Field(Constants.user_id) String user_id);




    @FormUrlEncoded
    @POST(Constants.estimate_specialfare)
    Call<SpecialFareEstimateResponse> estimate_specialfare(@Field(Constants.pickup_latitude) double pickup_latitude,
                                                           @Field(Constants.pickup_longitude) double pickup_longitude,
                                                           @Field(Constants.drop_latitude) double drop_latitude,
                                                           @Field(Constants.drop_longitude) double drop_longitude,
                                                           @Field(Constants.cab_variant_id) String cab_variant_id,
                                                           @Field(Constants.pickupcity) String pickupcity,
                                                           @Field(Constants.dropoffcity) String dropoffcity);



    @FormUrlEncoded
    @POST(Constants.get_cab_details)
    Call<CabDetailsResponse> get_cab_details(@Field(Constants.user_id) String user_id);




    @FormUrlEncoded
    @POST(Constants.customer_resend_otp)
    Call<ResendOtpResponse> customer_resend_otp(@Field(Constants.phone) String phone);


    @FormUrlEncoded
    @POST(Constants.update_paytm_number)
    Call<FeedbackRatingResponse> update_paytm_number(@Field(Constants.booking_id) String booking_id,
                                                @Field(Constants.phone) String phone);


    @FormUrlEncoded
    @POST(Constants.get_fare_details)
    Call<FareDetails> get_fare_details(@Field(Constants.booking_id) String booking_id);

}


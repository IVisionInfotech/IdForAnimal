package com.idforanimal.retrofit;

import com.google.gson.JsonObject;
import com.idforanimal.model.APIResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface ApiInterface {

    @FormUrlEncoded
    @POST
    Call<JsonObject> login(
            @Url String url,
            @Field("username") String username,
            @Field("password") String password,
            @Field("playerId") String playerId
    );

    @FormUrlEncoded
    @POST
    Call<APIResponse> getSql(
            @Url String url
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getList(
            @Url String url,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getList(
            @Url String url,
            @Field("id") String id,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getList(
            @Url String url,
            @Field("customerId") String customerId,
            @Field("formType") String formType,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getList(
            @Url String url,
            @Field("id") String userId,
            @Field("limit") String limit,
            @Field("offset") String offset,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getList(
            @Url String url,
            @Field("id") String userId,
            @Field("ownerId") String ownerId,
            @Field("keyword") String keyword,
            @Field("limit") String limit,
            @Field("offset") String offset,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getList(
            @Url String url,
            @Field("id") String userId,
            @Field("ownerId") String ownerId,
            @Field("keyword") String keyword,
            @Field("filterType") String filterType,
            @Field("limit") String limit,
            @Field("offset") String offset,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getList(
            @Url String url,
            @Field("ownerId") String ownerId,
            @Field("id") String userId,
            @Field("keyword") String keyword,
            @Field("filterType") String filterType,
            @Field("auditId") String auditId,
            @Field("limit") String limit,
            @Field("offset") String offset,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getList(
            @Url String url,
            @Field("id") String userId,
            @Field("keyword") String keyword,
            @Field("limit") String limit,
            @Field("offset") String offset,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getDetails(
            @Url String url,
            @Field("id") String id,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );




    @FormUrlEncoded
    @POST()
    Call<APIResponse> addAnimalAuditRemark(
            @Url String url,
            @Field("id") String id,
            @Field("auditId") String auditId,
            @Field("animalId") String animalId,
            @Field("rfidTagNo") String rfidTagNo,
            @Field("auditStatus") String auditStatus,
            @Field("missingStatus") String missingStatus,
            @Field("remark") String remark,
            @Field("customerId") String customerId,
            @Field("empId") String empId,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getAuditDetails(
            @Url String url,
            @Field("id") String id,
            @Field("auditId") String auditId,
            @Field("auditStatus") String auditStatus,
            @Field("missingStatus") String missingStatus,
            @Field("remark") String remark,
            @Field("empId") String empId,
            @Field("customerId") String customerId,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getAuditDetails(
            @Url String url,
            @Field("id") String id,
            @Field("auditId") String auditId,
            @Field("auditStatus") String auditStatus,
            @Field("empId") String empId,
            @Field("customerId") String customerId,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getDetails(
            @Url String url,
            @Field("id") String id,
            @Field("pages") String pages,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> getDetails(
            @Url String url,
            @Field("userId") String userId,
            @Field("id") String id,
            @Field("tagNo") String tagNo,
            @Field("status") String status,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @Multipart
    @POST()
    Call<APIResponse> addAnimalOwner(
            @Url String url,
            @Part("id") RequestBody id,
            @Part("customerId") RequestBody customerId,
            @Part("empId") RequestBody empId,
            @Part("validDate") RequestBody validDate,
            @Part("registrationDate") RequestBody registrationDate,
            @Part("registrationNo") RequestBody registrationNo,
            @Part("firstName") RequestBody firstName,
            @Part("middleName") RequestBody middleName,
            @Part("lastName") RequestBody lastName,
            @Part("aadharNo") RequestBody aadharNo,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part file2,
            @Part("electionCardNo") RequestBody electionCardNo,
            @Part MultipartBody.Part file3,
            @Part MultipartBody.Part file4,
            @Part("dlNo") RequestBody dlNo,
            @Part MultipartBody.Part file5,
            @Part MultipartBody.Part file6,
            @Part("gender") RequestBody gender,
            @Part("houseNo") RequestBody houseNo,
            @Part("street") RequestBody street,
            @Part("landmark") RequestBody landmark,
            @Part("area") RequestBody area,
            @Part("countryName") RequestBody countryName,
            @Part("stateId") RequestBody stateId,
            @Part("districtName") RequestBody districtName,
            @Part("cityId") RequestBody cityId,
            @Part("pincode") RequestBody pincode,
            @Part("tenementNo") RequestBody tenementNo,
            @Part("zoneId") RequestBody zoneId,
            @Part("wardId") RequestBody wardId,
            @Part("pondStatus") RequestBody pondStatus,
            @Part("pondId") RequestBody pondID,
            @Part("contact") RequestBody contact,
            @Part("contact1") RequestBody contact1,
            @Part("emailId") RequestBody emailId,
            @Part("placeArea") RequestBody placeArea,
            @Part("placeOwned") RequestBody placeOwned,
            @Part("shedAvailable") RequestBody shedAvailable,
            @Part("storageAvailable") RequestBody storageAvailable,
            @Part("drinkingWater") RequestBody drinkingWater,
            @Part("disposalFacility") RequestBody disposalFacility,
            @Part MultipartBody.Part file7,
            @Part("remarks") RequestBody remarks,
            @Part MultipartBody.Part file8,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("username") RequestBody username,
            @Part("userPassword") RequestBody userPassword,
            @Part("type") RequestBody type
    );

    @Multipart
    @POST()
    Call<APIResponse> addAnimal(
            @Url String url,
            @Part("id") RequestBody id,
            @Part("customerId") RequestBody customerId,
            @Part("empId") RequestBody empId,
            @Part("ownerId") RequestBody ownerId,
            @Part("taggingDate") RequestBody taggingDate,
            @Part("validDate") RequestBody validDate,
            @Part("rfidTagNo") RequestBody rfidTagNo,
            @Part("visualTagNo") RequestBody visualTagNo,
            @Part("catchingLocation") RequestBody catchingLocation,
            @Part("animalTypeId") RequestBody animalTypeId,
            @Part("cattleTypeId") RequestBody cattleTypeId,
            @Part("breedTypeId") RequestBody breedTypeId,
            @Part("colorId") RequestBody colorId,
            @Part("tailId") RequestBody tailId,
            @Part("hornId") RequestBody hornId,
            @Part("visualSign") RequestBody visualSign,
            @Part("dob") RequestBody dob,
            @Part("age") RequestBody age,
            @Part("girth") RequestBody girth,
            @Part("length") RequestBody length,
            @Part("milperday") RequestBody milperday,
            @Part("milktype") RequestBody milktype,
            @Part("lactation") RequestBody lactation,
            @Part("pregStatus") RequestBody pregStatus,
            @Part("remark") RequestBody remark,
            @Part("pondStatus") RequestBody pondStatus,
            @Part("pondId") RequestBody pondID,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part file2,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("username") RequestBody username,
            @Part("userPassword") RequestBody userPassword,
            @Part("type") RequestBody type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> addEditCatching(
            @Url String url,
            @Field("id") String id,
            @Field("animalIds") String animalIds,
            @Field("remark") String remark,
            @Field("area") String area,
            @Field("landmark") String landmark,
            @Field("wardId") String wardId,
            @Field("zoneId") String zoneId,
            @Field("pondId") String pondId,
            @Field("catchedTime") String catchedTime,
            @Field("catchedDate") String catchedDate,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("customerId") String customerId,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> addEditRelease(
            @Url String url,
            @Field("id") String id,
            @Field("customerId") String customerId,
            @Field("ownerId") String ownerId,
            @Field("releaseDate") String releaseDate,
            @Field("firNo") String firNo,
            @Field("chargeSheetNo") String chargeSheetNo,
            @Field("affidavitNo") String affidavitNo,
            @Field("paymentReceiptNo") String paymentReceiptNo,
            @Field("remark") String remark,
            @Field("area") String area,
            @Field("landmark") String landmark,
            @Field("animalIds") String animalIds,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> addEditNgoTransfer(
            @Url String url,
            @Field("id") String id,
            @Field("customerId") String customerId,
            @Field("ngoId") String ngoId,
            @Field("transferDate") String transferDate,
            @Field("vehicleNo") String vehicleNo,
            @Field("driverName") String driverName,
            @Field("contact") String contact,
            @Field("contact1") String contact1,
            @Field("cleanerName") String cleanerName,
            @Field("conveyanceDetails") String conveyanceDetails,
            @Field("applicationNo") String applicationNo,
            @Field("applicationDate") String applicationDate,
            @Field("animalIds") String animalIds,
            @Field("remark") String remark,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> addEditAudit(
            @Url String url,
            @Field("id") String id,
            @Field("startDate") String startDate,
            @Field("endDate") String endDate,
            @Field("auditbyInstitution") String auditbyInstitution,
            @Field("auditbyPerson") String auditbyPerson,
            @Field("status") String status,
            @Field("customerId") String customerId,
            @Field("empId") String empId,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );




    @FormUrlEncoded
    @POST()
    Call<APIResponse> addAnimalHistory(
            @Url String url,
            @Field("animalId") String animalId,
            @Field("oldOwnerId") String oldOwnerId,
            @Field("newOwnerId") String newOwnerId,
            @Field("rfidTagNo") String rfidTagNo,
            @Field("visualTagNo") String visualTagNo,
            @Field("historyType") String historyType,
            @Field("date") String date,
            @Field("pondStatus") String pondStatus,
            @Field("pondId") String pondId,
            @Field("other") String other,
            @Field("id") String id,
            @Field("remark") String remark,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("customerId") String customerId,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );

    @FormUrlEncoded
    @POST()
    Call<APIResponse> editAnimalDeath(
            @Url String url,
            @Field("id") String id,
            @Field("deathDate") String deathDate,
            @Field("disposedDate") String disposedDate,
            @Field("deathCertificateNo") String deathCertificateNo,
            @Field("disposedReceipt") String disposedReceipt,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("customerId") String customerId,
            @Field("username") String username,
            @Field("userPassword") String userPassword,
            @Field("type") String type
    );


}
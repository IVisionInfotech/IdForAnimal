package com.idforanimal.utils;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_BROWSABLE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.idforanimal.activity.BaseActivity;
import com.idforanimal.activity.ImagePreviewActivity;
import com.idforanimal.activity.LoginActivity;
import com.idforanimal.loadmore.RecyclerViewLoadMoreScroll;
import com.idforanimal.model.OfflineAnimalData;
import com.idforanimal.service.ConnectivityReceiver;
import com.idforanimal.R;
import com.idforanimal.model.User;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class Common {

    private static String TAG = BaseActivity.TAG;
    public static String ARG_PARAM1 = "param1";
    public static String ARG_PARAM2 = "param2";
    private static Context context = MyApplication.getContext();
    public static final int REQUEST_CHECK_SETTINGS = 100;
    public static final int PERMISSION_REQUEST_CODE = 100;

    public static final int REQUEST_CODE = 999;
    private static long mLastClickTime = 0;
    public static final int PERMISSION_REQUEST_LOCATION_CODE = 99;
    private static ProgressDialog pDialog;
    private static OnDateSet onDateSet;
    private static Calendar myCalendar = Calendar.getInstance();
    public static Runnable ansTrue = null;
    public static Runnable ansFalse = null;

    public interface OnDateSet {
        void OnDateSet(String date);
    }

    public interface OnDOBDateSet {
        void OnDOBDateSet(String date, int year, int monthOfYear, int dayOfMonth);
    }

    public static String handleIntent(Intent intent) {
        String id = "", type = "";
        if (intent != null) {
            String appLinkAction = intent.getAction();
            Uri appLinkData = intent.getData();
            if (ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
                id = intent.getData().getQueryParameter("id");
                type = intent.getData().getQueryParameter("type");
                id = decodeData(decodeData(id).trim()).trim();
                type = decodeData(decodeData(type).trim()).trim();
                return id + ", " + type;
            }
        }
        return "";
    }

    public void goToActivity(Context context, Class aClass, String id, String title) {
        Intent intent = new Intent(context, aClass);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        ((Activity) context).startActivity(intent);
    }

    public static void loadImage(final Context context, ImageView imageView, String url) {
        if (context == null) return;
        Glide.with(context) //passing context
                .load(url) //passing your url to load image.
                .fitCenter()//this method help to fit image into center of your ImageView
                .into(imageView); //pass imageView reference to appear the image.
    }

    public static void loadImage1(ImageView imageView, String url) {
        if (context == null) return;
        Glide.with(context) //passing context
                .load(url) //passing your url to load image.
                .placeholder(R.drawable.animal_bg) //this would be your default image (like default profile or logo etc). it would be loaded at initial time and it will replace with your loaded image once glide successfully load image using url.
                .error(R.drawable.animal_bg)//in case of any glide exception or not able to download then this image will be appear . if you won't mention this error() then nothing to worry placeHolder image would be remain as it is.
                .fitCenter()//this method help to fit image into center of your ImageView
                .into(imageView); //pass imageView reference to appear the image.
    }


    public static void loadImage(ImageView imageView, int placeholder, String url) {
        if (context == null) return;
        Glide.with(context) //passing context
                .load(url) //passing your url to load image.
                .placeholder(placeholder) //this would be your default image (like default profile or logo etc). it would be loaded at initial time and it will replace with your loaded image once glide successfully load image using url.
                .error(placeholder)//in case of any glide exception or not able to download then this image will be appear . if you won't mention this error() then nothing to worry placeHolder image would be remain as it is.
                .fitCenter()//this method help to fit image into center of your ImageView
                .into(imageView); //pass imageView reference to appear the image.
    }

    public static void loadImage(final Context context, ImageView imageView, int url) {
        if (context == null) return;
        Glide.with(context) //passing context
                .load(url) //passing your url to load image.
//                .placeholder(R.drawable.logo) //this would be your default image (like default profile or logo etc). it would be loaded at initial time and it will replace with your loaded image once glide successfully load image using url.
//                .error(R.drawable.logo)//in case of any glide exception or not able to download then this image will be appear . if you won't mention this error() then nothing to worry placeHolder image would be remain as it is.
                .fitCenter()//this method help to fit image into center of your ImageView
                .into(imageView); //pass imageView reference to appear the image.
    }

    public static boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            return (boolean) m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void shareProduct(Context context, String id, String title, String type) {

        title = title.replace(' ', '-');

        String url = Constant.detailsBaseURL + "id=" + encodeData(encodeData(id).trim()).trim() + "&type=" + encodeData(encodeData(type).trim()).trim();

        String shareBody = title + "\n\nAvailable only at " + context.getResources().getString(R.string.app_name) + "." + "\n" + url + "\n\n\nLet me recommend you this application\n" + "https://play.google.com/store/apps/details?id=" + context.getPackageName();

        String shareSubject = context.getResources().getString(R.string.app_name) + context.getResources().getString(R.string.share_text);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra("productId", id);
        ((Activity) context).startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public static String encodeData(String data) {
        return new String(Base64.encode(data.getBytes(), Base64.NO_PADDING));
    }

    public static String decodeData(String data) {
        return new String(Base64.decode(data, Base64.NO_PADDING));
    }

    public static String getUserId() {
        String id = "";
        RealmController.with(context).refresh();
        User user = RealmController.with(context).getUser();
        if (user != null) {
            id = String.valueOf(user.getId());
        }
        return id;
    }

    public static String getCustomerId() {
        String id = "";
        RealmController.with(context).refresh();
        User user = RealmController.with(context).getUser();
        if (user != null) {
            if (getType().equals(Constant.loginTypeEmployee)) {
                id = String.valueOf(user.getCustomerId());
            } else if (getType().equals(Constant.loginTypeCustomer)) {
                id = String.valueOf(user.getId());
            }
        }
        return id;
    }

    public static String getEmpId() {
        String id = "";
        RealmController.with(context).refresh();
        User user = RealmController.with(context).getUser();
        if (user != null) {
            if (getType().equals(Constant.loginTypeEmployee)) {
                id = String.valueOf(user.getId());
            }
        }
        return id;
    }

    public static String getUserName() {
        String id = "";
        RealmController.with(context).refresh();
        User user = RealmController.with(context).getUser();
        if (user != null) {
            id = String.valueOf(user.getName());
        }
        return id;
    }

    public static String getUsername() {
        Session session = new Session(context);
        return session.getUsername();
    }

    public static String getPassword() {
        Session session = new Session(context);
        return session.getPassword();
    }

    public static String getType() {
        Session session = new Session(context);
        return session.getLoginType();
    }

    public static void openImagePreview(Context context, ArrayList<String> imageList) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra("list", imageList);
        context.startActivity(intent);
    }

    public static void openImagePreview(Context context, String image) {
        ArrayList<String> imageList = new ArrayList<>();
        imageList.add(image);
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra("list", imageList);
        context.startActivity(intent);
    }

    public static void loadImageAndSetOnClickListener(Context context, ImageView imageView, String imageUrl) {
        if (!imageUrl.equals("")) {
            Common.loadImage(context, imageView, imageUrl);
            imageView.setOnClickListener(v -> Common.openImagePreview(context, imageUrl));
        }
    }

    public static void logout(Context context, String message) {
        Session session = new Session(context);
        if (!message.isEmpty()) showToast(message);
        session.setLoginStatus(false);
        session.setUsername("");
        session.setPassword("");
        RealmController.with(context).clearAllUser();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static String removeLastChars(String str, int chars) {
        return str.substring(0, str.length() - chars);
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) return null;

        // Create file path inside app's data dir
        String filePath = context.getApplicationInfo().dataDir + File.separator + "temp_file";
        File file = new File(filePath);
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null) return null;
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) outputStream.write(buf, 0, len);
            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }
        return file.getAbsolutePath();
    }

    public static String getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String message = "";
        if (hour >= 0 && hour < 12) {
            message = "Good morning!";
        } else if (hour >= 12 && hour < 18) {
            message = "Good afternoon!";
        } else {
            message = "Good evening!";
        }

        return message;
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public static String changeDateFormat(String dateTime, String inputPattern, String outputPattern) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = "";
        try {
            date = inputFormat.parse(dateTime);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String changeDateFormat(String dateTime, String outputPattern) {
        String inputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = "";
        try {
            date = inputFormat.parse(dateTime);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String changeDateFormat(String dateTime) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = "";
        try {
            date = inputFormat.parse(dateTime);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static boolean compareDates(String enteredDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(enteredDate);
            Date c = Calendar.getInstance().getTime();
            Date date2 = sdf.parse(sdf.format(c));
            if (date.compareTo(date2) == 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean compareGreaterDates(String enteredDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(enteredDate);
            Date c = Calendar.getInstance().getTime();
            Date date2 = sdf.parse(sdf.format(c));
            if (date2 != null && date2.compareTo(date) >= 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean compareSmallerDates(String enteredDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(enteredDate);
            Date c = Calendar.getInstance().getTime();
            Date date2 = sdf.parse(sdf.format(c));
            if (date2 != null && date2.compareTo(date) <= 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getDaysCount(String startDate, String endDate) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dateBefore = myFormat.parse(startDate);
            Date dateAfter = myFormat.parse(endDate);
            long difference = dateAfter.getTime() - dateBefore.getTime();
            int daysBetween = (int) (difference / (1000 * 60 * 60 * 24));
            return String.valueOf(daysBetween + 1);
        } catch (Exception exception) {
            return "";
        }
    }

    public static String getAge(int year, int month, int day) {

        String days = "";

        Calendar now = Calendar.getInstance();
        Calendar birthDay = Calendar.getInstance();

        birthDay.set(Calendar.YEAR, year);
        birthDay.set(Calendar.MONTH, month);
        birthDay.set(Calendar.DAY_OF_MONTH, day);

        double diff = (long) (now.getTimeInMillis() - birthDay.getTimeInMillis());

        if (diff < 0) {
            showToast("Selected date is in future.");
            days = "";
        } else {

            int years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
            int currMonth = now.get(Calendar.MONTH) + 1;
            int birthMonth = birthDay.get(Calendar.MONTH) + 1;

            int months = currMonth - birthMonth;

            if (months < 0) {
                years--;
                months = 12 - birthMonth + currMonth;
                if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) months--;
            } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                years--;
                months = 11;
            }

            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
                now.add(Calendar.MONTH, -1);
            } else {
                if (months == 12) {
                    years++;
                }
            }
            months = years * 12 + months;
            days = String.valueOf(months);
        }
        return days;
    }

    public static String getDOBFromAge(int ageInMonths) {
        String dateOfBirth = "";

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -ageInMonths);
        Date result = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateOfBirth = dateFormat.format(result.getTime());

        return dateOfBirth;
    }

    public static MultipartBody.Part imageToMultipart(String path, String fileName, String name) {
        File file = new File(path);

        RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/*"));
        return MultipartBody.Part.createFormData(name, fileName, requestBody);
    }

    public static RequestBody dataToRequestBody(String data) {
        return RequestBody.create(data, MediaType.parse("text/plain"));
    }

    public static void downloadFile(Context context, String url, String fileName) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            String title = URLUtil.guessFileName(url, null, null);
            request.setTitle(title);
            request.setDescription("Downloading File please wait...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, (context.getString(R.string.app_name) + File.separator + fileName));
            DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            manager.enqueue(request);
            showToast("Downloading Completed...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDatePicker(Context context, OnDateSet onDateSet) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

            onDateSet.OnDateSet(sdf.format(myCalendar.getTime()));
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public static void showMinDatePicker(Context context, OnDateSet onDateSet) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

            onDateSet.OnDateSet(sdf.format(myCalendar.getTime()));
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public static void showMinDatePicker(Context context, String startDate, OnDateSet onDateSet) {
        Calendar myCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            myCalendar.setTime(sdf.parse(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
            myCalendar = Calendar.getInstance();
        }

        long minDate = myCalendar.getTimeInMillis();

        Calendar finalMyCalendar = myCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            finalMyCalendar.set(Calendar.YEAR, year);
            finalMyCalendar.set(Calendar.MONTH, monthOfYear);
            finalMyCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            onDateSet.OnDateSet(sdf.format(finalMyCalendar.getTime()));
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();
    }



    public static void showDatePicker(Context context, OnDOBDateSet onDateSet) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

            onDateSet.OnDOBDateSet(sdf.format(myCalendar.getTime()), year, monthOfYear, dayOfMonth);
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public static void showTimePicker(Context context, OnDateSet onDateSet) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, (timePicker, selectedHour, selectedMinute) -> {
            myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            myCalendar.set(Calendar.MINUTE, selectedMinute);

            String myFormat = "HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            onDateSet.OnDateSet(sdf.format(myCalendar.getTime()));
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public static void disableButton(Button btnSubmit, int drawable) {
        btnSubmit.setBackground(context.getResources().getDrawable(drawable));
        btnSubmit.setEnabled(false);
    }

    public static void enableButton(Button btnSubmit, int drawable) {
        btnSubmit.setBackground(context.getResources().getDrawable(drawable));
        btnSubmit.setEnabled(true);
    }

    public static void showProgressDialog(Context context, String msg) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
            pDialog.setMessage(msg);
            pDialog.show();
        }
    }

    public static void hideProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public static void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public static void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean confirmationDialog(Activity activity, String message, String cancelBtn, String okBtn, Runnable aProcedure, Runnable bProcedure) {
        ansTrue = aProcedure;
        ansFalse = bProcedure;

        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, okBtn, (dialog1, buttonId) -> ansTrue.run());
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancelBtn, (dialog12, buttonId) -> ansFalse.run());
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
        return true;
    }

    public static boolean confirmationDialog(Activity activity, String title, String message, String cancelBtn, String okBtn, Runnable aProcedure) {
        ansTrue = aProcedure;

        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, okBtn, (dialog1, buttonId) -> ansTrue.run());
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancelBtn, (dialog12, buttonId) ->dialog12.dismiss() );
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
        return true;
    }

    public static boolean confirmationDialog(Context context, String message, String cancelBtn, String okBtn, Runnable aProcedure) {
        ansTrue = aProcedure;

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, okBtn, (dialog1, buttonId) -> ansTrue.run());
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancelBtn, (dialog12, buttonId) -> dialog12.dismiss());
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
        return true;
    }

    public static void bindGridRecyclerView(RecyclerView recyclerView, int spanCount, int orientation) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, spanCount, orientation, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
    }

    public static RecyclerViewLoadMoreScroll bindLoadMoreRecyclerView(RecyclerView recyclerView, int spanCount, int orientation, ClickListener listener) {
        StaggeredGridLayoutManager mLayoutManager1 = new StaggeredGridLayoutManager(spanCount, orientation);
        recyclerView.setLayoutManager(mLayoutManager1);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setHasFixedSize(true);

        RecyclerViewLoadMoreScroll scrollListener = new RecyclerViewLoadMoreScroll(mLayoutManager1);
        scrollListener.setOnLoadMoreListener(() -> listener.onLoadListener());
        recyclerView.addOnScrollListener(scrollListener);

        return scrollListener;
    }

    public static void openPopupMenu(Context context, ImageView imageView, int layout, ClickListener listener) {
        PopupMenu popup = new PopupMenu(context, imageView);
        popup.inflate(layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true);
        }
        popup.setOnMenuItemClickListener(item -> {
            listener.onItemSelected(item.getItemId());
            return true;
        });
        popup.show();
    }

    public static Bitmap getBitmapFromUri(Uri uri, String address) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public static Bitmap overlayTextOnBitmap(Bitmap bitmap, String location) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        // Calculate dynamic text size based on the image height
        int imageHeight = mutableBitmap.getHeight();
        int dynamicTextSize = imageHeight / 40; // Adjust the divisor to control the text size scaling (20 is an example)

        // Prepare paint for text
        Paint paint = new Paint();
        paint.setColor(Color.WHITE); // Text color
        paint.setTextSize(dynamicTextSize); // Dynamically set the text size
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // Prepare paint for background rectangles
        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.BLACK);
        rectPaint.setAlpha(200); // Semi-transparent background

        // Get current date and time
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Measure the text to calculate background size for the date/time
        Rect textBounds = new Rect();
        paint.getTextBounds(currentDateTime, 0, currentDateTime.length(), textBounds);
        int padding = dynamicTextSize / 3; // Adjust the padding relative to the text size

        // Draw the background rectangle for date/time at the top
        canvas.drawRect(0, 0, textBounds.width() + padding, textBounds.height() + padding, rectPaint);
        canvas.drawText(currentDateTime, padding / 2, textBounds.height(), paint);

        // Now handle the multi-line location (address)

        // Use TextPaint instead of Paint for StaticLayout
        TextPaint textPaint = new TextPaint(paint);
        int maxWidth = mutableBitmap.getWidth() - (padding * 2); // Max width for the text to wrap

        // Create StaticLayout for the multi-line location text
        StaticLayout locationLayout = new StaticLayout(location, textPaint, maxWidth,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // Calculate the height of the multi-line text
        int textHeight = locationLayout.getHeight();

        // Draw the background rectangle for the location (multi-line)
        canvas.drawRect(0, mutableBitmap.getHeight() - textHeight - padding,
                maxWidth + padding, mutableBitmap.getHeight(), rectPaint);

        // Draw the multi-line location text at the bottom
        canvas.save();
        canvas.translate(padding / 2, mutableBitmap.getHeight() - textHeight - padding / 2);
        locationLayout.draw(canvas);
        canvas.restore();

        return mutableBitmap;
    }


    public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static void setTintFilter(Context context, ImageView imageView, int colorId) {
        imageView.setColorFilter(ContextCompat.getColor(context, colorId), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public static void openBrowser(Context context, String url) {
        String query = Uri.encode(url, "UTF-8");
        Intent browserIntent = new Intent(CATEGORY_BROWSABLE, Uri.parse(Uri.decode(query)));
        browserIntent.setAction(ACTION_VIEW);
        ((Activity) context).startActivity(browserIntent);
    }

    public static void dialNumber(Context context, String contact) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + contact));
        ((Activity) context).startActivity(callIntent);
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static boolean displayLocationSettingsRequest(Context context, Activity activity) {

        final boolean[] flag = {false};

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {
            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.
                flag[0] = true;
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

        return flag[0];

    }

    public static String getAddress(Context context, double latitude, double longitude) {
        String strAdd = "";
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = String.valueOf(strReturnedAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd.toString();
    }


    public static String getAddressCity(Context context, double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = "";
            if (addresses.get(0).getLocality() == null) {
                city = addresses.get(0).getSubAdminArea();
            } else {
                city = addresses.get(0).getLocality();
            }
            return city;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void sendBroadcast(Context context, Intent intent) {
        intent.putExtra(Constant.refreshCount, Constant.refreshCount);
        intent.setAction(Constant.refreshCount);
        BroadCastManager.getInstance().sendBroadCast(context, intent);
    }

    public static float randomFloatBetween(int minYValue, int maxYValue) {
        Random rand = new Random();
        int randomInt = rand.nextInt((maxYValue - minYValue) + 1) + minYValue;
        return randomInt / 1000.00f;
    }

    public static Date convertStringToDate(String dtStart) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(dtStart);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static Calendar dateToCalendar(String date) {
        Calendar cal = Calendar.getInstance();
        if (convertStringToDate(date) != null) cal.setTime(convertStringToDate(date));
        return cal;
    }

    public static boolean isActivityActive(Activity activity) {
        if (null != activity) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return !activity.isFinishing() && !activity.isDestroyed();
        else return !activity.isFinishing();
        return false;
    }

    public static String getLastDetail(String data, String total, String type) {
        if (data != null && !data.isEmpty()) {
            return data + " (" + type + " COUNT :" + total + ")";
        } else {
            return " (" + type + " COUNT :" + total + ")";
        }
    }

    public static String buildText(String firstPart, String secondPart) {
        StringBuilder sb = new StringBuilder();
        if (firstPart != null && !firstPart.isEmpty()) {
            sb.append(firstPart);
        }
        if (firstPart != null && !firstPart.isEmpty() && secondPart != null && !secondPart.isEmpty()) {
            sb.append(" - ");
        }
        if (secondPart != null && !secondPart.isEmpty()) {
            sb.append(secondPart);
        }
        return sb.toString();
    }

    public static void setVisibilityAndText(View view, TextView textView, String data) {
        if (!TextUtils.isEmpty(data)) {
            if (data.equals("0000-00-00") || data.equals("30-11-0002")) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
                textView.setText(data);
            }
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public static void setVisibilityAndText(View view, TextView textView, String data, String data1) {
        if (!TextUtils.isEmpty(data)) {
            view.setVisibility(View.VISIBLE);
            data = data + data1;
            textView.setText(data);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public static File bitmapToFile(Activity activity, Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
        File directory = cw.getDir("imgDir", 0);
        String child = (long) (new Random()).nextInt(20) + System.currentTimeMillis() + "_Picker.jpg";
        File file = new File(directory, child);
        if (!file.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (IOException var10) {
                var10.printStackTrace();
            }
        }
        return file;
    }

//    public static File bitmapToFile(Activity activity, Bitmap bitmap, String location) {
//        Bitmap newBitmap = overlayTextOnBitmap(bitmap, location);
//
//        ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
//        File directory = cw.getDir("imgDir", 0);
//        String child = (long) (new Random()).nextInt(20) + System.currentTimeMillis() + "_Picker.jpg";
//        File file = new File(directory, child);
//
//        if (!file.exists()) {
//            try {
//                FileOutputStream fos = new FileOutputStream(file);
//                newBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                fos.flush();
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return file;
//    }


    public static void touchAnimation(final Context context, View view) {
        view.setOnTouchListener((v, event) -> {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.press);
            v.startAnimation(animation);
            return false;
        });
    }

    public static void noInternetDialog(final Context context) {
        final Dialog dialog = new Dialog(context, R.style.ThemeWithCorners);

        dialog.setContentView(R.layout.no_internet_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTransition;

        Button btnretry = dialog.findViewById(R.id.btnretry);
        TextView txtdesc = dialog.findViewById(R.id.txtDescription);

        Common.touchAnimation(context, btnretry);
        btnretry.setOnClickListener(v -> {
            if (avoidDoubleClick()) {
                if (ConnectivityReceiver.isConnected(context)) {
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    noInternetDialog(context);
                }
            }
        });
        dialog.show();
//        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static boolean avoidDoubleClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    public static void generateCSVFile(Context context, List<OfflineAnimalData> dataList) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "OfflineAnimalData_" + timestamp + ".csv";

        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, fileName);

        try {
            FileWriter writer = new FileWriter(file);
            writer.append("Microchip No,Latitude,Longitude\n");

            for (OfflineAnimalData data : dataList) {
                writer.append("=\"").append(data.getMicroChipNo()).append("\"").append(",");
                writer.append(data.getLatitude()).append(",");
                writer.append(data.getLongitude()).append("\n");
            }

            writer.flush();
            writer.close();

            Toast.makeText(context, "CSV File Saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            openOrShareCSVFile(context, file);

        } catch (IOException e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void openCSVFile(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(androidx.core.content.FileProvider.getUriForFile(
                context, context.getPackageName() + ".provider", file), "text/csv");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Open CSV"));
    }

    private static void openOrShareCSVFile(Context context, File file) {
        Uri uri = androidx.core.content.FileProvider.getUriForFile(
                context, context.getPackageName() + ".provider", file);

        Intent openIntent = new Intent(Intent.ACTION_VIEW);
        openIntent.setDataAndType(uri, "text/csv");
        openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(openIntent, "Open CSV");

        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(openIntent, 0);
        List<Intent> targetedIntents = new ArrayList<>();

        for (ResolveInfo resInfo : resInfoList) {
            String packageName = resInfo.activityInfo.packageName;
            if (packageName.contains("excel") || packageName.contains("sheet") ||
                    packageName.contains("viewer") || packageName.contains("docs") ||
                    packageName.contains("drive") || packageName.contains("office")) {

                Intent targetedIntent = new Intent(Intent.ACTION_VIEW);
                targetedIntent.setDataAndType(uri, "text/csv");
                targetedIntent.setPackage(packageName);
                targetedIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                targetedIntents.add(targetedIntent);
            }
        }

        if (!targetedIntents.isEmpty()) {
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedIntents.toArray(new Parcelable[0]));
            context.startActivity(chooser);
        } else {
            Toast.makeText(context, "No compatible apps found to open CSV", Toast.LENGTH_SHORT).show();
        }
    }

    public static void generateExcelFile(Context context, List<OfflineAnimalData> dataList) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "OfflineAnimalData_" + timestamp + ".xlsx";

        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, fileName);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Animal Data");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"Microchip No", "Latitude", "Longitude"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (OfflineAnimalData data : dataList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getMicroChipNo());
            row.createCell(1).setCellValue(data.getLatitude());
            row.createCell(2).setCellValue(data.getLongitude());
        }

        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            Toast.makeText(context, "Excel File Saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            shareExcelFile(context, file);

        } catch (IOException e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void shareExcelFile(Context context, File file) {
        Uri uri = androidx.core.content.FileProvider.getUriForFile(
                context, context.getPackageName() + ".provider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(shareIntent, "Share Excel File"));
    }


}

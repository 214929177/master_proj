package cn.edu.hfut.lilei.shareboard.utils;

import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.data.AppInfo;
import cn.edu.hfut.lilei.shareboard.view.InviteChooserDialog;
import cn.edu.hfut.lilei.shareboard.view.LodingDialog;

import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.TIMEZONE;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class MyAppUtil {
    /**
     * 根据应用包名获取应用名
     *
     * @param context
     * @param packageName 应用包名
     * @return
     */
    public static String getApplicationNameByPackageName(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        String Name;
        try {
            Name = pm.getApplicationLabel(
                    pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA))
                    .toString();
        } catch (PackageManager.NameNotFoundException e) {
            Name = "";
        }
        return Name;
    }

    /**
     * 复制内容到剪贴板
     *
     * @param context
     * @param content 文本内容
     */
    public static void copy(Context context, String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        showToast(context, context.getString(R.string.alreday_copy_to_sheet));
    }

    /**
     * 发送短信
     *
     * @param context
     * @param smsBody 短信内容
     */
    public static void sendSMS(Context context, String smsBody)

    {

        Uri smsToUri = Uri.parse("smsto:");

        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

        intent.putExtra("sms_body", smsBody);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(
                    Intent.createChooser(intent, context.getString(R.string.choose_sms)));
        }

    }

    /**
     * 发送邮件
     *
     * @param context
     * @param subject 主题
     * @param Body    正文
     */
    public static void sendMail(Context context, String subject, String Body)

    {
        Uri mailToUri = Uri.parse("mailto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, mailToUri);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, Body); // 正文
        context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.choose_mail)));

    }

    /**
     * 吐司通知（短时间）
     *
     * @param context
     * @param msg     文本
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                .show();
    }

    public static void showToast(Context context, int strid) {
        Toast.makeText(context, context.getResources()
                .getString(strid), Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * 打印log
     *
     * @param msg
     */
    public static void showLog(String msg) {
        Log.i(TAG, msg);
    }


    /**
     * 查看指定日期的日历事件提醒
     *
     * @param context
     * @param timeMillis 指定日期
     */
    public static void viewCalendarEvent(Context context, long timeMillis) {

        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, timeMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        context.startActivity(intent);

    }

    /**
     * 添加事件（提醒）系统日历
     *
     * @param context
     * @param startMillis 开始时间
     * @param endMillis   结束时间
     * @param title       标题
     * @param location    地点
     * @param description 备注
     * @param timezone    时区
     * @return 事件ID
     */
    public static long insertCalendarEvent(Context context, long startMillis, long endMillis,
                                           String title, @Nullable String location, String
                                                   description,
                                           @Nullable String
                                                   timezone) {
        long calID = 1;

        //添加日历事件
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE,
                String.format(context.getResources()
                        .getString(R.string.invite_title), title));
        values.put(CalendarContract.Events.EVENT_LOCATION,
                context.getString(R.string.meeting_location));
        values.put(CalendarContract.Events.DESCRIPTION,
                description);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TIMEZONE);//时区
        values.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);

        try {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            long mEventID = Long.parseLong(uri.getLastPathSegment());
            // 添加 提前15分钟提醒
            ContentValues values2 = new ContentValues();
            values2.put(CalendarContract.Reminders.MINUTES, 15);
            values2.put(CalendarContract.Reminders.EVENT_ID, mEventID);
            values2.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            context.getContentResolver()
                    .insert(CalendarContract.Reminders.CONTENT_URI, values2);
            showToast(context, context.getString(R.string.meeting_event_already_add));
            return mEventID;
        } catch (SecurityException e) {
            return -1;
        }


    }

    /**
     * 选择邀请方式
     */
    public static void invite(Context context, String title, String description,
                              List<AppInfo> listAppInfo) {

        final String subject =
                String.format(context.getResources()
                        .getString(R.string.invite_title), title);
        final String content = description;

        final InviteChooserDialog.Builder dialog =
                new InviteChooserDialog.Builder(context);

        dialog.setTitle(context.getString(R.string.choose_invite_type));
        dialog.setSubject(subject);
        dialog.setContent(content);
        dialog.setData(listAppInfo);
        dialog.show();


    }

    /**
     * 加载中
     */
    public static LodingDialog.Builder loding(Context context, String title) {

        LodingDialog.Builder dialog = new LodingDialog.Builder(context);
        dialog.setTitle(title);
        dialog.show();
        return dialog;


    }

    /**
     * 应用是否在后台运行
     *
     * @param context
     * @return
     */
    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName()
                    .equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * get the package information of the context
     *
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager()
                    .getPackageInfo(
                            context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * whether the external storage (SDCard) exists
     *
     * @return
     */
    public static boolean isStorageExists() {
        if (Environment.getExternalStorageState()
                .equals(
                        Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }


    /**
     * 获取版本名称
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 获取版本号
     */
    public static int getAppVersionCode(Context context) {
        int versioncode = -1;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode;
    }


}


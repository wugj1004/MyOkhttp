package com.wugj.okhttp.request;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;

import com.wugj.okhttp.MyApplication;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * description:获取设备信息
 * </br>
 * author: wugj
 * </br>
 * date: 2018/9/10
 * </br>
 * version:
 */
public class DeviceInfo {

    private static DeviceInfo deviceInfo;

    public static DeviceInfo getInstance() {

        if (null == deviceInfo){
            synchronized (DeviceInfo.class){
                if (null == deviceInfo){
                    deviceInfo = new DeviceInfo();
                }
            }
        }
        return deviceInfo;
    }

    public Map<String, Object> getConstants() {
        Context ctx = MyApplication.getContext();
        HashMap<String, Object> constants = new HashMap<String, Object>();

        PackageManager packageManager = ctx.getPackageManager();
        String packageName = ctx.getPackageName();

        constants.put("appVersion", "not available");
        constants.put("appName", "not available");
        constants.put("buildVersion", "not available");
        constants.put("buildNumber", 0);

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            String applicationName = ctx.getApplicationInfo().loadLabel(ctx.getPackageManager()).toString();
            constants.put("appVersion", info.versionName);
            constants.put("buildNumber", info.versionCode);
            constants.put("firstInstallTime", info.firstInstallTime);
            constants.put("lastUpdateTime", info.lastUpdateTime);
            constants.put("appName", applicationName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String deviceName = "Unknown";

        String permission = "android.permission.BLUETOOTH";
        int res = ctx.checkCallingOrSelfPermission(permission);
        if (res == PackageManager.PERMISSION_GRANTED) {
            try {
                BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
                if (myDevice != null) {
                    deviceName = myDevice.getName();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        constants.put("serialNumber", Build.SERIAL);
        constants.put("deviceName", deviceName);
        constants.put("systemName", "Android");
        constants.put("systemVersion", Build.VERSION.RELEASE);
        constants.put("model", Build.MODEL);
        constants.put("brand", Build.BRAND);
        constants.put("deviceId", Build.BOARD);
        constants.put("apiLevel", Build.VERSION.SDK_INT);
        constants.put("deviceLocale", getCurrentLanguage(ctx));
        constants.put("deviceCountry", this.getCurrentCountry());
        constants.put("uniqueId", Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID));
        constants.put("systemManufacturer", Build.MANUFACTURER);
        constants.put("bundleId", packageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                constants.put("userAgent", WebSettings.getDefaultUserAgent(ctx));
            } catch (RuntimeException e) {
                constants.put("userAgent", System.getProperty("http.agent"));
            }
        }
        constants.put("timezone", TimeZone.getDefault().getID());
        constants.put("isEmulator", isEmulator());
        constants.put("isTablet", isTablet());
        constants.put("fontScale", fontScale());
        constants.put("is24Hour", is24Hour());
        if (ctx.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED ||
                ctx.checkCallingOrSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED ||
                ctx.checkCallingOrSelfPermission("android.permission.READ_PHONE_NUMBERS") == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            constants.put("phoneNumber", telMgr.getLine1Number());
        }
        constants.put("carrier", getCarrier());
        constants.put("totalDiskCapacity", getTotalDiskCapacity());
        constants.put("freeDiskStorage", getFreeDiskStorage());
        constants.put("installReferrer", getInstallReferrer());

        Runtime rt = Runtime.getRuntime();
        constants.put("maxMemory", rt.maxMemory());
        ActivityManager actMgr = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actMgr.getMemoryInfo(memInfo);
        constants.put("totalMemory", memInfo.totalMem);

        return constants;
    }

    private String getCurrentLanguage(Context ctx) {
        Locale current = ctx.getResources().getConfiguration().locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return current.toLanguageTag();
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(current.getLanguage());
            if (current.getCountry() != null) {
                builder.append("-");
                builder.append(current.getCountry());
            }
            return builder.toString();
        }
    }

    private String getCurrentCountry() {
        Context ctx = MyApplication.getContext();
        Locale current = ctx.getResources().getConfiguration().locale;
        return current.getCountry();
    }

    private Boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    private Boolean isTablet() {
        Context ctx = MyApplication.getContext();
        int layout = ctx.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (layout != Configuration.SCREENLAYOUT_SIZE_LARGE && layout != Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            return false;
        }

        final DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
                || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
                || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
                || metrics.densityDpi == DisplayMetrics.DENSITY_TV
                || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
            return true;
        }
        return false;
    }

    private float fontScale() {
        Context ctx = MyApplication.getContext();
        return ctx.getResources().getConfiguration().fontScale;
    }

    private Boolean is24Hour() {
        Context ctx = MyApplication.getContext();
        return android.text.format.DateFormat.is24HourFormat(ctx);
    }


    public String getCarrier() {
        Context ctx = MyApplication.getContext();
        TelephonyManager telMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        return telMgr.getNetworkOperatorName();
    }


    public Integer getTotalDiskCapacity() {
        try {
            StatFs root = new StatFs(Environment.getRootDirectory().getAbsolutePath());
            return root.getBlockCount() * root.getBlockSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Integer getFreeDiskStorage() {
        try {
            StatFs external = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            return external.getAvailableBlocks() * external.getBlockSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getInstallReferrer() {
        Context ctx = MyApplication.getContext();
        SharedPreferences sharedPref = ctx.getSharedPreferences("react-native-device-info", Context.MODE_PRIVATE);
        return sharedPref.getString("installReferrer", null);
    }

}

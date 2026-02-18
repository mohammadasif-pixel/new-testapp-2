package com.plugin.installedapps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstalledApps extends CordovaPlugin {

    private static final Set<String> UPI_PACKAGES = new HashSet<>(Arrays.asList(
            "com.phonepe.app",
            "com.google.android.apps.nbu.paisa.user",
            "net.one97.paytm",
            "in.org.npci.upiapp",
            "in.amazon.mShop.android.shopping",
            "com.whatsapp",
            "com.dreamplug.androidapp",
            "com.mobikwik_new",
            "com.freecharge.android",
            "com.myairtelapp",
            "com.jio.jiopay"));

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getInstalledApps")) {
            this.getInstalledApps(callbackContext);
            return true;
        } else if (action.equals("getAppInfo")) {
            String packageName = args.getString(0);
            this.getAppInfo(packageName, callbackContext);
            return true;
        }
        return false;
    }

    private void getInstalledApps(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    PackageManager pm = cordova.getActivity().getPackageManager();
                    List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_META_DATA);
                    JSONArray appsArray = new JSONArray();

                    for (PackageInfo packageInfo : packages) {
                        // Skip system apps if needed (optional)
                        // if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        // continue;
                        // }

                        JSONObject appInfo = getAppInfoObject(packageInfo, pm);
                        if (appInfo != null) {
                            appsArray.put(appInfo);
                        }
                    }

                    callbackContext.success(appsArray);
                } catch (Exception e) {
                    callbackContext.error("Error getting installed apps: " + e.getMessage());
                }
            }
        });
    }

    private void getAppInfo(final String packageName, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    PackageManager pm = cordova.getActivity().getPackageManager();
                    PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
                    JSONObject appInfo = getAppInfoObject(packageInfo, pm);

                    if (appInfo != null) {
                        callbackContext.success(appInfo);
                    } else {
                        callbackContext.error("App not found");
                    }
                } catch (Exception e) {
                    callbackContext.error("Error getting app info: " + e.getMessage());
                }
            }
        });
    }

    private JSONObject getAppInfoObject(PackageInfo packageInfo, PackageManager pm) {
        try {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            JSONObject app = new JSONObject();

            // Basic info
            app.put("packageName", packageInfo.packageName);
            app.put("appName", appInfo.loadLabel(pm).toString());
            app.put("versionName", packageInfo.versionName);
            app.put("versionCode", packageInfo.versionCode);

            // System app check
            boolean isSystemApp = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            app.put("isSystemApp", isSystemApp);

            if (!UPI_PACKAGES.contains(packageInfo.packageName)) {
                return null;
            }

            // Install time
            app.put("firstInstallTime", packageInfo.firstInstallTime);
            app.put("lastUpdateTime", packageInfo.lastUpdateTime);

            // Get app icon as file path
            try {
                Drawable icon = pm.getApplicationIcon(appInfo);
                String iconPath = saveIconToFile(icon, packageInfo.packageName);
                String iconBase64 = drawableToBase64(icon);
                app.put("icon", iconBase64);
            } catch (Exception e) {
                app.put("icon", "");
            }

            return app;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String saveIconToFile(Drawable drawable, String packageName) {
        try {
            // Get cache directory
            File cacheDir = cordova.getActivity().getCacheDir();
            File iconsDir = new File(cacheDir, "installed_apps_icons");
            if (!iconsDir.exists()) {
                iconsDir.mkdirs();
            }

            File iconFile = new File(iconsDir, packageName + ".png");

            // Optimization: If file already exists, return it.
            // Note: If you want to handle app updates (icon changes), you might need to
            // check last modified time
            // or force update. For now, assuming persistent icons for performance.
            if (iconFile.exists()) {
                return "file://" + iconFile.getAbsolutePath();
            }

            Bitmap bitmap;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }

            FileOutputStream out = new FileOutputStream(iconFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            return "file://" + iconFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String drawableToBase64(Drawable drawable) {
        try {
            Bitmap bitmap;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP);
        } catch (Exception e) {
            return "";
        }
    }
}

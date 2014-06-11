package com.nj.simba;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


/**
 * Collect all information of device
 * @author Honey
 */
public class DviceInfoCollector extends IDeviceInfo {
    private static final String TAG = "SmartTools";
    private Context mContext;
    private PackageManager mPm;
    
    public DviceInfoCollector(Context context) {
        mContext = context;
        mPm = mContext.getPackageManager();
        
        Log.d(TAG, "start collect!");
        initMemInfo();
        initDeviceFeature();
        initAppsInfo();
        Log.d(TAG, "end collect!");
    }

    private void initMemInfo() {
        mMemInfo = new MemInfo();
        
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        
        mMemInfo.ramTotal = getMemTotal();
        mMemInfo.ramAvail = mi.availMem;
        
        long[] rom = getPartitionSize(Environment.getDataDirectory());
        mMemInfo.romTotal = rom[0];
        mMemInfo.romAvail = rom[1];
        
        long[] sd1 = getPartitionSize(Environment.getExternalStorageDirectory());
        mMemInfo.sd0Total = sd1[0];
        mMemInfo.sd0Avail = sd1[1];
        mMemInfo.sd0Path = Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private void initDeviceFeature() {
        mFeatures = new DeviceFeature();
        getDisplayInfo();
    }

    private void getDisplayInfo() {
        WindowManager winMgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = winMgr.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        mFeatures.lcdDensity = metrics.density;
        mFeatures.lcdWidth = metrics.widthPixels;
        mFeatures.lcdHeight = metrics.heightPixels;
        
        mFeatures.androidVersion = android.os.Build.VERSION.SDK_INT;
        mFeatures.imageVersion = android.os.Build.DISPLAY;
        mFeatures.deviceName =  android.os.Build.MODEL+"(" + android.os.Build.PRODUCT + ")";
    }

    private long[] getPartitionSize(File path) {
        long[] size = new long[] { 0, 0 };
        
        StatFs statfs = new StatFs(path.getPath());

        long blocSize = statfs.getBlockSize();
        long totalBlocks = statfs.getBlockCount();
        long availaBlock = statfs.getAvailableBlocks();   
        
        long allSize = (blocSize * totalBlocks) >> 10;
        long availableSize = blocSize * availaBlock>> 10;
        
        size[0] = allSize;
        size[1] = availableSize;
        
        return size;
    }
    
    private long getMemTotal() {
        String line = null;
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader("/proc/meminfo"), 64);
            line = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        int begin = line.indexOf(':');
        int end = line.indexOf('k');
        line = line.substring(begin + 1, end).trim();

        return Integer.parseInt(line);
    }
    
    private void initAppsInfo() {
        Log.d(TAG, "initAppsInfo!");
        Set<Signature> wellKnowns = null;
        
        try {
            wellKnowns = getWellKnownSignatures();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runnings = am.getRunningAppProcesses();

        List<PackageInfo> pkgInfoList = mPm
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                        | PackageManager.GET_SIGNATURES);

        mAppsInfo = new AppInfo[pkgInfoList.size()];

        for (int i = 0; i < pkgInfoList.size(); i++) {
            PackageInfo pkgInfo = pkgInfoList.get(i);
            mAppsInfo[i] = new AppInfo();
            
            mAppsInfo[i].name = pkgInfo.applicationInfo.loadLabel(mPm).toString();
            // Log.d(TAG, pkgInfo.applicationInfo.name); // cause exception

            mAppsInfo[i].pkgName = pkgInfo.packageName;
            mAppsInfo[i].pkgPath = pkgInfo.applicationInfo.sourceDir;
            mAppsInfo[i].versionName = pkgInfo.versionName==null?"N/A":pkgInfo.versionName;
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            mAppsInfo[i].installDate = sdf.format(new Date(pkgInfo.firstInstallTime));
            Log.d(TAG, mAppsInfo[i].installDate);
            
            mAppsInfo[i].isCtsPass = ctsCheck(pkgInfo, wellKnowns);
            
            mAppsInfo[i].isSystem = (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            mAppsInfo[i].isRunning = isPkgRunning(pkgInfo.packageName, runnings);
            
            mAppsInfo[i].icon = getIcon(pkgInfo);
            
            mAppsInfo[i].size = (int) new File(mAppsInfo[i].pkgPath).length();
            mAppsInfo[i].uid = pkgInfo.applicationInfo.uid;
        }
    }

    private byte[] getIcon(PackageInfo pkgInfo) {
        byte[] icon = null;
        Drawable iconDrawble = pkgInfo.applicationInfo.loadIcon(mPm);
        
        if (iconDrawble == null) {
            icon = bitmap2Bytes(imageScale(
                    drawableToBitmap(mContext.getResources().getDrawable(R.drawable.ic_launcher)), 24, 24));
        } else {
            icon = bitmap2Bytes(imageScale(
                    drawableToBitmap(iconDrawble), 24, 24));
            // info.icon = bitmap2Bytes(drawableToBitmap(iconDrawble));
        }

        return icon;
    }

    private boolean ctsCheck(PackageInfo pkgInfo, Set<Signature> wellKnowns) {
        boolean isCtsOk = true;
        
        for (Signature signature : pkgInfo.signatures) {
            if (wellKnowns.contains(signature)) {
                isCtsOk = false;
                Log.d(TAG, "isCtsPass is false:" + pkgInfo.packageName);
                break;
            }
        }

        if ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE) {
            isCtsOk = false;
        }

        return isCtsOk;
    }

    public boolean isPkgRunning(String PackageName, List<RunningAppProcessInfo> runnings) {
        boolean isRunning = false;

        for (ActivityManager.RunningAppProcessInfo run : runnings) {
            if (run.processName.equals(PackageName)) {
                isRunning = true;
                break;
            }
        }
   
        return isRunning;
    }

    public static Bitmap imageScale(Bitmap bitmap, int dstW, int dstH) {
        Matrix matrix = new Matrix();

        int srcW = bitmap.getWidth();
        int srcH = bitmap.getHeight();

        float scale_w = ((float) dstW) / srcW;
        float scale_h = ((float) dstH) / srcH;

        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, srcW, srcH, matrix,
                true);

        return dstbmp;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @SuppressWarnings("unused")
    private Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    private Set<Signature> getWellKnownSignatures() throws NotFoundException, IOException {
        Set<Signature> wellKnownSignatures = new HashSet<Signature>();
        wellKnownSignatures.add(getSignature(R.raw.sig_media));
        wellKnownSignatures.add(getSignature(R.raw.sig_platform));
        wellKnownSignatures.add(getSignature(R.raw.sig_shared));
        wellKnownSignatures.add(getSignature(R.raw.sig_testkey));
        wellKnownSignatures.add(getSignature(R.raw.sig_devkeys));
        wellKnownSignatures.add(getSignature(R.raw.sig_devkeys_media));
        wellKnownSignatures.add(getSignature(R.raw.sig_devkeys_platform));
        wellKnownSignatures.add(getSignature(R.raw.sig_devkeys_shared));
        return wellKnownSignatures;
    }

    private Signature getSignature(int resId) throws NotFoundException, IOException {
        InputStream input = mContext.getResources().openRawResource(resId);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[1024 * 4];
            int numBytes = 0;
            while ((numBytes = input.read(buffer)) != -1) {
                output.write(buffer, 0, numBytes);
            }
            return new Signature(output.toByteArray());
        } finally {
            input.close();
            output.close();
        }
    }
}


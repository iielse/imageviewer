package ch.ielse.view.imagecropper;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * https://github.com/yanzhenjie/AndPermission/blob/master/permission/src/main/java/com/yanzhenjie/permission/AndPermission.java
 */
public class PermissionUtils {

    public static boolean hasPermission(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        for (String permission : permissions) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager
                    .PERMISSION_GRANTED);
            if (!hasPermission) return false;
        }
        return true;
    }
}

package ch.ielse.demo.p01;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/yanzhenjie/AndPermission/blob/master/permission/src/main/java/com/yanzhenjie/permission/AndPermission.java
 * 参考AndPermission
 * <p>
 * 对处理android6.0动态权限的需要任务，提供了简单的API封装
 */
public class PermissionUtils {
    static int PERMISSION_CAMERA = 102;
    static int PERMISSION_EXTERNAL_STORAGE = 103;

    private static final String TAG = "PermissionUtils";

    /**
     * 是否拥有权限
     */
    public static boolean hasPermission(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        for (String permission : permissions) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager
                    .PERMISSION_GRANTED);
            if (!hasPermission) return false;
        }
        return true;
    }

    /**
     * 请求权限
     * <pre>
     * if (!PermissionUtils.hasPermission()) {
     *         PermissionUtils.requestPermissions();
     * }
     * </pre>
     */
    public static void requestPermissions(Object o, int requestCode, String... permissions) {
        final Context context = getContext(o);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            final int[] grantResults = new int[permissions.length];
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            final int permissionCount = permissions.length;
            for (int i = 0; i < permissionCount; i++) {
                grantResults[i] = packageManager.checkPermission(permissions[i], packageName);
            }
            dispatchRequestPermissionsResult(o, requestCode, permissions, grantResults);
        } else {
            List<String> deniedList = new ArrayList<>();
            for (String permission : permissions) {
                if (!hasPermission(context, permission)) {
                    deniedList.add(permission);
                }
            }
            String[] deniedPermissions = deniedList.toArray(new String[deniedList.size()]);
            // Denied permissions size > 0.
            if (deniedPermissions.length > 0) {
                // Remind users of the purpose of permissions.
                requestPermissionsFromSystem(o, requestCode, deniedPermissions);
            } else { // All permission granted.
                final int[] grantResults = new int[permissions.length];
                final int permissionCount = permissions.length;
                for (int i = 0; i < permissionCount; i++) {
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;
                }
                dispatchRequestPermissionsResult(o, requestCode, permissions, grantResults);
            }
        }
    }

    /**
     * 请求权限的结果
     * <pre>
     * @Override
     * public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
     *     PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionUtils.Callback() {
     *         @Override
     *         public void onResult(int requestCode, List<String> grantPermissions, List<String> deniedPermissions) {
     *             if(PermissionUtils.hasAlwaysDeniedPermission(deniedPermissions)) {
     *
     *             } else {
     *             }
     *         }
     *     });
     * }
     * </pre>
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Callback callback) {
        List<String> grantedList = new ArrayList<>();
        List<String> deniedList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                grantedList.add(permissions[i]);
            else
                deniedList.add(permissions[i]);
        }
        callback.onResult(requestCode, grantedList, deniedList);
    }

    /**
     * 权限被用户永久禁止不再提示了？
     */
    public static boolean hasAlwaysDeniedPermission(Object o, List<String> deniedPermissions) {
        final Context context = getContext(o);
        for (String deniedPermission : deniedPermissions) {
            if (!PermissionUtils.shouldShowRationalePermissions(context, deniedPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 权限被拒绝了，有必要解释下为什么需要权限。如果被用户永久禁止不再提示了，这里会返回false
     */
    public static boolean shouldShowRationalePermissions(Object o, String... permissions) {
        if (Build.VERSION.SDK_INT < 23) return false;
        boolean rationale = false;
        for (String permission : permissions) {
            if (o instanceof Activity) {
                rationale = ActivityCompat.shouldShowRequestPermissionRationale((Activity) o, permission);
            } else if (o instanceof android.support.v4.app.Fragment) {
                rationale = ((android.support.v4.app.Fragment) o).shouldShowRequestPermissionRationale(permission);
            } else if (o instanceof android.app.Fragment) {
                rationale = ((android.app.Fragment) o).shouldShowRequestPermissionRationale(permission);
            }
            if (rationale) return true;
        }
        return false;
    }

    public interface Callback {
        void onResult(int requestCode, List<String> grantPermissions, List<String> deniedPermissions);
    }

    private static Context getContext(Object o) {
        if (o instanceof Context) {
            return (Context) o;
        } else if (o instanceof Activity) {
            return (Activity) o;
        } else if (o instanceof Fragment) {
            return ((Fragment) o).getActivity();
        } else if (o instanceof android.app.Fragment) {
            return ((android.app.Fragment) o).getActivity();
        }
        throw new IllegalArgumentException("The " + o.getClass().getName() + " is not support.");
    }

    private static void dispatchRequestPermissionsResult(Object o, int requestCode, String[] permissions, int[] grantResults) {
        if (o instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((Activity) o).onRequestPermissionsResult(requestCode, permissions, grantResults);
            } else if (o instanceof ActivityCompat.OnRequestPermissionsResultCallback) {
                ((ActivityCompat.OnRequestPermissionsResultCallback) o).onRequestPermissionsResult(requestCode, permissions, grantResults);
            } else {
                Log.e(TAG, "The " + o.getClass().getName() + " is not support " + "onRequestPermissionsResult()");
            }
        } else if (o instanceof Fragment) {
            ((Fragment) o).onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else if (o instanceof android.app.Fragment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((android.app.Fragment) o).onRequestPermissionsResult(requestCode, permissions, grantResults);
            } else {
                Log.e(TAG, "The " + o.getClass().getName() + " is not support " + "onRequestPermissionsResult()");
            }
        }
    }

    private static void requestPermissionsFromSystem(Object o, int requestCode, String... permissions) {
        if (o instanceof Activity) {
            ActivityCompat.requestPermissions(((Activity) o), permissions, requestCode);
        } else if (o instanceof Fragment) {
            ((Fragment) o).requestPermissions(permissions, requestCode);
        } else if (o instanceof android.app.Fragment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((android.app.Fragment) o).requestPermissions(permissions, requestCode);
            } else {
                Log.e(TAG, "The " + o.getClass().getName() + " is not support " + "requestPermissions()");
            }
        }
    }

}

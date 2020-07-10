package com.dragclosehelper.example;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * @author Jsonshi
 * @date 2020/4/14
 * <p>
 * Description:
 */
public class ClickUtils {

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;
    public static boolean isFastClick() {
        long now = System.currentTimeMillis();
        if (now - lastClickTime >= 600) {
            lastClickTime = now;
            return false;
        }else{

        }
        return true;
    }


   static long[] mHits = new long[2];
    public static boolean click(){
        System. arraycopy(mHits, 1, mHits, 0, mHits.length-1);
        mHits[ mHits. length-1] = SystemClock. uptimeMillis();
        if ( mHits[0] >= (SystemClock. uptimeMillis()-600)) {
            Log.d("test>>>>你好", "click: "+"连续点击");
            return true;
        }
        Log.d("test>>>>你好", "click: "+"单点击");
        return false;
    }
}

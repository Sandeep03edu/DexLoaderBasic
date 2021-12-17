package com.example.dexproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTag";
    private Class<?> apkActivity;
    private Class<?> apkUtils;
    private Button getData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getData = findViewById(R.id.main_get_string_btn);

        String apkName = "";
        try {
            logClassLoader("start to load apk");
            MyApplication application = (MyApplication) getApplication();
            apkName = "dex.apk";
            application.LoadApk(apkName);
            logClassLoader("apk loaded");

            String pkgName = "";
            pkgName = "com.example.dex.MainActivity";
            apkActivity = getClassLoader().loadClass(pkgName);

            String utilsPkgName= "com.example.dex.Utils";
            apkUtils = getClassLoader().loadClass(utilsPkgName);

//            Toast.makeText(getApplicationContext(), getApkInfo(apkName), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "onCreate: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Oops", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(apkUtils!=null){
                    Method getInputStringStatic = null;
                    try {
                        //Get constructor for not-static method
                        Constructor<?> cons = apkUtils.getConstructor();

                        Method getStringValue = apkUtils.getDeclaredMethod("getData");
                        String returns1 = (String) getStringValue.invoke(cons.newInstance());
                        Toast.makeText(getApplicationContext(), returns1, Toast.LENGTH_SHORT).show();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void logClassLoader(String msg) {
        ClassLoader oldLoader = getClass().getClassLoader();
        int sum = 0;
        try {
            while (oldLoader != null) {
                Log.e(msg + sum, "" + oldLoader);
                sum++;
                oldLoader = oldLoader.getParent();
            }
            Log.e(msg + sum, "" + oldLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove the latest loaded-apk
        ((MyApplication) getApplication()).RemoveApk();
        Log.d(TAG, "onDestroy");
    }
}
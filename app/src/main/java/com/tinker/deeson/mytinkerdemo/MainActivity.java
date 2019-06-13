package com.tinker.deeson.mytinkerdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;
import com.tinker.deeson.mytinkerdemo.TinkerUtils.FileUtils;
import com.tinker.deeson.mytinkerdemo.TinkerUtils.Utils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_load).setOnClickListener(this);
        findViewById(R.id.btn_kill).setOnClickListener(this);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.setBackground(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.setBackground(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_load:
                loadPatch();
            break;
            case R.id.btn_kill:
                killApp();
            break;
        }
    }

    /**
     * 加载热补丁插件
     */
    public void loadPatch() {
        //加载补丁包
        File sourcesFile = new File(Environment.getExternalStorageDirectory(), "patch_signed_7zip.apk");
        File privateFile = new File(getDir("odex", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + "patch_signed_7zip.apk");
        //判断私有目录 是否存在 文件
        if (privateFile.exists()) {
            privateFile.delete();//存在就删除文件
        }

        try {
            //使用封装的方法 把 sd卡里面的dex文件 复制到私有目录里面
            FileUtils.copyFile(sourcesFile, privateFile);
            Toast.makeText(this, "复制私有目录成功", Toast.LENGTH_SHORT).show();
            //修复dex文件
            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), privateFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 杀死应用加载补丁
     */
    public void killApp() {
        ShareTinkerInternals.killAllOtherProcess(getApplicationContext());
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

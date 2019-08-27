package com.example.mix;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.flutter.facade.Flutter;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

public class MainActivity extends AppCompatActivity {

    public static final String FlutterToAndroidCHANNEL = "com.example.toandroid/plugin";
    public static final String AndroidToFlutterCHANNEL = "com.example.toflutter/plugin";


    private TextView mTextView;

    private FrameLayout mFrameLayout;
    private FlutterView mFlutterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.params);

        mFrameLayout = findViewById(R.id.rl_flutter);

        mFlutterView = Flutter.createView(this, getLifecycle(), "route2");
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mFrameLayout.addView(mFlutterView, layoutParams);

        String params = getIntent().getStringExtra("test");
        if (!TextUtils.isEmpty(params)) {
            Toast.makeText(this, "" + params, Toast.LENGTH_SHORT).show();
            mTextView.setText("flutter 传参:" + params);
        }

        // Android 向 Flutter 传参
        new EventChannel(mFlutterView, AndroidToFlutterCHANNEL).setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                String androidParams = "来自android原生的参数";
                eventSink.success(androidParams);
            }

            @Override
            public void onCancel(Object o) {

            }
        });


        // Flutter 调用 Android
        new MethodChannel(mFlutterView, FlutterToAndroidCHANNEL).setMethodCallHandler(new MethodChannel.MethodCallHandler() {

            @Override
            public void onMethodCall(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {

                // 接收来自flutter的指令withoutParams
                if (methodCall.method.equals("withoutParams")) {
                    // 跳转到指定Activity
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);

                    // 返回给flutter的参数
                    result.success("success");
                }
                //接收来自flutter的指令withParams
                else if (methodCall.method.equals("withParams")) {

                    // 解析参数
                    String text = methodCall.argument("flutter");

                    // 带参数跳转到制定Activity
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("test", text);
                    startActivity(intent);

                    // 返回给flutter的参数
                    result.success("success");
                } else {
                    result.notImplemented();
                }
            }
        });
    }
}

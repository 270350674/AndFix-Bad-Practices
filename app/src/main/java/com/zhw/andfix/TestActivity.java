package com.zhw.andfix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.zhw.andfix.model.PatchBean;

/**
 * Created by zhonghw on 2016/3/11.
 */
public class TestActivity extends Activity {

    PatchBean bean;

    public static void launch(Activity activity){
        Intent intent = new Intent(activity, TestActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        /*bean = new PatchBean();
        bean.url = "repair the bug";*/
        /*tetBug();*/

    }

    private void tetBug() {
        Toast.makeText(this, "hello,world!!!", Toast.LENGTH_SHORT).show();
    }
}

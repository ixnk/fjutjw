package com.renen.fjgr.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.renen.fjgr.R;
import com.renen.fjgr.support.JsoupService;
import com.renen.fjgr.support.OkHttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by J。 on 2016/4/29.
 * 修改密码activity
 */
public class UpdatePasswordActivity extends AppCompatActivity {

    private static final String TAG = "UpdatePasswordActivity";

    private Toolbar toolbar;
    private EditText editText_old_password;
    private EditText editText_new_password;
    private EditText editText_new_password2;
    private Button button_ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_password_layout);
        initView();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword(editText_old_password.getText().toString().trim(), editText_new_password.getText().toString().trim(), editText_new_password2.getText().toString().trim());
            }
        });
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        editText_old_password = (EditText) findViewById(R.id.editText_old_password);
        editText_new_password = (EditText) findViewById(R.id.editText_new_password);
        editText_new_password2 = (EditText) findViewById(R.id.editText_new_password2);
        button_ok = (Button) findViewById(R.id.button_update_password_ok);
    }

    /**
     * 执行修改密码
     *
     * @param old_password  旧密码
     * @param new_password  新密码
     * @param new_password2 重复新密码
     */
    private void updatePassword(String old_password, String new_password, String new_password2) {
        if (TextUtils.isEmpty(old_password)) {
            editText_old_password.setError(getString(R.string.update_password_old_password_error));
//            Toast.makeText(UpdatePasswordActivity.this, getString(R.string.update_password_old_password_error), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(new_password)) {
            editText_new_password.setError(getString(R.string.update_password_new_password_error));
//            Toast.makeText(UpdatePasswordActivity.this, getString(R.string.update_password_new_password_error), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(new_password2)) {
            editText_new_password2.setError(getString(R.string.update_password_new_password2_error));
//            Toast.makeText(UpdatePasswordActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!new_password.equals(new_password2)) {
            editText_new_password2.setError(getString(R.string.update_password_password_is_not_same));
//            Toast.makeText(UpdatePasswordActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        //添加请求参数
        RequestBody requestBody = new FormBody.Builder()
                .add("__VIEWSTATE", "dDwtMzg5NzE5MDc3Ozs+D9XpBPpncfl9VzHasb+GdD/ksxg=")
                .add("__VIEWSTATEGENERATOR", "23D32CE9")
                .add("TextBox2", editText_old_password.getText().toString().trim())
                .add("TextBox3", editText_new_password.getText().toString().trim())
                .add("Textbox4", editText_new_password2.getText().toString().trim())
                .add("Button1", "%D0%DE++%B8%C4")
                .build();
        //获取Request对象
        Request request = OkHttpUtil.getRequest(OkHttpUtil.getREFERER() + JsoupService.getLinkMap().get("密码修改"), OkHttpUtil.getREFERER() + JsoupService.getLinkMap().get("密码修改"), requestBody);
        OkHttpUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdatePasswordActivity.this, getString(R.string.update_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String content = new String(response.body().bytes(), "gb2312");
                    final String message = JsoupService.getUpdatePasswordMessage(content);
                    //获得修改后的提示信息
                    if (message != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UpdatePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UpdatePasswordActivity.this, getString(R.string.update_error), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

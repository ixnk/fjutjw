package com.renen.fjgr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.renen.fjgr.R;
import com.renen.fjgr.adapter.ScoreRecyclerAdapter;
import com.renen.fjgr.bean.CourseInfo;
import com.renen.fjgr.support.JsoupService;
import com.renen.fjgr.support.OkHttpUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by J。 on 2016/4/25.
 * 成绩展示Activity
 */
public class ScoreActivity extends AppCompatActivity {
    private Toolbar toolbar;

    /**
     * 显示学年学期
     */
    private TextView textView_score_header;
    private RecyclerView recyclerView_score;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_layout);
        initView();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        //获取intent传递过来的List
        ArrayList<CourseInfo> courseInfoArrayList = (ArrayList<CourseInfo>) intent.getSerializableExtra("score");
        String year = intent.getStringExtra("year");
        String semester = intent.getStringExtra("semester");
        textView_score_header.setText(year + "第" + semester + "学期");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_score.setLayoutManager(linearLayoutManager);
        ScoreRecyclerAdapter scoreRecyclerAdapter = new ScoreRecyclerAdapter(this, courseInfoArrayList);
        recyclerView_score.setAdapter(scoreRecyclerAdapter);

    }

    private void initView() {
        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        this.textView_score_header = (TextView) findViewById(R.id.textView_score_header);
        this.recyclerView_score = (RecyclerView) findViewById(R.id.recyclerView_score);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_grade_exam:
                Intent intent = new Intent();
                intent.setClass(ScoreActivity.this, GradeScoreActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.score_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

package com.renen.fjgr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.renen.fjgr.R;
import com.renen.fjgr.support.JsoupService;

/**
 * Created by J。 on 2016/4/20.
 * 显示课程表界面
 */
public class CourseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private int[][] btn_id = {{R.id.course_1_1, R.id.course_1_2, R.id.course_1_3, R.id.course_1_4, R.id.course_1_5, R.id.course_1_6, R.id.course_1_7},
            {R.id.course_2_1, R.id.course_2_2, R.id.course_2_3, R.id.course_2_4, R.id.course_2_5, R.id.course_2_6, R.id.course_2_7},
            {R.id.course_3_1, R.id.course_3_2, R.id.course_3_3, R.id.course_3_4, R.id.course_3_5, R.id.course_3_6, R.id.course_3_7},
            {R.id.course_4_1, R.id.course_4_2, R.id.course_4_3, R.id.course_4_4, R.id.course_4_5, R.id.course_4_6, R.id.course_4_7},
            {R.id.course_5_1, R.id.course_5_2, R.id.course_5_3, R.id.course_5_4, R.id.course_5_5, R.id.course_5_6, R.id.course_5_7},
            {R.id.course_6_1, R.id.course_6_2, R.id.course_6_3, R.id.course_6_4, R.id.course_6_5, R.id.course_6_6, R.id.course_6_7}};
    /**
     * 课程背景
     */
    private int[] course_bg_id = {
            R.drawable.course_1, R.drawable.course_2, R.drawable.course_3, R.drawable.course_4, R.drawable.course_5, R.drawable.course_6, R.drawable.course_7};
    /**
     * 课程背景随机数
     */
    int course_id_num = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_layout);
        this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String response = intent.getStringExtra("content");
        String[][] course = JsoupService.getCourse(response);
        for (int i = 0; i < course.length; i++) {
            for (int j = 0; j < course[i].length; j++) {
                Button button = (Button) findViewById(btn_id[i][j]);
				boolean mIsLessonByWeek=true;
				if(!(course[i][j].length()<=1)){
					Log.e("zzz",String.valueOf(i)+",,,,"+String.valueOf(j));
					String mLessonByWeek=course[i][j].split("\n")[1];
					//Matcher  m = Pattern.compile("(单|双)").matcher(mLessonByWeek);
					if(mLessonByWeek.lastIndexOf("单")>=0){
						mLessonByWeek= mLessonByWeek.replace("单", "");
						mIsLessonByWeek=(MainActivity.mWeekth)%2==1;
					}else if(mLessonByWeek.lastIndexOf("双")>=0){
						mLessonByWeek=mLessonByWeek.replace("双", "");
						mIsLessonByWeek=(MainActivity.mWeekth)%2==0;
					}
					if(mIsLessonByWeek){
						mIsLessonByWeek=false;
						mLessonByWeek=mLessonByWeek.replaceAll("\\(\\d,\\d\\)","");
						String[] mLessonByWeekArray=mLessonByWeek.split(",");
						for(String mWeekDuration:mLessonByWeekArray){
							if(mWeekDuration.contains("-")){
								String[] mWeekDurationSplit= mWeekDuration.split("-");
								
								if(MainActivity.mWeekth>=Integer.valueOf( mWeekDurationSplit[0] )&& MainActivity.mWeekth<=Integer.valueOf( mWeekDurationSplit[1])){
									mIsLessonByWeek=true;
									break;
								}
								
							}else if(MainActivity.mWeekth==Integer.valueOf( mWeekDuration)){
								mIsLessonByWeek=true;
								break;
							}
						}
						
					}
					
					if(!mIsLessonByWeek){
						course[i][j]="";
					}
				}
				
                button.setText(course[i][j]);
                if (button.getText().length() > 2) {
                    button.setBackgroundResource(course_bg_id[course_id_num]);
                    //如果等于最大值，就从0从新开始，否则递增
                    course_id_num = (course_id_num < course_bg_id.length - 1) ? course_id_num + 1 : 0;
                }

            }
        }
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

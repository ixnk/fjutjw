package com.renen.fjgr.ui;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.renen.fjgr.*;
import com.renen.fjgr.support.*;
import java.io.*;
import java.util.*;
import okhttp3.*;

import android.support.v7.widget.Toolbar;
import org.jsoup.*;
import org.jsoup.select.*;
import org.jsoup.nodes.*;
import android.view.View.*;


public class CoursesListActivity extends AppCompatActivity
{


	private ListView lv_courses;
	private ArrayAdapter<String> adapter;
	private Map<String, String> CoursesMap;

	private Toolbar toolbar;
	
	private String submitIsDisabled;
	private Button btn_bottom;
	
	private List<Object> FormDatas;
	
	private String c;
    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
		
		 
		
		this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.login_activity_name));
		
		Intent intent = getIntent();
        CoursesMap = (Map<String, String>) intent.getSerializableExtra("CoursesMap");
		c=getFirstOrNull(CoursesMap);
		btn_bottom=(Button) findViewById(R.id.btn_buttom);
		lv_courses = (ListView)findViewById(R.id.list);
	    adapter = new ArrayAdapter<String>(CoursesListActivity.this, android.R.layout.simple_list_item_1);

		lv_courses.setAdapter(adapter);

		for (String CourseName : CoursesMap.keySet())
		{            
		    // System.out.println("Value = " + value);        
			adapter.add(CourseName);
		} 
		
		btn_bottom.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					getSubmitIsEnabled();
					if(submitIsDisabled.equals("disabled")){
						System.out.println("err");
						//Looper.prepare();
						//Toast.makeText(CoursesListActivity.this,"err",Toast.LENGTH_LONG).show();
						//Looper.loop();
					}else{
						System.out.println(".");
						EvaluateCourseActivity.evaluateCourse(OkHttpUtil.getREFERER()+c,FormDatas);
					}
				}
			
		});
		lv_courses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					//String CoursePath=adapter.getItem(position);
					//Log.e("zzj",String.valueOf(position));
					String CoursePath=CoursesMap.get(adapter.getItem(position));
					//Map<String,String> CourseLink=getEvaluateForm(OkHttpUtil.getREFERER() + CoursePath);
					startEvaluateActivity(CoursePath);
					
				}
			});
			
    }
	
	private void startEvaluateActivity(final String CoursePath){
		
		Request request = OkHttpUtil.getRequest(OkHttpUtil.getREFERER()+ CoursePath);
		OkHttpUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e)
				{
					System.out.println("err");
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException
				{

					List<Object> FormDatas;
						Message message = Message.obtain();
						if (response.code() == 200)
						{
							String content = new String(response.body().bytes(), "gb2312");
							FormDatas = JsoupService.parseEvaluateParam(content);

							Intent intent = new Intent();
							intent.setClass(CoursesListActivity.this, EvaluateCourseActivity.class);
							intent.putExtra("EvaluatingSubmitLink",(Serializable) (OkHttpUtil.getREFERER()+ CoursePath));
							intent.putExtra("FormDatas",(Serializable) FormDatas);
							startActivity(intent);

						}else{
							message.obj = getString(R.string.login_error);

						}

				}
			});
	}
	
	
	private void getSubmitIsEnabled(){
		
		
		Request request = OkHttpUtil.getRequest(OkHttpUtil.getREFERER()+ c);
		
		OkHttpUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e)
				{
					System.out.println("err");
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException
				{
						Message message = Message.obtain();
						if (response.code() == 200)
						{
							String content = new String(response.body().bytes(), "gb2312");
					         FormDatas=JsoupService.parseEvaluateParam(content);
							submitIsDisabled = (String) FormDatas.get(3);

						}else{
							message.obj = getString(R.string.login_error);
						}
				}
			});

	}
	
	public static <K, V> V getFirstOrNull(Map<K, V> map) {
        V obj = null;
		
		for (V v: map.values())
		{            
		    
			obj = v;
            if (obj != null) {
                break;
            }
		} 
      
        return obj;
    }
	
	

}



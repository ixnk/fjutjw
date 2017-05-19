package com.renen.fjgr.ui;
import android.content.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v7.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.ListView;
import com.renen.fjgr.*;
import com.renen.fjgr.adapter.*;
import com.renen.fjgr.support.*;
import java.io.*;
import java.net.*;
import java.util.*;
import okhttp3.*;
import android.widget.Button;


import android.support.v7.widget.Toolbar;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.*;

public class EvaluateCourseActivity extends AppCompatActivity{
	private ListView EvaluateContents;
	private List<Object> FormDatas;
	private Map<String,String> hidenMap;
	private List<String> optionList;
	private Button btn_buttom;
	//private String mCourse;
	private String EvaluatingSubmitLink;

	private Toolbar toolbar;
	EditText et_suggestion;
	@Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
		this.toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        setSupportActionBar(toolbar);
		// getSupportActionBar().setTitle(getResources().getString(R.string.login_activity_name));

		Intent intent = getIntent();
        hidenMap =  (Map<String, String>) ((List<Object>) intent.getSerializableExtra("FormDatas")).get(0);
		optionList = (List<String>) ((List<Object>) intent.getSerializableExtra("FormDatas")).get(1);
		String suggestion=(String)((List<Object>) intent.getSerializableExtra("FormDatas")).get(2);
		
		EvaluatingSubmitLink =  (String) intent.getSerializableExtra("EvaluatingSubmitLink");


		//mCourse=optionList.get(0);


		EvaluateContents = (ListView)findViewById(R.id.list);  

		FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams
		(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		et_suggestion = new EditText(this);
		et_suggestion.setText(suggestion);
		EvaluateContents.addFooterView(et_suggestion);

        EvaluateContents.setAdapter(new EvaluateAdapter(this, optionList));

		btn_buttom = (Button) findViewById(R.id.btn_buttom);
		btn_buttom.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v){
					// TODO: Implement this method
					//if( ((EvaluateAdapter) EvaluateContents.getAdapter()).getUnEvaluaion()!=0)
					//System.out.println("");

					FormBody.Builder mFormBodyBuilder = new FormBody.Builder();
					//StringBuffer sb = new StringBuffer();
					for (String FormParam : hidenMap.keySet()){            

						mFormBodyBuilder.add(FormParam, hidenMap.get(FormParam));
						//sb.append("&"+FormParam+"="+hidenMap.get(FormParam));
					} 
					//mFormBodyBuilder.add("pjkc",mCourse);
					//sb.append("&pjkc="+mCourse);
					int n=2;


					try{
						int mDegreeNum[]={0,0,0,0,0};
						for (int i=0;i < 10;i++){
							//for(String FormData : ((EvaluateAdapter)((HeaderViewListAdapter)EvaluateContents.getAdapter()).getWrappedAdapter()).getEvaluateData() ){
							//DataGrid1:_ctl2:JS1
							//DataGrid1:_ctl2:txtjs1
							String FormData= ((EvaluateAdapter)((HeaderViewListAdapter)EvaluateContents.getAdapter()).getWrappedAdapter()).getEvaluateData().get(i);
							switch (FormData){
								case "优秀":
									mDegreeNum[0]++;
									break;
								case "良好":
									mDegreeNum[1]++;
									break;
								case "中等":
									mDegreeNum[2]++;
									break;
								case "及格":
									mDegreeNum[3]++;
									break;
								case "不及格":
									mDegreeNum[4]++;
									break;
								default:
									Toast.makeText(EvaluateCourseActivity.this, "请完成所有评价", Toast.LENGTH_LONG).show();
									return;
							}
							if(i==9){
								if( mDegreeNum[0]==10 ||
									mDegreeNum[1]==10 ||
									mDegreeNum[2]==10 ||
									mDegreeNum[3]==10 ||
									mDegreeNum[4]==10 ){
										Toast.makeText(EvaluateCourseActivity.this,"不能所有评价都一样",Toast.LENGTH_LONG).show();
										return;
									}
							}



							mFormBodyBuilder.addEncoded("DataGrid1:_ctl" + String.valueOf(n) + ":JS1", URLEncoder.encode(FormData, "GBK"));
							mFormBodyBuilder.addEncoded("DataGrid1:_ctl" + String.valueOf(n++) + ":txtjs1", "");
							//sb.append("&DataGrid1:_ctl" +String.valueOf(n)+":JS1"+"="+FormDatas);
							//sb.append("&DataGrid1:_ctl" +String.valueOf(n++)+":txtJS1"+"=");
						}

						if (et_suggestion.getText().toString().equals("")){
							mFormBodyBuilder.add("pjxx", "");}else{
							mFormBodyBuilder.add("pjxx", URLEncoder.encode(et_suggestion.getText().toString()));
						}
						mFormBodyBuilder.add("txt1", "");
						mFormBodyBuilder.add("TextBox1", "0");


						mFormBodyBuilder.addEncoded("Button1", URLEncoder.encode("保  存", "GBK"));
						//mFormBodyBuilder.addEncoded("Button2",URLEncoder.encode(" 先完成评价，再提交 ","GBK"));
					}
					catch (UnsupportedEncodingException e){}

					//sb.append("&pjxx=&txt1=&TextBox1=0&Button1=保  存");

					RequestBody requestBody=mFormBodyBuilder.build();

					//RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=gb2312"), sb.toString());
					Request request = OkHttpUtil.getRequest(EvaluatingSubmitLink , requestBody);
					OkHttpUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
							@Override
							public void onFailure(Call call, IOException e){
								System.out.println("err");
							}

							@Override
							public void onResponse(Call call, Response response) throws IOException{

								List<Object> FormDatas;
								try{
									Message message = Message.obtain();
									if (response.code() == 200){
										String content = new String(response.body().bytes(), "gb2312");
										FormDatas = JsoupService.parseEvaluateParam(content);
										

										Looper.prepare();
										Toast.makeText(EvaluateCourseActivity.this,"评价成功",Toast.LENGTH_SHORT).show();
										Looper.loop();
										
									}else{
										message.obj = getString(R.string.login_error);

									}

								}
								catch (Exception e){
									e.printStackTrace();
								}
								finally{
									//progressDialog.dismiss();
								}
							}
						});
				}
			});

	}

	public class OnItemSelectedCallback implements Spinner.OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4){
			// TODO: Implement this method
		}

		@Override
		public void onNothingSelected(AdapterView<?> p1){
			// TODO: Implement this method
		}


	}
	
	public static void evaluateCourse(String EvaluatingSubmitLink,List<Object> FormDatas){
		
		Map<String,String> hidenMap =  (Map<String, String>) FormDatas.get(0);
		List<String>  optionList = (List<String>) FormDatas.get(1);
		String suggestion=(String) FormDatas.get(2);
		FormBody.Builder mFormBodyBuilder = new FormBody.Builder();
		
		for (String FormParam : hidenMap.keySet()){            

			mFormBodyBuilder.add(FormParam, hidenMap.get(FormParam));
			
		} 
		
		
		try{
			int n=2;
			for (int i=0;i < 10;i++){
				mFormBodyBuilder.addEncoded("DataGrid1:_ctl" + String.valueOf(n) + ":JS1", URLEncoder.encode(optionList.get(i), "GBK"));
				mFormBodyBuilder.addEncoded("DataGrid1:_ctl" + String.valueOf(n++) + ":txtjs1", "");
				
			}

			mFormBodyBuilder.add("pjxx", URLEncoder.encode(suggestion));
			
			mFormBodyBuilder.add("txt1", "");
			mFormBodyBuilder.add("TextBox1", "0");


			//mFormBodyBuilder.addEncoded("Button1", URLEncoder.encode("保  存", "GBK"));
			mFormBodyBuilder.addEncoded("Button2",URLEncoder.encode(" 先完成评价，再提交 ","GBK"));
		}
		catch (UnsupportedEncodingException e){}

		RequestBody requestBody=mFormBodyBuilder.build();

		//RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=gb2312"), sb.toString());
		Request request = OkHttpUtil.getRequest(EvaluatingSubmitLink , requestBody);
		OkHttpUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e){
					System.out.println("err");
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException{

					List<Object> FormDatas;
					try{
						Message message = Message.obtain();
						if (response.code() == 200){
							String content = new String(response.body().bytes(), "gb2312");
							FormDatas = JsoupService.parseEvaluateParam(content);

							System.out.println(content);
							Looper.prepare();
							//Toast.makeText(EvaluateCourseActivity.this,"评价成功",Toast.LENGTH_SHORT).show();
							Looper.loop();

						}else{
							//message.obj = getString(R.string.login_error);

						}

					}
					catch (Exception e){
						e.printStackTrace();
					}
					finally{
						//progressDialog.dismiss();
					}
				}
			});
	}

}

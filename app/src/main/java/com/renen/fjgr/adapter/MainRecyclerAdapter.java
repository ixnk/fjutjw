package com.renen.fjgr.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.renen.fjgr.R;
import com.renen.fjgr.bean.CourseInfo;
import com.renen.fjgr.holder.SimpleItemHolder;
import com.renen.fjgr.support.JsoupService;
import com.renen.fjgr.support.OkHttpUtil;
import com.renen.fjgr.ui.CourseActivity;
import com.renen.fjgr.ui.CoursesListActivity;
import com.renen.fjgr.ui.ScoreActivity;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;



/**
 * Created by J。 on 2016/4/18.
 * MainActivity的Recycler适配器
 */
public class MainRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener , OnItemSelectedListener {

	@Override
	public void onItemSelected(final AdapterView<?> parent, View v, final int pos, long p4){
		// TODO: Implement this method
		spinnerInitNum++;
		if(spinnerInitNum<=6) return;
		
		FormBody.Builder mFormBodyBuilder = new FormBody.Builder();
		mFormBodyBuilder
			//.add("__EVENTTARGET", baseParam.get(0))
			//.add("__EVENTARGUMENT", baseParam.get(1))
			.add("__EVENTARGUMENT", "")
			.add("__EVENTTARGET", "kb")
			
			.add("__VIEWSTATE", mFormSelectedParam.get("__VIEWSTATE"));
		
		if(parent.getId() == R.id.spinner_academy || parent.getId() == R.id.spinner_mojar || parent.getId()==R.id.spinner_class){
			for(int i=0;i<6;i++){
				mFormBodyBuilder.add(param[i],mFormParamVal.get(param[i]).get(spinnerArray[i].getSelectedItemPosition()));
			}
			
			//String s=CourseParamVal.get("xy").get(spinnerArray[3].getSelectedItemPosition());

			String Referer = OkHttpUtil.encodeUrl(OkHttpUtil.getREFERER() + linkMap.get("课表查询"));
			
			
			
			Request request = new Request.Builder().url(Referer).addHeader("Host", "jw.fjuts.com:8000").addHeader("Content-Type","application/x-www-form-urlencoded").addHeader("Referer", Referer).post(mFormBodyBuilder.build()).build();
			//Request request = OkHttpUtil.getRequest(Referer,Referer, mFormBodyBuilder.build());
			//dialogShow(context.getString(R.string.isLoddingData), false);
			
			OkHttpUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						Log.v(TAG, "课表查询  --> onFailure  --> " + e.getMessage());
						progressDialog.dismiss();

						Message message = Message.obtain();
						message.obj = context.getString(R.string.get_data_error);
						handler.sendMessage(message);
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						Message message = Message.obtain();
						try {
							if (response.code() == 200) {
								String content = new String(response.body().bytes(), "gb2312");
								
								LogUtil.e("zzzzzi",content);
								map = JsoupService.getCourseTableParam(content);
								mFormParamVal = JsoupService.getCourseTableParamVal(content);
								mFormSelectedParam=JsoupService.getCurrentCourseTableParam(content);
								//Log.e("zz",map.toString());
								mCouseTableWebContent=content;
								((Activity)context). runOnUiThread(new Runnable(){

										@Override
										public void run() {
											switch(parent.getId()){
												case R.id.spinner_academy:
													//ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, (List<String>) map.get("zy"));
													//spinnerArray[4].setAdapter(arrayAdapter);
													((ArrayAdapter) spinnerArray[4].getAdapter()).clear();
													((ArrayAdapter) spinnerArray[4].getAdapter()).addAll( map.get("zy"));
													((ArrayAdapter) spinnerArray[4].getAdapter()).notifyDataSetChanged();
/*
													ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, (List<String>) map.get("kb"));
													spinnerArray[5].setAdapter(arrayAdapter2);
													((ArrayAdapter) spinnerArray[5].getAdapter()).notifyDataSetChanged();
*/
												case R.id.spinner_mojar:
													//case R.id.spinner_class:
													((ArrayAdapter) spinnerArray[5].getAdapter()).clear();
													((ArrayAdapter) spinnerArray[5].getAdapter()).addAll( map.get("kb"));
													((ArrayAdapter) spinnerArray[5].getAdapter()).notifyDataSetChanged();

													
													break;
											}
										}
									});
								
								
							} else {
								message.obj = context.getString(R.string.get_data_error);
								handler.sendMessage(message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							message.obj = context.getString(R.string.get_data_error);
							handler.sendMessage(message);
						} finally {
							progressDialog.dismiss();
						}
						Log.v(TAG, "课表查询  --> onResponse  --> response.code = " + response.code());
					}
				});
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> p1){
		// TODO: Implement this method
	}

    private static final String TAG = "MainRecyclerAdapter";
    private Context context;
    /**
     * 布局加载器
     */
    private LayoutInflater layoutInflater;
    /**
     * 菜单名称和链接的集合,key为title,value为链接
     */
    private Map<String, String> linkMap;
    private Handler handler;
    private ProgressDialog progressDialog;
	private Map<String,String> mFormSelectedParam;
	private Map<String,List<String>> mFormParamVal;
	
	private int spinnerInitNum=0;
	private Spinner spinnerArray[]=new Spinner[6];
	public final String param[] ={"xn","xq","nj","xy","zy","kb"};
	private Map<String, List<String>> map;
	private String mCouseTableWebContent;
	
    public MainRecyclerAdapter(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        layoutInflater = LayoutInflater.from(context);
        progressDialog = new ProgressDialog(context);
    }

    public Map<String, String> getLinkMap() {
        return linkMap;
    }

    public void setLinkMap(Map<String, String> linkMap) {
        this.linkMap = linkMap;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SimpleItemHolder simpleItemHolder = new SimpleItemHolder(layoutInflater.inflate(R.layout.main_simple_item_layout, parent, false));
        
        simpleItemHolder.itemView.setOnClickListener(this);
        return simpleItemHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (position) {
            case 2:
                ((SimpleItemHolder) holder).getTextView_title().setText("课表查询");
                ((SimpleItemHolder) holder).getTextView_content().setText("课表在手，天下我有，妈妈再也不用担心我缺课啦");
                ((SimpleItemHolder) holder).getImageView_titleImage().setImageResource(R.drawable.test_1);
                holder.itemView.setTag(position);
                break;
            case 0:
                ((SimpleItemHolder) holder).getTextView_title().setText("成绩查询");
                ((SimpleItemHolder) holder).getTextView_content().setText("挂不挂科，点我就知，再也不用怕查不到成绩啦");
                ((SimpleItemHolder) holder).getImageView_titleImage().setImageResource(R.drawable.test_3);
                holder.itemView.setTag(position);
                break;
            case 3:
                ((SimpleItemHolder) holder).getTextView_title().setText("图书查询");
                ((SimpleItemHolder) holder).getTextView_content().setText("图书馆有啥书，一查便知，再也不用白跑一趟啦");
                ((SimpleItemHolder) holder).getImageView_titleImage().setImageResource(R.drawable.test_2);
                holder.itemView.setTag(position);
                break;
			case 1:
                ((SimpleItemHolder) holder).getTextView_title().setText("评价系统");
                ((SimpleItemHolder) holder).getTextView_content().setText("图书馆有啥书，一查便知，再也不用白跑一趟啦");
                ((SimpleItemHolder) holder).getImageView_titleImage().setImageResource(R.drawable.test_1);
                holder.itemView.setTag(position);
                break;
				
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
		
        return 4;
		
    }

    @Override
    public void onClick(final View v) {
        int tag = (int) v.getTag();
		Intent intent = new Intent();
        switch (tag) {
            case 2:
                dialogShow(context.getString(R.string.isLoddingData), false);
                Request request_couse_table = OkHttpUtil.getRequest(OkHttpUtil.getREFERER() + linkMap.get("课表查询"));
                OkHttpUtil.getOkHttpClient().newCall(request_couse_table).enqueue(new Callback() {
						@Override
						public void onFailure(Call call, IOException e) {
							Log.v(TAG, "学生课表查询  onFailure --> " + e.getMessage());
							Message message = Message.obtain();
							message.obj = context.getString(R.string.get_data_error);
							handler.sendMessage(message);
							progressDialog.dismiss();
						}

						@Override
						public void onResponse(Call call, Response response) throws IOException {
							Message message = Message.obtain();
							try {
								if (response.code() == 200) {
									String content = new String(response.body().bytes(), "gb2312");
									if(content.length()<300){
										return;
									}

									map = JsoupService.getCourseTableParam(content);
									mFormParamVal = JsoupService.getCourseTableParamVal(content);
									mFormSelectedParam=JsoupService.getCurrentCourseTableParam(content);
									//baseParam=map.get("baseParam");
									progressDialog.dismiss();
									((Activity) context).runOnUiThread(new Runnable() {
											@Override
											public void run() {
												showChooseCourseTable(map);
											}
										});
									Log.v(TAG, "学习课表查询  onResponse --> content = " + content);
								} else {
									message.obj = "获取失败,请检查网络连接状况";
									handler.sendMessage(message);
								}
								Log.v(TAG, "学习课表查询  onResponse --> response.code = " + response.code());
							} catch (Exception e) {
								e.printStackTrace();
								progressDialog.dismiss();
								message.obj = context.getString(R.string.get_data_error);
								handler.sendMessage(message);
							} finally {
								progressDialog.dismiss();
							}
						}
					});
                break;
            case 0:
                dialogShow(context.getString(R.string.isLoddingData), false);
                Request request_score = OkHttpUtil.getRequest(OkHttpUtil.getREFERER() + linkMap.get("成绩查询"));
                OkHttpUtil.getOkHttpClient().newCall(request_score).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.v(TAG, "学习成绩查询  onFailure --> " + e.getMessage());
                        Message message = Message.obtain();
                        message.obj = context.getString(R.string.get_data_error);
                        handler.sendMessage(message);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = Message.obtain();
                        try {
                            if (response.code() == 200) {
                                String content = new String(response.body().bytes(), "gb2312");
								if(content.length()<300){
									return;
								}
								
                                final Map<String, Object> map = JsoupService.getScoreYear(content);
                                progressDialog.dismiss();
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showChooseYearSemesterDialog(map);
                                    }
                                });
                                Log.v(TAG, "学习成绩查询  onResponse --> content = " + content);
                            } else {
                                message.obj = "获取失败,请检查网络连接状况";
                                handler.sendMessage(message);
                            }
                            Log.v(TAG, "学习成绩查询  onResponse --> response.code = " + response.code());
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            message.obj = context.getString(R.string.get_data_error);
                            handler.sendMessage(message);
                        } finally {
                            progressDialog.dismiss();
                        }
                    }
                });
                break;
            case 3:
                dialogShow(context.getString(R.string.isLoddingData), false);
                Request request_classroom = OkHttpUtil.getRequest(OkHttpUtil.getREFERER() + linkMap.get("教室查询"));
                OkHttpUtil.getOkHttpClient().newCall(request_classroom).enqueue(new Callback() {
						@Override
						public void onFailure(Call call, IOException e) {
							//Log.v(TAG, "学生课表查询  onFailure --> " + e.getMessage());
							Message message = Message.obtain();
							message.obj = context.getString(R.string.get_data_error);
							handler.sendMessage(message);
							progressDialog.dismiss();
						}

						@Override
						public void onResponse(Call call, Response response) throws IOException {
							Message message = Message.obtain();
							try {
								if (response.code() == 200) {
									String content = new String(response.body().bytes(), "gb2312");
									if(content.length()<300){
										return;
									}
									LogUtil.e("zzzz",content);

									map = JsoupService.getEmptyClassroomParam(content);
									//CourseParamVal = JsoupService.getCourseTableParamVal(content);
									mFormSelectedParam=JsoupService.getCurrentCourseTableParam(content);
									//baseParam=map.get("baseParam");
									progressDialog.dismiss();
									((Activity) context).runOnUiThread(new Runnable() {
											@Override
											public void run() {
												showCustomEmptyClassroom(map);
											}
										});
									//Log.v(TAG, "学习课表查询  onResponse --> content = " + content);
								} else {
									message.obj = "获取失败,请检查网络连接状况";
									handler.sendMessage(message);
								}
								//Log.v(TAG, "学习课表查询  onResponse --> response.code = " + response.code());
							} catch (Exception e) {
								e.printStackTrace();
								progressDialog.dismiss();
								message.obj = context.getString(R.string.get_data_error);
								handler.sendMessage(message);
							} finally {
								progressDialog.dismiss();
							}
						}
					});
				
                //intent.setClass(context, LibraryActivity.class);
              //  context.startActivity(intent);
                break;
			case 1://评价
				dialogShow(context.getString(R.string.isLoddingData), false);
				//GET /xsjxpj.aspx?xkkh=(2016-2017-2)-90110014-81092-1&xh=3168119105&gnmkdm=N12141 HTTP/1.1\r\n
				
				Map<String, String> CoursesMap=new HashMap<String,String>();
				for (Map.Entry<String, String> entry : linkMap.entrySet()) {
					//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
					if(entry.getValue().indexOf("xsjxpj.aspx") >=0){
						CoursesMap.put(entry.getKey(),entry.getValue());
					}
				}
				if(!CoursesMap.isEmpty()){
					
					intent.setClass(context, CoursesListActivity.class);
					intent.putExtra("CoursesMap", (Serializable) CoursesMap);
					context.startActivity(intent);
					
				}else{
					Message message = Message.obtain();
					//通过handler发送message以通知ui线程更新UI
					message.obj = context.getString(R.string.get_data_error);
					handler.sendMessage(message);
					//Log.v(TAG, "班级课表查询  onFailure -->  = " + e.getMessage());
					progressDialog.dismiss();
				}
				//for (String value : linkMap.values())
				
                
                
				break;
				

        }
    }

    /**
     * 设置dialog状态信息并展示
     *
     * @param message    dialog内容
     * @param cancelable 是否可取消
     */
    public void dialogShow(String message, boolean cancelable) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancelable);
        progressDialog.show();
    }
	
	
	private void showCustomEmptyClassroom(final Map<String,List<String>> map){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View view = View.inflate(context,R.layout.dialog_custom_empty_classroom,null);
		builder.setView(view);
		builder.setTitle(context.getString(R.string.dialog_choose_year_name));
		
		
		spinnerArray[0] = ((Spinner) view.findViewById(R.id.spinner_date_start));
		spinnerArray[1] = ((Spinner) view.findViewById(R.id.spinner_date_end));
		spinnerArray[2] = ((Spinner) view.findViewById(R.id.spinner_week_day));
	    spinnerArray[3] = ((Spinner) view.findViewById(R.id.spinner_week_divide));
		spinnerArray[4] = ((Spinner) view.findViewById(R.id.spinner_lesson));
	    //spinnerArray[5] = ((Spinner) view.findViewById(R.id.spinner_class));
		
		
		
		for(int i=0;i<4;i++){
			//List<String> h=(List<String>) map.get(param[i]);
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,(List<String>) map.get(JsoupService.paramEmptyClassroom[i]) );
			arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//final Spinner spinner_year = ((Spinner) view.findViewById(R.id.spinner_year));
			spinnerArray[i].setAdapter(arrayAdapter);
			spinnerArray[i].setOnItemSelectedListener(this);
		}
        
        builder.setPositiveButton(context.getString(R.string.dialog_PositiveButton_text), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, int which) {
					
					Intent intent = new Intent();
					intent.setClass(context, CourseActivity.class);
					intent.putExtra("content", mCouseTableWebContent);
					LogUtil.e("zz",mCouseTableWebContent);
					
					context.startActivity(intent);
					
				}

			});
        builder.setNegativeButton(context.getString(R.string.dialog_NegativeButton_text), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					spinnerInitNum=0;
				}
			});
		
        builder.show();
    
	}

    /**
     * 显示自定义对话框并请求数据
     *
     * @param map 学年学期以及请求数据的集合
     */

    private void showChooseYearSemesterDialog(final Map<String, Object> map) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.score_custom_dialog, null);
        builder.setView(view);
        builder.setTitle(context.getString(R.string.dialog_choose_year_name));
        /**
         * 学年spinner适配器
         */
        ArrayAdapter<String> arrayAdapter_year = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, (List<String>) map.get("score_year"));
        arrayAdapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner_year = ((Spinner) view.findViewById(R.id.spinner_year));
        spinner_year.setAdapter(arrayAdapter_year);
        //默认选择List集合中倒数第二个
        if (((List<String>) map.get("score_year")).size() > 1) {
            spinner_year.setSelection(((List<String>) map.get("score_year")).size() - 2);
        }
        /**
         * 学期spinner适配器
         */
        ArrayAdapter<String> arrayAdapter_semester = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, (List<String>) map.get("score_semester"));
        arrayAdapter_semester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner_semester = ((Spinner) view.findViewById(R.id.spinner_semester));
        spinner_semester.setAdapter(arrayAdapter_semester);
        //默认选择List集合中倒数第三个
        if (((List<String>) map.get("score_semester")).size() > 2) {
            spinner_semester.setSelection((((List<String>) map.get("score_semester")).size() - 3));
        }
        builder.setPositiveButton(context.getString(R.string.dialog_PositiveButton_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
				FormBody.Builder mFormBodyBuilder = new FormBody.Builder(); 
                try{
					mFormBodyBuilder
                        .add("__VIEWSTATE", (String) map.get("__VIEWSTATE"))
						//   .add("__VIEWSTATEGENERATOR", (String) map.get("__VIEWSTATEGENERATOR"))
                        .add("ddlXN", spinner_year.getSelectedItem().toString())
                        .add("ddlXQ", spinner_semester.getSelectedItem().toString())
                        .addEncoded("Button1", URLEncoder.encode("按学期查询", "GBK"))
                        .build();
				}
				catch (UnsupportedEncodingException e){}
                /**
                 * 对Referer中的中文进行编码
                 */
               // String Referer = OkHttpUtil.encodeUrl(OkHttpUtil.getREFERER() + getLinkMap().get("学习成绩查询"));
				String Referer = OkHttpUtil.encodeUrl(OkHttpUtil.getREFERER() + map.get("SubmitLink"));
                Request request = OkHttpUtil.getRequest(Referer, Referer, mFormBodyBuilder.build());
	                dialogShow(context.getString(R.string.isLoddingData), false);
                OkHttpUtil.getOkHttpClient().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.v(TAG, "学习成绩查询  --> onFailure  --> " + e.getMessage());
                        progressDialog.dismiss();
                        dialog.dismiss();
                        Message message = Message.obtain();
                        message.obj = context.getString(R.string.get_data_error);
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = Message.obtain();
                        try {
                            if (response.code() == 200) {
                                String content = new String(response.body().bytes(), "gb2312");
								
                                ArrayList<CourseInfo> courseInfoArrayList = JsoupService.parseCourseScore(content);
                                Intent intent = new Intent();
                                intent.setClass(context, ScoreActivity.class);
                                intent.putExtra("score", courseInfoArrayList);
                                intent.putExtra("year", spinner_year.getSelectedItem().toString());
                                intent.putExtra("semester", spinner_semester.getSelectedItem().toString());
                                context.startActivity(intent);
                            } else {
                                message.obj = context.getString(R.string.get_data_error);
                                handler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            message.obj = context.getString(R.string.get_data_error);
                            handler.sendMessage(message);
                        } finally {
                            progressDialog.dismiss();
                        }
                        Log.v(TAG, "学习成绩查询  --> onResponse  --> response.code = " + response.code());
                    }
                });
            }

        });
        builder.setNegativeButton(context.getString(R.string.dialog_NegativeButton_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
	
	private void showChooseCourseTable(final Map<String,List<String>> map){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View view = View.inflate(context,R.layout.dialog_custom_couse_table,null);
		builder.setView(view);
		builder.setTitle(context.getString(R.string.dialog_choose_year_name));


		spinnerArray[0] = ((Spinner) view.findViewById(R.id.spinner_year));
		spinnerArray[1] = ((Spinner) view.findViewById(R.id.spinner_semester));
		spinnerArray[2] = ((Spinner) view.findViewById(R.id.spinner_grade));
	    spinnerArray[3] = ((Spinner) view.findViewById(R.id.spinner_academy));
		spinnerArray[4] = ((Spinner) view.findViewById(R.id.spinner_mojar));
	    spinnerArray[5] = ((Spinner) view.findViewById(R.id.spinner_class));



		for(int i=0;i<6;i++){
			List list=(List<String>) map.get(param[i]);
			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
			arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//final Spinner spinner_year = ((Spinner) view.findViewById(R.id.spinner_year));
			spinnerArray[i].setAdapter(arrayAdapter);
			spinnerArray[i].setOnItemSelectedListener(this);
			for(int j=0;j<list.size();j++){ //注
				if(mFormSelectedParam.get(param[i]).equals(list.get(j))){
					spinnerArray[i].setSelection(j,true);
					break;
				}
			}
		}
		/*
		SpinnerAdapter arrayAdapter= spinner.getAdapter();
		int k= apsAdapter.getCount();
		for(int i=0;i<k;i++){
			if("hhh".equals(apsAdapter.getItem(i).toString())){
				spinner.setSelection(i,true);
				break;
			}
		}*/
        builder.setPositiveButton(context.getString(R.string.dialog_PositiveButton_text), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, int which) {
					Intent intent = new Intent();
					intent.setClass(context, CourseActivity.class);
					intent.putExtra("content", mCouseTableWebContent);
					LogUtil.e("zz",mCouseTableWebContent);

					context.startActivity(intent);

				}

			});
        builder.setNegativeButton(context.getString(R.string.dialog_NegativeButton_text), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					spinnerInitNum=0;
				}
			});

        builder.show();

	}

}


 class LogUtil {
    //规定每段显示的长度
    private static int LOG_MAXLENGTH = 2000;

    public static void e(String TAG, String msg) {
		int strLength = msg.length();
		int start = 0;
		int end = LOG_MAXLENGTH;
		for (int i = 0; i < 100; i++) {
			//剩下的文本还是大于规定长度则继续重复截取并输出
			if (strLength > end) {
				Log.e(TAG + i, msg.substring(start, end));
				start = end;
				end = end + LOG_MAXLENGTH;
			} else {
				Log.e(TAG, msg.substring(start, strLength));
				break;
			}
        }
    }
}


package com.renen.fjgr.support;
import okhttp3.*;
import android.os.*;
import android.content.*;
import java.io.*;
import com.renen.fjgr.*;
import android.util.*;

public class OkCallback  implements Callback{
	public String WebContent;
	private Context context;
	private Handler handler;

	private void setWebContent(String s){
		WebContent=s;
	}

	public String getWebContent(){
		return WebContent;
	}
	//请求失败后的回调方法
	public OkCallback(Context context,Handler handler){
		this.context=context;
		this.handler=handler;
	}

	@Override
	public void onFailure(Call call, IOException e) {
		Message message = Message.obtain();
		//通过handler发送message以通知ui线程更新UI
		/*message.obj = context.getString(R.string.get_data_error);
		 handler.sendMessage(message);
		 Log.v(TAG, "班级课表查询  onFailure -->  = " + e.getMessage());
		 progressDialog.dismiss();*/
	}

	//请求成功后的回调方法
	@Override
	public void onResponse(Call call, Response response) throws IOException {
		Message message = Message.obtain();
		try {
			if (response.code() == 200) {
				WebContent = new String(response.body().bytes(), "gb2312");
				//Log.e("zz",content);
				
				//this.setWebContent(content);

			} else {
				//请求码 response.code != 200  发送消息通知ui线程更新ui
				message.obj = context.getString(R.string.get_data_error);
				handler.sendMessage(message);
			}
			//Log.v(TAG, "班级课表查询  onResponse -->  statuscode = " + response.code());
		} catch (Exception e) {
			message.obj = context.getString(R.string.get_data_error);
			handler.sendMessage(message);
			e.printStackTrace();
		} /*finally {
		 progressDialog.dismiss();
		 }*/
	}
}

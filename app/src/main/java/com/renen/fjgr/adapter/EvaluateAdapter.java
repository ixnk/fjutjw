package com.renen.fjgr.adapter;
import android.widget.*;
import java.util.*;
import android.view.*;
import android.content.*;
import com.renen.fjgr.*;
import android.content.res.*;
import com.renen.fjgr.ui.*;
import android.util.*;

public class EvaluateAdapter extends BaseAdapter{


    private LayoutInflater layoutInflater;  
    private Context context;  
	private String[] titles;
	private String[] descriptions;
	private List<String> mEvaluateDegree;
	private final static String mStringDegree[]={"优秀","良好","中等","及格","不及格","未评"};
	Map<String,Integer> spMap;
	private int unevaluation=0;
	
	//int a=0;
	
	
    public EvaluateAdapter(Context context, List<String> mEvaluateDegree){  
        this.context = context;  
		this.mEvaluateDegree = mEvaluateDegree;
		mEvaluateDegree.remove(0);
		
		if(this.mEvaluateDegree.isEmpty()){
			for(int i=1;i<=10;i++){
				this.mEvaluateDegree.add(mStringDegree[5]);
			}
			
		}
		spMap = new HashMap<String, Integer>();  
        this.layoutInflater = LayoutInflater.from(context);  
		Resources res =context.getResources();
		titles = res.getStringArray(R.array.evaluating_indicator);
		descriptions = res.getStringArray(R.array.evaluating_description);
		
    }  

    public final class Row{  

        public TextView title;  
        public TextView description; 
		//public Button btn_evaluate;
		public Spinner sp_elvaluateDegree;
    }  

	public List<String> getEvaluateData(){
		return mEvaluateDegree;
	}

	public static int[] EvaluateDegreeNumber(List<String> delist){
		int mDegreeNum[]=new int[10];
		for (int i=1;i <= 10;i++){
			switch (delist.get(i)){
				case "优秀":
					mDegreeNum[i] = 0;
					break;
				case "良好":
					mDegreeNum[i] = 1;
					break;
				case "中等":
					mDegreeNum[i] = 2;
					break;
				case "及格":
					mDegreeNum[i] = 3;
					break;
				case "不及格":
					mDegreeNum[i] = 4;
					break;

			}
		}
		return mDegreeNum;
	}

    @Override  
    public int getCount(){  
        return titles.length;  
    }  
    /** 
     * 获得某一位置的数据 
     */  
    @Override  
    public Object getItem(int position){  
        return titles[position];
    }  

    @Override  
    public long getItemId(int position){  
        return position;  
    }  
	
	public int getUnEvaluaion(){
		return unevaluation;
	}
	
    @Override  
    public View getView(int position, View convertView, ViewGroup parent){  
	
        Row row=null; 
		
        if (convertView == null){  
            row = new Row();  

            convertView = layoutInflater.inflate(R.layout.evaluate_row, null);  

            row.title = (TextView)convertView.findViewById(R.id.evaluate_indication);  
            row.description = (TextView)convertView.findViewById(R.id.evaluate_description);  
			
			row.sp_elvaluateDegree = (Spinner)convertView.findViewById(R.id.spinner_evaluate);
			row.sp_elvaluateDegree.setId(position);
			//String[] evaluating_degree=context.getResources().getStringArray(R.array.evaluating_degree);
			ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.evaluating_degree));
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
			row.sp_elvaluateDegree.setAdapter(adapter);  
			
			int mDegree;
				switch (mEvaluateDegree.get(position)){
					case "优秀":
						mDegree = 0;
						break;
					case "良好":
						mDegree = 1;
						break;
					case "中等":
						mDegree = 2;
						break;
					case "及格":
						mDegree = 3;
						break;
					case "不及格":
						mDegree = 4;
						break;
					default:
						mDegree = 5;
						//unevaluation++;
				}
				
			row.sp_elvaluateDegree.setSelection(mDegree);
			row.sp_elvaluateDegree.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

					@Override
					public void onItemSelected(AdapterView<?> adv, View v, int pos, long id){
						// TODO: Implement this method

						//int b=((Spinner)adv).getId();
						//Toast.makeText(context, String.valueOf(b), 0).show();
						//int m=((Spinner)adv).getSelectedItemPosition();
						mEvaluateDegree.set(((Spinner)adv).getId(), mStringDegree[((Spinner)adv).getSelectedItemPosition()]);
						if(mStringDegree[((Spinner)adv).getSelectedItemPosition()].equals("未评")){
							unevaluation++;
						}else{
							unevaluation--;
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> p1){
						// TODO: Implement this method
					}


				});
			convertView.setTag(row);		
			//Log.e("zzj1",String.valueOf(position)+row.title.toString());
		}else{  
            row = (Row)convertView.getTag();  
			//Log.e("zzj2",String.valueOf(position)+row.title.toString());
        }  

        
		// row.image.setBackgroundResource((Integer)data.get(position).get("image"));  
        row.title.setText(titles[position]);  
        row.description.setText(descriptions[position]);
		//row.btn_evaluate.setText(mEvaluateDegree.get(position));
		Log.e("zzj",String.valueOf(position)+row.title.toString());
		
        return convertView;
    }  


}  

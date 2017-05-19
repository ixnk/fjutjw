package com.renen.fjgr.ui;

import android.content.*;
import android.os.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.renen.fjgr.*;
import com.renen.fjgr.adapter.*;
import com.renen.fjgr.support.*;
import java.util.*;

import android.support.v7.widget.Toolbar;
import java.text.*;

/**
 * 登录后的主界面
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private Handler handler;
    private MainRecyclerAdapter mainRecyclerAdapter;
    private Toolbar toolbar;
    private TextView textView_drawer_top_name;
    private ListView drawer_listView;
    private LinearLayout linearLayout_sign_out;
    //记录第一次按退出按钮时的时间
    private long exitTime = 0;

	public static int mWeekth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main_layout);
        this.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                
                if (msg.obj != null && msg.obj instanceof String) {
                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                }
            }
        };
        final Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String content = intent.getStringExtra("content");
        initView();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawerLayout_open_string, R.string.drawerLayout_close_string);
        actionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mainRecyclerAdapter = new MainRecyclerAdapter(this, handler);
        mainRecyclerAdapter.setLinkMap(JsoupService.parseMenu(content));
        recyclerView.setAdapter(mainRecyclerAdapter);
        initDrawerMenu(name);
		
		Date curDate = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		
		try{
			Date beginDate = sdf.parse("2017-2-10");
			long betweenTime = curDate.getTime() - beginDate.getTime(); 
			mWeekth = (int) (betweenTime / 1000 / 60 / 60 / 24 / 7); 
		}
		catch (ParseException e){}
        //退出登录
        linearLayout_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent1);
                MainActivity.this.finish();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_main);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_DrawerLayout);
        textView_drawer_top_name = (TextView) findViewById(R.id.drawer_top_name);
        drawer_listView = (ListView) findViewById(R.id.drawer_listView);
        linearLayout_sign_out = (LinearLayout) findViewById(R.id.sign_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            case R.id.about_app:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                String app_name = this.getString(this.getApplicationInfo().labelRes);
                String app_version ="1.0";
                String author = getResources().getString(R.string.about_author);
                String contacts = getResources().getString(R.string.about_email);
                String message = app_name + "\n" + getResources().getString(R.string.about_version) + app_version + "\n" + author + "\n" + contacts;
                builder.setMessage(message);
                builder.setPositiveButton(getResources().getString(R.string.dialog_PositiveButton_text), null);
                builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化抽屉菜单
     *
     * @param name 用户名字
     */
    private void initDrawerMenu(String name) {
        textView_drawer_top_name.setText(name);
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("title", getString(R.string.drawerlayout_person_info_item_name));
        map.put("icon", R.drawable.information_personal);
        list.add(map);
        map = new HashMap<>();
        map.put("title", getString(R.string.drawerlayout_update_password_item_name));
        map.put("icon", R.drawable.change_password);
        list.add(map);
        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, list, R.layout.drawer_menu_item, new String[]{"title", "icon"}, new int[]{R.id.drawer_menu_title, R.id.drawer_menu_icon});
        drawer_listView.setAdapter(simpleAdapter);
        drawer_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        intent.setClass(MainActivity.this, PersonInfoActivity.class);
                        startActivity(intent);
                        break;

                    case 1:
                        intent.setClass(MainActivity.this, UpdatePasswordActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    /**
     * 实现按两下返回退出app
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, getString(R.string.exit_app) + this.getString(this.getApplicationInfo().labelRes), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                onBackPressed();
            }
        }
        return false;
    }
}

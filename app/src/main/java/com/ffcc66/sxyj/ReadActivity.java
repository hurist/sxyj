package com.ffcc66.sxyj;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.baidu.tts.auth.AuthInfo;
//import com.baidu.tts.client.SpeechError;
//import com.baidu.tts.client.SpeechSynthesizer;
//import com.baidu.tts.client.SpeechSynthesizerListener;
//import com.baidu.tts.client.TtsMode;
import com.ffcc66.sxyj.View.PageWidget;
import com.ffcc66.sxyj.base.BaseActivity;
import com.ffcc66.sxyj.dialog.PageModeDialog;
import com.ffcc66.sxyj.dialog.SettingDialog;
import com.ffcc66.sxyj.entity.BookList;
import com.ffcc66.sxyj.entity.BookMarks;
import com.ffcc66.sxyj.util.BrightnessUtil;
import com.ffcc66.sxyj.util.PageFactory;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class ReadActivity extends BaseActivity {
    private static final String TAG = "ReadActivity";
    private final static String EXTRA_BOOK = "bookList";
    private final static int MESSAGE_CHANGEPROGRESS = 1;

    @BindView(R.id.bookpage)
    PageWidget bookpage;        //小说的实际显示View
    @BindView(R.id.tv_progress)
    TextView tv_progress;
    @BindView(R.id.rl_progress)
    RelativeLayout rl_progress;
    @BindView(R.id.tv_pre)
    TextView tv_pre;
    @BindView(R.id.sb_progress)
    SeekBar sb_progress;
    @BindView(R.id.tv_next)
    TextView tv_next;
    @BindView(R.id.tv_directory)
    TextView tv_directory;
    @BindView(R.id.tv_dayornight)
    TextView tv_dayornight;
    @BindView(R.id.tv_pagemode)
    TextView tv_pagemode;
    @BindView(R.id.tv_setting)
    TextView tv_setting;
    @BindView(R.id.bookpop_bottom)
    LinearLayout bookpop_bottom;
    @BindView(R.id.rl_bottom)
    RelativeLayout rl_bottom;
    @BindView(R.id.tv_stop_read)
    TextView tv_stop_read;
    @BindView(R.id.rl_read_bottom)
    RelativeLayout rl_read_bottom;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    private Config config;
    private WindowManager.LayoutParams lp;
    private BookList bookList;
    private PageFactory pageFactory;
    private int screenWidth, screenHeight;
    // popwindow是否显示
    private Boolean isShow = false;
    private SettingDialog mSettingDialog;
    private PageModeDialog mPageModeDialog;
    private Boolean mDayOrNight;
//    // 语音合成客户端
//    private SpeechSynthesizer mSpeechSynthesizer;
//    private boolean isSpeaking = false;

    // 接收电池信息更新的广播
    private BroadcastReceiver myReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                Log.e(TAG, Intent.ACTION_BATTERY_CHANGED);
                int level = intent.getIntExtra("level", 0);
                pageFactory.updateBattery(level);
            }else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)){
                Log.e(TAG, Intent.ACTION_TIME_TICK);
                pageFactory.updateTime();
            }
        }
    };

    @Override
    public int getLayoutRes() {
        return R.layout.activity_read;
    }

    @Override
    protected void initData() {
        //判断安卓版本，如果大于等于14小于19，bookpage开启硬件加速
        if(Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 19){
            bookpage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.return_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        config = Config.getInstance();    //获取配置文件实例
        pageFactory = PageFactory.getInstance();    //获取PageFactory实例

        //设置intent过滤器，订阅电池和时间变化广播
        IntentFilter mfilter = new IntentFilter();
        mfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mfilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(myReceiver, mfilter);  //注册广播接收器

        mSettingDialog = new SettingDialog(this);   //实例化设置Dialog
        mPageModeDialog = new PageModeDialog(this); //实例化翻页模式设置Dialog
        //获取屏幕宽高
        WindowManager manage = getWindowManager();      //获取窗口管理器
        Display display = manage.getDefaultDisplay();
        Point displaysize = new Point();
        display.getSize(displaysize);       //获取屏幕宽高
        screenWidth = displaysize.x;
        screenHeight = displaysize.y;
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //隐藏系统UI(状态栏之类的)，自定义方法
        hideSystemUI();
        //改变屏幕亮度
        if (!config.isSystemLight()) {  //如果不是系统的亮度（用户改过亮度），则将亮度修改为用户设置的亮度
            BrightnessUtil.setBrightness(this, config.getLight());
        }
        //获取intent中的携带的图书信息
        Intent intent = getIntent();
        bookList = (BookList) intent.getSerializableExtra(EXTRA_BOOK);

        bookpage.setPageMode(config.getPageMode());     //设置翻页模式
        pageFactory.setPageWidget(bookpage);

        try {
            pageFactory.openBook(bookList);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "打开电子书失败", Toast.LENGTH_SHORT).show();
        }

        initDayOrNight();

//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                initialTts();
//            }
//        }.start();
//        initialTts();
    }

    @Override
    protected void initListener() {
        sb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float pro;
            // 触发操作，拖动
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pro = (float) (progress / 10000.0);
                showProgress(pro);
            }

            // 表示进度条刚开始拖动，开始拖动时候触发的操作
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // 停止拖动时候
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pageFactory.changeProgress(pro);
            }
        });

        mPageModeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideSystemUI();
            }
        });

        mPageModeDialog.setPageModeListener(new PageModeDialog.PageModeListener() {
            @Override
            public void changePageMode(int pageMode) {
                bookpage.setPageMode(pageMode);
            }
        });

        mSettingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideSystemUI();
            }
        });

        mSettingDialog.setSettingListener(new SettingDialog.SettingListener() {
            @Override
            public void changeSystemBright(Boolean isSystem, float brightness) {
                if (!isSystem) {
                    BrightnessUtil.setBrightness(ReadActivity.this, brightness);
                } else {
                    int bh = BrightnessUtil.getScreenBrightness(ReadActivity.this);
                    BrightnessUtil.setBrightness(ReadActivity.this, bh);
                }
            }

            @Override
            public void changeFontSize(int fontSize) {
                pageFactory.changeFontSize(fontSize);
            }

            @Override
            public void changeTypeFace(Typeface typeface) {
                pageFactory.changeTypeface(typeface);
            }

            @Override
            public void changeBookBg(int type) {
                pageFactory.changeBookBg(type);
            }
        });

        pageFactory.setPageEvent(new PageFactory.PageEvent() {
            @Override
            public void changeProgress(float progress) {
                Message message = new Message();
                message.what = MESSAGE_CHANGEPROGRESS;
                message.obj = progress;
                mHandler.sendMessage(message);
            }
        });

        bookpage.setTouchListener(new PageWidget.TouchListener() {
            @Override
            public void center() {
                if (isShow) {
                    hideReadSetting();
                } else {
                    showReadSetting();
                }
            }

            @Override
            public Boolean prePage() {
                if (isShow){
                    return false;
                }

                pageFactory.prePage();
                if (pageFactory.isfirstPage()) {
                    return false;
                }

                return true;
            }

            @Override
            public Boolean nextPage() {
                Log.e("setTouchListener", "nextPage");
                if (isShow){
                    return false;
                }

                pageFactory.nextPage();
                if (pageFactory.islastPage()) {
                    return false;
                }
                return true;
            }

            @Override
            public void cancel() {
                pageFactory.cancelPage();
            }
        });

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_CHANGEPROGRESS:
                    float progress = (float) msg.obj;
                    setSeekBarProgress(progress);
                    break;
            }
        }
    };


    @Override
    protected void onResume(){
        super.onResume();
        if (!isShow){
            hideSystemUI();
        }
//        if (mSpeechSynthesizer != null){
//            mSpeechSynthesizer.resume();
//        }
    }

    @Override
    protected void onStop(){
        super.onStop();
//        if (mSpeechSynthesizer != null){
//            mSpeechSynthesizer.stop();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageFactory.clear();
        bookpage = null;
        unregisterReceiver(myReceiver);
//        isSpeaking = false;
//        if (mSpeechSynthesizer != null){
//            mSpeechSynthesizer.release();
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShow){
                hideReadSetting();
                return true;
            }
            if (mSettingDialog.isShowing()){
                mSettingDialog.hide();
                return true;
            }
            if (mPageModeDialog.isShowing()){
                mPageModeDialog.hide();
                return true;
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.read, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_bookmark){
            if (pageFactory.getCurrentPage() != null) {
                List<BookMarks> bookMarksList = DataSupport.where("bookpath = ? and begin = ?", pageFactory.getBookPath(),pageFactory.getCurrentPage().getBegin() + "").find(BookMarks.class);

                if (!bookMarksList.isEmpty()){
                    Toast.makeText(ReadActivity.this, "该书签已存在", Toast.LENGTH_SHORT).show();
                }else {
                    BookMarks bookMarks = new BookMarks();
                    String word = "";
                    for (String line : pageFactory.getCurrentPage().getLines()) {
                        word += line;
                    }
                    try {
                        SimpleDateFormat sf = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm ss");
                        String time = sf.format(new Date());
                        bookMarks.setTime(time);
                        bookMarks.setBegin(pageFactory.getCurrentPage().getBegin());
                        bookMarks.setText(word);
                        bookMarks.setBookpath(pageFactory.getBookPath());
                        bookMarks.save();

                        Toast.makeText(ReadActivity.this, "书签添加成功", Toast.LENGTH_SHORT).show();
                    } catch (SQLException e) {
                        Toast.makeText(ReadActivity.this, "该书签已存在", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ReadActivity.this, "添加书签失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }/*else if (id == R.id.action_read_book){
            initialTts();
            if (mSpeechSynthesizer != null){
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5");
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
//                mSpeechSynthesizer.setParam(SpeechSynthesizer. MIX_MODE_DEFAULT);
//                mSpeechSynthesizer.setParam(SpeechSynthesizer. AUDIO_ENCODE_AMR);
//                mSpeechSynthesizer.setParam(SpeechSynthesizer. AUDIO_BITRA TE_AMR_15K85);
                mSpeechSynthesizer.setParam(SpeechSynthesizer. PARAM_VOCODER_OPTIM_LEVEL, "0");
                int result = mSpeechSynthesizer.speak(pageFactory.getCurrentPage().getLineToString());
                if (result < 0) {
                    Log.e(TAG,"error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
                }else{
                    hideReadSetting();
                    isSpeaking = true;
                }
            }
        }*/

        return super.onOptionsItemSelected(item);
    }


    public static boolean openBook(final BookList bookList, Activity context) {
        if (bookList == null){
            throw new NullPointerException("BookList can not be null");
        }

        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra(EXTRA_BOOK, bookList);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //如果activity在task存在，将Activity之上的所有Activity结束掉
        context.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);  //activity切换动画，第一个为开始，第二个为结束
        context.startActivity(intent);
        return true;
    }

//    public BookPageWidget getPageWidget() {
//        return bookpage;
//    }

    /**
     * 隐藏菜单。沉浸式阅读
     */
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    //显示书本进度
    public void showProgress(float progress){
        if (rl_progress.getVisibility() != View.VISIBLE) {
            rl_progress.setVisibility(View.VISIBLE);
        }
        setProgress(progress);
    }

    //隐藏书本进度
    public void hideProgress(){
        rl_progress.setVisibility(View.GONE);
    }

    public void initDayOrNight(){
        mDayOrNight = config.getDayOrNight();
        if (mDayOrNight){
            tv_dayornight.setText(getResources().getString(R.string.read_setting_day));
        }else{
            tv_dayornight.setText(getResources().getString(R.string.read_setting_night));
        }
    }

    //改变显示模式
    public void changeDayOrNight(){
        if (mDayOrNight){
            mDayOrNight = false;
            tv_dayornight.setText(getResources().getString(R.string.read_setting_night));
        }else{
            mDayOrNight = true;
            tv_dayornight.setText(getResources().getString(R.string.read_setting_day));
        }
        config.setDayOrNight(mDayOrNight);
        pageFactory.setDayOrNight(mDayOrNight);
    }

    private void setProgress(float progress){
        DecimalFormat decimalFormat=new DecimalFormat("00.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p=decimalFormat.format(progress * 100.0);//format 返回的是字符串
        tv_progress.setText(p + "%");
    }

    public void setSeekBarProgress(float progress){
        sb_progress.setProgress((int) (progress * 10000));
    }

    private void showReadSetting(){
        isShow = true;
        rl_progress.setVisibility(View.GONE);

//        if (isSpeaking){
//            Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_top_enter);
//            rl_read_bottom.startAnimation(topAnim);
//            rl_read_bottom.setVisibility(View.VISIBLE);
//        }else {
            showSystemUI();

            Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_enter);
            Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_top_enter);
            rl_bottom.startAnimation(topAnim);
            appbar.startAnimation(topAnim);
//        ll_top.startAnimation(topAnim);
            rl_bottom.setVisibility(View.VISIBLE);
//        ll_top.setVisibility(View.VISIBLE);
            appbar.setVisibility(View.VISIBLE);
     //   }
    }

    private void hideReadSetting() {
        isShow = false;
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_exit);
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_top_exit);
        if (rl_bottom.getVisibility() == View.VISIBLE) {
            rl_bottom.startAnimation(topAnim);
        }
        if (appbar.getVisibility() == View.VISIBLE) {
            appbar.startAnimation(topAnim);
        }
        if (rl_read_bottom.getVisibility() == View.VISIBLE) {
            rl_read_bottom.startAnimation(topAnim);
        }
//        ll_top.startAnimation(topAnim);
        rl_bottom.setVisibility(View.GONE);
        rl_read_bottom.setVisibility(View.GONE);
//        ll_top.setVisibility(View.GONE);
        appbar.setVisibility(View.GONE);
        hideSystemUI();
    }


    @OnClick({R.id.tv_progress, R.id.rl_progress, R.id.tv_pre, R.id.sb_progress, R.id.tv_next, R.id.tv_directory, R.id.tv_dayornight,R.id.tv_pagemode, R.id.tv_setting, R.id.bookpop_bottom, R.id.rl_bottom,R.id.tv_stop_read})
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_return:
//                finish();
//                break;
//            case R.id.ll_top:
//                break;
            case R.id.tv_progress:
                break;
            case R.id.rl_progress:
                break;
            case R.id.tv_pre:
                pageFactory.preChapter();
                break;
            case R.id.sb_progress:
                break;
            case R.id.tv_next:
                pageFactory.nextChapter();
                break;
            case R.id.tv_directory:
                Intent intent = new Intent(ReadActivity.this, MarkActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_dayornight:
                changeDayOrNight();
                break;
            case R.id.tv_pagemode:
                hideReadSetting();
                mPageModeDialog.show();
                break;
            case R.id.tv_setting:
                hideReadSetting();
                mSettingDialog.show();
                break;
            case R.id.bookpop_bottom:
                break;
            case R.id.rl_bottom:
                break;
            case R.id.tv_stop_read:
//                if (mSpeechSynthesizer!=null){
//                    mSpeechSynthesizer.stop();
//                    isSpeaking = false;
//                    hideReadSetting();
//                }
                break;
        }
    }


}

package com.ffcc66.sxyj.filechooser;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.ReadActivity;
import com.ffcc66.sxyj.entity.BookList;
//import com.zijie.treader.R;
import com.ffcc66.sxyj.util.FileUtils;
//import com.zijie.treader.ReadActivity;
//import com.zijie.treader.db.BookList;
//import com.zijie.treader.util.FileUtils;
//import com.zijie.treader.util.Fileutil;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DirectoryFragment extends Fragment implements View.OnClickListener {

    private static String title_ = "";
    private View fragmentView;
    private boolean receiverRegistered = false;
    private File currentDir;    //当前目录
    private ListView listView;
    private ListAdapter listAdapter;
    private TextView emptyView;
    private LinearLayout layout_bottom;
    private Button btn_choose_all;
    private Button btn_delete;
    private Button btn_add_file;
    private DocumentSelectActivityDelegate delegate;
    private ArrayList<ListItem> items = new ArrayList<ListItem>();  //listview列表数据
    private ArrayList<ListItem> checkItems = new ArrayList<ListItem>();
    private ArrayList<HistoryEntry> history = new ArrayList<HistoryEntry>();
    private HashMap<String, ListItem> selectedFiles = new HashMap<String, ListItem>();
    private List<BookList> bookLists;
    private long sizeLimit = 1024 * 1024 * 1024;

    private String[] chhosefileType = {".txt"};
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        if (currentDir == null) {
                            listRoots();
                        } else {
                            listFiles(currentDir);
                        }
                    } catch (Exception e) {
                        Log.e("tmessages", e.toString());
                    }
                }
            };
            if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
                listView.postDelayed(r, 1000);
            } else {
                r.run();
            }
        }
    };

    /**
     * 格式化输出文件大小
     *
     * @param size
     * @return 多少B 或多少KB 或多少M
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return String.format("%d B", size);
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0f);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / 1024.0f / 1024.0f);
        } else {
            return String.format("%.1f GB", size / 1024.0f / 1024.0f / 1024.0f);
        }
    }

    public static void clearDrawableAnimation(View view) {
        if (Build.VERSION.SDK_INT < 21 || view == null) {
            return;
        }
        Drawable drawable = null;
        if (view instanceof ListView) {
            drawable = ((ListView) view).getSelector();
            if (drawable != null) {
                drawable.setState(StateSet.NOTHING);
            }
        } else {
            drawable = view.getBackground();
            if (drawable != null) {
                drawable.setState(StateSet.NOTHING);
                drawable.jumpToCurrentState();
            }
        }
    }

    /**
     * 返回按键监听操作
     *
     * @return false表示还未回到最外层，true表示已经到最外层，再按就退出
     */
    public boolean onBackPressed_() {
        if (history.size() > 0) {
            HistoryEntry he = history.remove(history.size() - 1);
            title_ = he.title;
            updateName(title_);
            if (he.dir != null) {   //上一步不是根目录时
                listFiles(he.dir);
            } else {        //是根目录时
                listRoots();
            }
            listView.setSelectionFromTop(he.scrollItem, he.scrollOffset);
            return false;
        } else {
            return true;
        }
    }

    private void updateName(String title_) {
        if (delegate != null) {
            delegate.updateToolBarName(title_);
        }
    }

    public void onFragmentDestroy() {
        try {
            if (receiverRegistered) {
                getActivity().unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            Log.e("tmessages", e.toString());
        }
    }

    /**
     * 设置代理
     *
     * @param delegate
     */
    public void setDelegate(DocumentSelectActivityDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!receiverRegistered) {
            receiverRegistered = true;
            IntentFilter filter = new IntentFilter();
            //与SD卡挂载相关的Intent的action属性
            filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL); //未正确移除SD卡但已取出来时
            filter.addAction(Intent.ACTION_MEDIA_CHECKING);  //插入外部储存装置（如SD卡）
            filter.addAction(Intent.ACTION_MEDIA_EJECT);    //ACTION_MEDIA_EJECT表示用户欲卸载SD卡，但是SD卡上的部分内容尚处于打开状态
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);  //插入SD卡并且已正确安装（识别）时发出的广播
            filter.addAction(Intent.ACTION_MEDIA_NOFS);     //介质存在但是为空白或用在不支持的文件系统
            filter.addAction(Intent.ACTION_MEDIA_REMOVED);  //外部储存设备已被移除，不管有没正确卸载,都会发出此广播？
            filter.addAction(Intent.ACTION_MEDIA_SHARED);   // 广播：扩展介质的挂载被解除 (unmount)，因为它已经作为 USB 大容量存储被共享。
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);  //表示SD卡存在，但是无法挂载
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);    // 广播：扩展介质存在，但是还没有被挂载 (mount)。
            filter.addDataScheme("file");
            getActivity().registerReceiver(receiver, filter);
        }

        bookLists = DataSupport.findAll(BookList.class);

        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_directory_fragment,
                    container, false);

            //底部按钮栏
            layout_bottom = (LinearLayout) fragmentView
                    .findViewById(R.id.layout_bottom);
            btn_choose_all = (Button) fragmentView
                    .findViewById(R.id.btn_choose_all);
            btn_delete = (Button) fragmentView      //取消按钮
                    .findViewById(R.id.btn_delete);
            btn_add_file = (Button) fragmentView
                    .findViewById(R.id.btn_add_file);
            btn_choose_all.setOnClickListener(this);
            btn_delete.setOnClickListener(this);
            btn_add_file.setOnClickListener(this);


            listAdapter = new ListAdapter(getActivity());
            emptyView = (TextView) fragmentView     //空布局，用于在listview没有数据时显示
                    .findViewById(R.id.searchEmptyView);
            emptyView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            listView = (ListView) fragmentView.findViewById(R.id.listView);
            listView.setEmptyView(emptyView);
            listView.setAdapter(listAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view,
                                        int i, long l) {
                    if (i < 0 || i >= items.size()) {
                        return;
                    }
                    ListItem item = items.get(i);
                    File file = item.file;
                    if (file == null) {
                        HistoryEntry he = history.remove(history.size() - 1);
                        title_ = he.title;
                        updateName(title_);
                        if (he.dir != null) {
                            listFiles(he.dir);
                        } else {
                            listRoots();
                        }
                        listView.setSelectionFromTop(he.scrollItem,
                                he.scrollOffset);
                    } else if (file.isDirectory()) {    //如果点击的是一个目录（文件夹）
                        //记录历史
                        HistoryEntry he = new HistoryEntry();
                        he.scrollItem = listView.getFirstVisiblePosition(); //获取第一个可见的item的位置
                        he.scrollOffset = listView.getChildAt(0).getTop();  //获取第一个可见的item到父容器顶部的距离。用于返回上一级时回到同一位置
                        he.dir = currentDir;
                        he.title = title_.toString();

                        //updateName(title_);     //更改toolbar标题
                        if (!listFiles(file)) {     //列出下一级的文件和文件夹
                            return;
                        }
                        history.add(he);        //记录此次历史
                        title_ = item.title;
                        updateName(title_);     //更改toolbar标题
                        listView.setSelection(0);  //将打开下级目录的listview位置恢复到最上面
                    } else {
                        if (!file.canRead()) {  //文件不能读
                            showErrorBox("没有权限！");
                            return;
                        }
                        if (sizeLimit != 0) {       //文件过大
                            if (file.length() > sizeLimit) {
                                showErrorBox("文件大小超出限制！");
                                return;
                            }
                        }
                        if (file.length() == 0) {       //文件大小为 0
                            return;
                        }   //打开文件
                        if (file.toString().contains(chhosefileType[0]) ||
                                file.toString().contains(chhosefileType[1]) ||
                                file.toString().contains(chhosefileType[2]) ||
                                file.toString().contains(chhosefileType[3]) ||
                                file.toString().contains(chhosefileType[4])) {
                            if (delegate != null) {
                                ArrayList<String> files = new ArrayList<String>();
                                files.add(file.getAbsolutePath());
                                delegate.didSelectFiles(DirectoryFragment.this, files);
                            }
                        } else {
                            showErrorBox("请选择正确的文件！");
                            return;
                        }

                    }
                }
            });
            //改变加入书架按钮上的图书本数
            changgeCheckBookNum();
            listRoots();
        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        bookLists = DataSupport.findAll(BookList.class);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_choose_all:
                checkAll();
                changgeCheckBookNum();
                listAdapter.notifyDataSetChanged();
                changgeCheckBookNum();
                break;
            case R.id.btn_delete:
                checkItems.clear();
                listAdapter.notifyDataSetChanged();
                changgeCheckBookNum();
                break;
            case R.id.btn_add_file:
//                changgeCheckBookNum();
                addCheckBook();
                break;
        }
    }

    /**
     * 将选择的图书加入
     */
    private void addCheckBook() {
        if (checkItems.size() > 0) {
            List<BookList> bookLists = new ArrayList<BookList>();
            for (ListItem item : checkItems) {
                BookList bookList = new BookList();
                String bookName = FileUtils.getFileName(item.thumb);
                bookList.setBookname(bookName);
                bookList.setBookpath(item.thumb);
                bookList.setCoverpath("http://192.168.137.1:8080/images/test.jpg");
                bookList.setType(0);
                bookLists.add(bookList);
            }
            SaveBookToSqlLiteTask mSaveBookToSqlLiteTask = new SaveBookToSqlLiteTask();
            mSaveBookToSqlLiteTask.execute(bookLists);
        }
    }

    /**
     * 选择全部
     */
    private void checkAll() {
        for (ListItem listItem : items) {
            if (!TextUtils.isEmpty(listItem.thumb)) {
                boolean isCheck = false;
                for (ListItem item : checkItems) {
                    if (item.thumb.equals(listItem.thumb)) {
                        isCheck = true;
                        break;
                    }
                }
                for (BookList list : bookLists) {
                    if (list.getBookpath().equals(listItem.thumb)) {
                        isCheck = true;
                        break;
                    }
                }
                if (!isCheck) {
                    checkItems.add(listItem);
                }
            }
        }
    }

    /**
     * 列出根目录、内置存储、外置存储
     */
    private void listRoots() {
        currentDir = null;
        items.clear();

        //获取内部存储
        //获取sdcard的绝对路径，这里可能是内部存储也可能是外部sd卡,根据具体系统
        String extStorage = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        ListItem ext = new ListItem();
        //判断时内部还是外部存储
        if (Build.VERSION.SDK_INT < 9
                || Environment.isExternalStorageRemovable()) {
            ext.title = "SdCard";
        } else {
            ext.title = "InternalStorage";
        }
        ext.icon = Build.VERSION.SDK_INT < 9
                || Environment.isExternalStorageRemovable() ? R.mipmap.ic_external_storage
                : R.mipmap.ic_storage;
        ext.subtitle = getRootSubtitle(extStorage);
        ext.file = Environment.getExternalStorageDirectory();   //获取内存卡的目录并存储下来
        items.add(ext);     //将内存卡的相关数据加入list

        //获取外部存储
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    "/proc/mounts"));   //当前系统所安装的文件系统信息
            String line;
            HashMap<String, ArrayList<String>> aliases = new HashMap<String, ArrayList<String>>();
            ArrayList<String> result = new ArrayList<String>();
            String extDevice = null;
            while ((line = reader.readLine()) != null) {
                if ((!line.contains("/mnt") && !line.contains("/storage") && !line
                        .contains("/sdcard"))
                        || line.contains("asec")
                        || line.contains("tmpfs") || line.contains("none")) {
                    continue;
                }
                String[] info = line.split(" ");
                if (!aliases.containsKey(info[0])) {
                    aliases.put(info[0], new ArrayList<String>());
                }
                aliases.get(info[0]).add(info[1]);
                if (info[1].equals(extStorage)) {
                    extDevice = info[0];
                }
                result.add(info[1]);
            }
            reader.close();
            if (extDevice != null) {
                result.removeAll(aliases.get(extDevice));
                for (String path : result) {
                    try {
                        ListItem item = new ListItem();
                        if (path.toLowerCase().contains("sd")) {
                            ext.title = "SdCard";
                        } else {
                            ext.title = "外部存储";
                        }
                        item.icon = R.mipmap.ic_external_storage;
                        item.subtitle = getRootSubtitle(path);
                        item.file = new File(path);
                        items.add(item);
                    } catch (Exception e) {
                        Log.e("tmessages", e.toString());
                    }
                }
            }
        } catch (Exception e) {
            Log.e("tmessages", e.toString());
        }

        //获取系统根目录
        ListItem fs = new ListItem();
        fs.title = "/";
        fs.subtitle = "系统目录";
        fs.icon = R.mipmap.ic_directory;
        fs.file = new File("/");
        items.add(fs);

        // try {
        // File telegramPath = new
        // File(Environment.getExternalStorageDirectory(), "Telegram");
        // if (telegramPath.exists()) {
        // fs = new ListItem();
        // fs.title = "Telegram";
        // fs.subtitle = telegramPath.toString();
        // fs.icon = R.drawable.ic_directory;
        // fs.file = telegramPath;
        // items.add(fs);
        // }
        // } catch (Exception e) {
        // FileLog.e("tmessages", e);
        // }

        // AndroidUtilities.clearDrawableAnimation(listView);
        // scrolling = true;
        listAdapter.notifyDataSetChanged();
    }

    /**
     * 列出目录的下一级内容
     *
     * @param dir 目录路径
     * @return 无法读取返回false。正常则返回true
     */
    private boolean listFiles(File dir) {
        if (!dir.canRead()) {   //如果不可读
            if (dir.getAbsolutePath().startsWith(
                    Environment.getExternalStorageDirectory().toString())
                    || dir.getAbsolutePath().startsWith("/sdcard")
                    || dir.getAbsolutePath().startsWith("/mnt/sdcard")) {   //是否是存储卡中的内容
                if (!Environment.getExternalStorageState().equals(         //判断存储卡的状态，是否被挂载，是否为只读
                        Environment.MEDIA_MOUNTED)
                        && !Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED_READ_ONLY)) {     //如果即没被挂载且又不是可读的
                    currentDir = dir;
                    items.clear();
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_SHARED.equals(state)) {   //如果储存卡被作为usb大容量存储分享
                        emptyView.setText("UsbActive");
                    } else {
                        emptyView.setText("NotMounted");
                    }
                    clearDrawableAnimation(listView);
                    // scrolling = true;
                    listAdapter.notifyDataSetChanged();
                    return true;
                }
            }
            showErrorBox("没有权限!");
            return false;
        }


        //获取下一级的所有文件夹和文件信息
        emptyView.setText("没有文件!");
        File[] files = null;
        try {
            files = dir.listFiles();
        } catch (Exception e) {
            showErrorBox(e.getLocalizedMessage());
            return false;
        }
        if (files == null) {
            showErrorBox("未知错误!");
            return false;
        }
        currentDir = dir;
        items.clear();  //将item清空
        //将下一级的所有文件夹和文件信息按名称排序
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if (lhs.isDirectory() != rhs.isDirectory()) {
                    return lhs.isDirectory() ? -1 : 1;
                }
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
        for (File file : files) {
            //是否为隐藏文件/夹 || (不是一个目录 且 拓展名不为TXT) ，成立则直接跳过
            if (file.getName().startsWith(".") || (!file.isDirectory() && !file.getName().endsWith(".txt"))) {
                continue;
            }

            //判断类型，获取详细信息之类的操作
            ListItem item = new ListItem();
            item.title = file.getName();
            item.file = file;
            if (file.isDirectory()) {
                item.icon = R.mipmap.ic_directory;
                item.subtitle = "文件夹";
            } else {
                String fname = file.getName();     //这里获取的是带文件拓展名的文件名称
                String[] sp = fname.split("\\.");
                item.ext = sp.length > 1 ? sp[sp.length - 1] : "?"; //获取文件拓展名
                item.subtitle = formatFileSize(file.length());  //获取文件大小
                fname = fname.toLowerCase();    //将文件名字改成小写
                if (/*fname.endsWith(".jpg") || fname.endsWith(".png")
                        || fname.endsWith(".gif") || fname.endsWith(".jpeg") ||*/ fname.endsWith(".txt")) {
                    item.thumb = file.getAbsolutePath();    //获取绝对路径，（暂时有疑问，为图片格式时执行操作，这里应该是作者的失误？因此注释）
                }
            }
            items.add(item);
        }

        //将返回上级目录的文件夹加入item
        ListItem item = new ListItem();
        item.title = "..";
        item.subtitle = "文件夹";
        item.icon = R.mipmap.ic_directory;
        item.file = null;
        items.add(0, item);
        clearDrawableAnimation(listView);
        // scrolling = true;
        listAdapter.notifyDataSetChanged();
        return true;
    }

    /**
     * 显示错误消息
     *
     * @param error 内容
     */
    public void showErrorBox(String error) {
        if (getActivity() == null) {
            return;
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.app_name))
                .setMessage(error).setPositiveButton("OK", null).show();
    }

    /**
     * 显示是否打开文件并阅读的对话框
     *
     * @param path
     */
    public void showReadBox(final String path) {
        if (getActivity() == null) {
            return;
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.app_name))
                .setMessage(path).setPositiveButton("阅读", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BookList bookList = new BookList();
                String bookName = FileUtils.getFileName(path);
                bookList.setBookname(bookName);
                bookList.setBookpath(path);
                bookList.setCoverpath("http://192.168.137.1:8080/images/test.jpg");
                bookList.setType(0);

                boolean isSave = false;
                for (BookList book : bookLists) {
                    if (book.getBookpath().equals(bookList.getBookpath())) {
                        isSave = true;
                    }
                }

                if (!isSave) {
                    bookList.save();
                }
                ReadActivity.openBook(bookList, getActivity());
            }
        }).show();
    }

    /**
     * 获取外部存储的一些空间信息
     *
     * @param path
     * @return 空间信息
     */
    private String getRootSubtitle(String path) {
        StatFs stat = new StatFs(path);
        long total = (long) stat.getBlockCount() * (long) stat.getBlockSize();
        long free = (long) stat.getAvailableBlocks()
                * (long) stat.getBlockSize();
        if (total == 0) {
            return "";
        }
        return "Free " + formatFileSize(free) + " of " + formatFileSize(total);
    }

    /**
     * 改变选中图书的数量显示
     */
    private void changgeCheckBookNum() {
        btn_add_file.setText("加入书架(" + checkItems.size() + ")");
    }

    public void finishFragment() {

    }

    public static abstract interface DocumentSelectActivityDelegate {
        public void didSelectFiles(DirectoryFragment activity, ArrayList<String> files);

        public void startDocumentSelectActivity();

        public void updateToolBarName(String name);
    }

    /**
     * 历史记录实体
     */
    private class HistoryEntry {
        int scrollItem, scrollOffset;       //第一个可见的item的位置，以及距离父布局的偏移量，用于返回上级时回到同样的位置
        File dir;
        String title;
    }

    /**
     * 文件列表的item实体
     */
    private class ListItem {
        int icon;   //图标
        String title;   //文件或者路径名称
        String subtitle = "";   //副标题，为文件夹时显示文件夹，为存储卡是显示容量信息，为文件时则显示大小
        String ext = "";    //文件拓展名
        String thumb;       //文件的绝对路径
        File file;     //路径或文件本身
    }

    /**
     * 异步将文件信息存入数据库
     */
    private class SaveBookToSqlLiteTask extends AsyncTask<List<BookList>, Void, Integer> {
        private static final int FAIL = 0;
        private static final int SUCCESS = 1;
        private static final int REPEAT = 2;
        private BookList repeatBookList;

        @Override
        protected Integer doInBackground(List<BookList>... params) {
            List<BookList> bookLists = params[0];
            for (BookList bookList : bookLists) {
                List<BookList> books = DataSupport.where("bookpath = ?", bookList.getBookpath()).find(BookList.class);
                if (books.size() > 0) {
                    repeatBookList = bookList;
                    return REPEAT;
                }
            }

            try {
                DataSupport.saveAll(bookLists);
            } catch (Exception e) {
                e.printStackTrace();
                return FAIL;
            }
            return SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            String msg = "";
            switch (result) {
                case FAIL:
                    msg = "由于一些原因添加书本失败";
                    break;
                case SUCCESS:
                    msg = "导入书本成功";
                    checkItems.clear();
                    bookLists = DataSupport.findAll(BookList.class);
                    listAdapter.notifyDataSetChanged();
                    changgeCheckBookNum();
                    break;
                case REPEAT:
                    msg = "书本" + repeatBookList.getBookname() + "重复了";
                    break;
            }

            Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 目录列表适配器
     */
    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public int getItemViewType(int pos) {
            return items.get(pos).subtitle.length() > 0 ? 0 : 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {

                //TextDetailDocumentsCell为自定义控件，用于listview中的每一个item
                convertView = new TextDetailDocumentsCell(mContext);
            }
            TextDetailDocumentsCell textDetailCell = (TextDetailDocumentsCell) convertView;
            final ListItem item = items.get(position);
            if (item.icon != 0) {      //如果item的icon不为0，即有icon，在listFiles函数中，文件夹item设置了icon，而TXT文件item没有设置icon，因此这里是在判断是否是文件夹
                ((TextDetailDocumentsCell) convertView)
                        .setTextAndValueAndTypeAndThumb(item.title,
                                item.subtitle, null, null, item.icon, false);    //isStorage属性表示的是是否已经被导入
            } else {
                String type = item.ext.toUpperCase().substring(0,
                        Math.min(item.ext.length(), 4));

                ((TextDetailDocumentsCell) convertView)
                        .setTextAndValueAndTypeAndThumb(item.title,
                                item.subtitle, type, item.thumb, 0, isStorage(item.thumb));
            }

            textDetailCell.getCheckBox().setOnCheckedChangeListener(null);
            textDetailCell.setChecked(isCheck(item.thumb));
            textDetailCell.getCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {     //如过被选中，则记录下当前item
                        checkItems.add(item);
                    } else {      //如果取消选中，则从记录中删除当前item
                        removeCheckItem(item.thumb);
                    }
                    changgeCheckBookNum();
                }
            });

            return convertView;
        }

        private boolean isCheck(String path) {
            for (ListItem item : checkItems) {
                if (item.thumb.equals(path)) {
                    return true;
                }
            }
            return false;
        }

        private void removeCheckItem(String path) {
            for (ListItem item : checkItems) {
                if (item.thumb.equals(path)) {
                    checkItems.remove(item);
                    break;
                }
            }
        }

        /**
         * 是否被导入过
         *
         * @param path
         * @return
         */
        private boolean isStorage(String path) {
            boolean isStore = false;
            for (BookList bookList : bookLists) {
                if (bookList.getBookpath().equals(path)) {
                    return true;
                }
            }

            return false;
        }
    }

}

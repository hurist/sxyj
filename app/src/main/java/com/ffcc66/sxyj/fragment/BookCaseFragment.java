package com.ffcc66.sxyj.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ffcc66.sxyj.R;
import com.ffcc66.sxyj.ReadActivity;
import com.ffcc66.sxyj.activity.BookDetailActivity;
import com.ffcc66.sxyj.base.BaseFragment;
import com.ffcc66.sxyj.adapter.BookCaseAdapter;
import com.ffcc66.sxyj.dialog.DeleteDialog;
import com.ffcc66.sxyj.entity.BookList;
import com.ffcc66.sxyj.filechooser.FileChooserActivity;
import com.ffcc66.sxyj.response.Response;
import com.ffcc66.sxyj.response.entity.ResponseBook;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;



/**
 * 书架Fragment
 */
public class BookCaseFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener,AdapterView.OnItemClickListener,View.OnClickListener {

    @BindView(R.id.lvBookcase)
    ListView lvBookcase;
    @BindView(R.id.toolbarBookCase)
    Toolbar toolbar;
    @BindView(R.id.llBottomBtnBar)
    LinearLayout llBottomBtnBar;
    @BindView(R.id.btnCheckAll)
    Button btnCheckAll;
    @BindView(R.id.btnDelete)
    Button btnDelete;
    @BindView(R.id.llSearchBar)
    LinearLayout llSearchBar;
    private List<BookList> booklist = new ArrayList<BookList>();
    BookCaseAdapter bookCaseAdapter;

    private static final String TAG = "BookCaseFragment";
    private boolean isCreate = false;
    private int itemPosition;
    private BookList tempbook;


    public BookCaseFragment() {
        super();
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_book_case;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData(View view) {

        setHasOptionsMenu(true);

        toolbar.setTitle("");
        //getActivity().setActionBar(toolbar);   使用这句后无法显示menu
        toolbar.inflateMenu(R.menu.menu_bookcase);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(getContext(),R.drawable.bookcase_manager));
        toolbar.setOnMenuItemClickListener(this);
        //初始化书架数据
        booklist = DataSupport.findAll(BookList.class);
        bookCaseAdapter = new BookCaseAdapter(getActivity(), R.layout.item_fragment_book_case, booklist);
        lvBookcase.setAdapter(bookCaseAdapter);
        isCreate = true;
    }

    @Override
    protected void initListener() {
        toolbar.setOnMenuItemClickListener(this);
        lvBookcase.setOnItemClickListener(this);
        //为listview设置上下文菜单
        lvBookcase.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) contextMenuInfo;
                tempbook = (BookList) bookCaseAdapter.getItem(info.position);
                contextMenu.setHeaderTitle(tempbook.getBookname());
                if (tempbook.getType() == 1) {
                    contextMenu.add(0,0,Menu.NONE,"书籍详情");
                }
                contextMenu.add(0,1,Menu.NONE,"删除");
                contextMenu.add(0,2,Menu.NONE,"批量管理");
            }
        });

        btnCheckAll.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    protected void LazyLoad() {
        super.LazyLoad();
        Log.d(TAG, "LazyLoad: ");
        List<BookList> tempbooklist = new ArrayList<>();
        tempbooklist = DataSupport.findAll(BookList.class);
        booklist.clear();
        booklist.addAll(tempbooklist);

        if (isCreate) {
            Log.d(TAG, "LazyLoad: "+"notifyDataSetChanged");
            bookCaseAdapter.notifyDataSetChanged();
        }
    }


    //上下文菜单选择监听
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 0:
                Intent intent = new Intent(getContext(), BookDetailActivity.class);

                ResponseBook book = new ResponseBook();
                book.setId(tempbook.getBookid());
                book.setName(tempbook.getBookname());
                book.setType("");
                book.setFile(tempbook.getFileURL());
                book.setCover_img("http://192.168.137.1:8080/images/test.jpg");
                book.setCollectionnum(0);
                book.setWordcount(0);
                book.setIntroduction("");
                book.setSearchnum(0);
                book.setAuthor(tempbook.getWriter());

                intent.putExtra("bookinfo",book);
                startActivity(intent);
                break;
            case 1:
                if (lvBookcase.getChoiceMode() == ListView.CHOICE_MODE_NONE) {
                    new DeleteDialog(getContext(),tempbook,bookCaseAdapter).show();
                }
                break;
            case 2:
                lvBookcase.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                llSearchBar.setVisibility(View.GONE);
                llBottomBtnBar.setVisibility(View.VISIBLE);
                bookCaseAdapter.setChoiceModel(true);
                btnDelete.setText("删除（0）");
                btnCheckAll.setText("全选");
                lvBookcase.clearChoices();
                break;
        }

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.itemScan:
                getActivity().startActivity(new Intent(getContext(), FileChooserActivity.class));
                break;
            case R.id.itemBatchManagement:
                lvBookcase.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                llSearchBar.setVisibility(View.GONE);
                llBottomBtnBar.setVisibility(View.VISIBLE);
                bookCaseAdapter.setChoiceModel(true);
                btnDelete.setText("删除（0）");
                btnCheckAll.setText("全选");
                lvBookcase.clearChoices();
                break;
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        //首先判断listview 当前是否为多选状态，如果是则不对item 的点击事件进行响应
        if (lvBookcase.getChoiceMode() != ListView.CHOICE_MODE_MULTIPLE) {
            if (booklist.size() > position) {
                itemPosition = position;

                bookCaseAdapter.setItemToFirst(itemPosition);
                final BookList bookList = booklist.get(itemPosition);   //获取当前点击书本的图书信息
                bookList.setId(booklist.get(0).getId());   //将这本点击的书在书架中设置成第一本
                final String path = bookList.getBookpath();   //获取图书路径
                File file = new File(path);         //打开文件
                if (!file.exists()) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getContext().getString(R.string.app_name))
                            .setMessage(path + "文件不存在,是否删除该书本？")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DataSupport.deleteAll(BookList.class, "bookpath = ?", path);
                                    booklist = DataSupport.findAll(BookList.class);
                                    bookCaseAdapter.setBookList(booklist);
                                }
                            }).setCancelable(true).show();
                    return;
                }

                ReadActivity.openBook(bookList, getActivity());
            }
        } else {    //如果是多选模式


            //判断现在是否是已全选的状态，如果是，且当前item为不选中的状态，则将全选按钮重新恢复到可全选的状态
            if (btnCheckAll.getText().toString().equals("全不选") && lvBookcase.isItemChecked(position) == false) {
                btnCheckAll.setText("全选");
            }
            //统计当前有多少本书被选中
            int size = 0;
            for (int i=0; i<lvBookcase.getCheckedItemPositions().size(); i++){
                if (lvBookcase.getCheckedItemPositions().get(i)) {
                    size++;
                }
            }
            btnDelete.setText("删除（"+size+"）");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setUserVisibleHint(true);
    }

    //返回按钮监听
    public boolean onBackPressed_() {
        //如果当前还在多选状态则返回false，并关闭多选模式，否则返回true
        if (lvBookcase.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
            lvBookcase.setChoiceMode(ListView.CHOICE_MODE_NONE);
            llSearchBar.setVisibility(View.VISIBLE);
            llBottomBtnBar.setVisibility(View.GONE);
            bookCaseAdapter.setChoiceModel(false);
            lvBookcase.clearChoices();
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCheckAll:      //全选按钮
                if (btnCheckAll.getText().toString().equals("全不选")) {   //全不选操作
                    for (int i=0; i<lvBookcase.getChildCount(); i++) {
                        lvBookcase.setItemChecked(i,false);
                    }
                    btnDelete.setText("删除（0)");
                    btnCheckAll.setText("全选");
                } else {        //全选操作
                    if (lvBookcase.getChildCount() > 0) {
                        for (int i=0; i<lvBookcase.getChildCount(); i++) {
                            lvBookcase.setItemChecked(i,true);
                        }
                        btnDelete.setText("删除（"+lvBookcase.getChildCount()+")");
                        btnCheckAll.setText("全不选");
                    } else {
                        Toast.makeText(getContext(),"已经没有图书了！",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btnDelete:        //删除按钮

                //获取选中的图书
                List<BookList> tempbookLists = new ArrayList<>();
                for (int i=0; i<booklist.size(); i++) {

                    if (lvBookcase.getCheckedItemPositions().get(i)) {
                        BookList bookList = (BookList) bookCaseAdapter.getItem(i);
                        tempbookLists.add(bookList);
                    }

                }
                //判断是否有选图书
                if (tempbookLists.size() == 0) {
                    Toast.makeText(getContext(),"你没选择要删除的书哦！",Toast.LENGTH_SHORT).show();
                } else {
                    new DeleteDialog(getContext(),tempbookLists,bookCaseAdapter).show();
                    lvBookcase.clearChoices();
                    btnDelete.setText("删除（0）");
                    btnCheckAll.setText("全选");
                }
                break;
        }
    }
}

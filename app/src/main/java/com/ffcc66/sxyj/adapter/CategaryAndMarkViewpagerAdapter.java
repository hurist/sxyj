package com.ffcc66.sxyj.adapter;




import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ffcc66.sxyj.fragment.BookMarkFragment;
import com.ffcc66.sxyj.fragment.CatalogFragment;

/**
 * 书签目录viewpager适配器
 */
public class CategaryAndMarkViewpagerAdapter extends FragmentPagerAdapter {
    private CatalogFragment catalogueFragment;
    private BookMarkFragment bookMarkFragment;
    private String bookPath;
    private final String[] titles = { "目录", "书签" };

    public CategaryAndMarkViewpagerAdapter(FragmentManager fm, String bookPath) {
        super(fm);
        this.bookPath = bookPath;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (catalogueFragment == null) {
                    //  bookMarkFragment = new BookMarkFragment();
                    //创建bookMarkFragment实例时同时把需要intent中的值传入
//                    catalogueFragment = CatalogFragment
                   // bookMarkFragment = BookMarkFragment.newInstance(MarkActivity.getBookpath_intent());
                    catalogueFragment = CatalogFragment.newInstance(bookPath);
                }
                return catalogueFragment;

            case 1:
                if (bookMarkFragment == null) {
                    //catalogueFragment = new CatalogueFragment();
                  //  catalogueFragment = CatalogueFragment.newInstance(MarkActivity.getBookpath_intent());
//                    bookMarkFragment = BookMarkFragment.newInstance(MarkActivity.getBookpath_intent());
                    bookMarkFragment = BookMarkFragment.newInstance(bookPath);
                }
                return bookMarkFragment;
        }

        return null;
    }

}

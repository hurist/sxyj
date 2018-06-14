package com.ffcc66.sxyj.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

//import static android.support.constraint.Constraints.TAG;



/**
 * Created by Administrator on 2016/8/31 0031.
 */
public abstract class BaseFragment extends Fragment {

    protected boolean isVisible;//标记当前Fragment是否被用户可见
    private View rootView;
    private static final String TAG = "BaseFragment";
    /**
     * 初始化布局
     */
    protected abstract int getLayoutRes();

    protected abstract void initData(View view);

    protected abstract void initListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        rootView = view;
        Log.d(TAG, "onCreateView: ");
        // 初始化View注入
        ButterKnife.bind(this,view);
        initData(view);
        initListener();
        return view;
    }

    public View getRootView(){
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){//判断用户是否可见当前Fragment
            isVisible=true;
            onVisible();
        }else{
            isVisible=false;
            onInvisible();
        }
    }

    private void onVisible(){
        LazyLoad();//只有在用户可见的情况下我们才去加载数据
    }

    private void onInvisible(){//用户不可见的情况下不去加载任何的数据

    }

    protected void LazyLoad() {

    };  //LazyLoad

}

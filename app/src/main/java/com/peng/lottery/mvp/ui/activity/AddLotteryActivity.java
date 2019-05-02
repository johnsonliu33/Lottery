package com.peng.lottery.mvp.ui.activity;

import android.support.design.button.MaterialButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.peng.lottery.R;
import com.peng.lottery.app.config.ActionConfig;
import com.peng.lottery.app.config.ActionConfig.LotteryType;
import com.peng.lottery.app.utils.ToastUtil;
import com.peng.lottery.app.widget.LotteryLayout;
import com.peng.lottery.base.BaseActivity;
import com.peng.lottery.mvp.model.db.bean.LotteryNumber;
import com.peng.lottery.mvp.presenter.activity.AddLotteryPresenter;
import com.peng.lottery.mvp.ui.adapter.LotteryNumberBallAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.peng.lottery.app.config.ActionConfig.LotteryType.LOTTERY_TYPE_11X5;
import static com.peng.lottery.app.config.ActionConfig.LotteryType.LOTTERY_TYPE_DLT;
import static com.peng.lottery.app.config.ActionConfig.LotteryType.LOTTERY_TYPE_SSQ;

public class AddLotteryActivity extends BaseActivity<AddLotteryPresenter> {

    @BindView(R.id.spinner_type_lottery)
    AppCompatSpinner spinnerTypeLottery;
    @BindView(R.id.spinner_type_11x5)
    AppCompatSpinner spinnerType11x5;
    @BindView(R.id.lottery_ball_recycle)
    RecyclerView lotteryBallRecycle;
    @BindView(R.id.layout_lottery_number)
    LotteryLayout layoutLotteryNumber;
    @BindView(R.id.bt_clean_lottery)
    MaterialButton btCleanLottery;
    @BindView(R.id.bt_complement_lottery)
    MaterialButton btComplementLottery;
    @BindView(R.id.bt_save_lottery)
    MaterialButton btSaveLottery;

    private LotteryType mLotteryType;
    private List<LotteryNumber> mLotteryValue;
    private LotteryNumberBallAdapter mLotteryNumberBallAdapter;

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected int setLayoutResID() {
        return R.layout.activity_add_lottery;
    }

    @Override
    protected void initView() {
        super.initView();
        mActivityTitle.setText(R.string.title_add_lottery);


        // 初始化彩票号码球
        mLotteryNumberBallAdapter = new LotteryNumberBallAdapter(R.layout.item_lottery_number_ball, ActionConfig.getLotteryNumberBallList(LOTTERY_TYPE_DLT));
        mLotteryNumberBallAdapter.bindToRecyclerView(lotteryBallRecycle);
        lotteryBallRecycle.setLayoutManager(new GridLayoutManager(mActivity, 7));
        lotteryBallRecycle.setAdapter(mLotteryNumberBallAdapter);
        // 初始化彩票数据
        mLotteryType = LOTTERY_TYPE_DLT;
        mLotteryValue = new ArrayList<>();
        layoutLotteryNumber.setLotteryValue(mLotteryValue, mLotteryType.type);
    }

    @Override
    protected void initListener() {
        spinnerTypeLottery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        changeLotteryType(LOTTERY_TYPE_DLT);
                        break;
                    case 1:
                        changeLotteryType(LOTTERY_TYPE_SSQ);
                        break;
                    case 2:
                        change11x5Type();
                        changeLotteryType(LOTTERY_TYPE_11X5);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerType11x5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mLotteryType.equals(LOTTERY_TYPE_11X5)) {
                    change11x5Type();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mLotteryNumberBallAdapter.setOnItemClickListener((adapter, view, position) -> {
            LotteryNumber item = mLotteryNumberBallAdapter.getData().get(position);
            if (mPresenter.checkIsAdd(mLotteryValue, mLotteryType, item)) {
                mLotteryValue.add(item);
                mPresenter.sortList(mLotteryValue, mLotteryType);
                layoutLotteryNumber.setLotteryValue(mLotteryValue, mLotteryType.type);
            }
        });
        btCleanLottery.setOnClickListener(v -> {
            mLotteryValue.clear();
            layoutLotteryNumber.setLotteryValue(mLotteryValue, mLotteryType.type);
        });
        btComplementLottery.setOnClickListener(v -> {
            if (mLotteryValue.size() == 0) {
                ToastUtil.showToast(mActivity, "请先选几个号吧！");
                return;
            }
            if (TextUtils.isEmpty(mLotteryValue.get(0).getNumberValue())) {
                ToastUtil.showToast(mActivity, "请先清空号码！");
                return;
            }
            mPresenter.complementLottery(mLotteryValue, mLotteryType);
            layoutLotteryNumber.setLotteryValue(mLotteryValue, mLotteryType.type);
        });
        btSaveLottery.setOnClickListener(v -> {
            String label = mLotteryType.equals(LOTTERY_TYPE_11X5) ? (String) spinnerType11x5.getSelectedItem() : "";
            String result = mPresenter.saveLottery(label, mLotteryValue, mLotteryType);
            ToastUtil.showToast(mActivity, result);
        });
    }

    @Override
    protected boolean enableSlidingFinish() {
        return true;
    }

    private void changeLotteryType(LotteryType lotteryType) {
        mLotteryType = lotteryType;
        mLotteryValue.clear();
        spinnerType11x5.setVisibility(LOTTERY_TYPE_11X5.equals(lotteryType) ? View.VISIBLE : View.GONE);
        mLotteryNumberBallAdapter.setNewData(ActionConfig.getLotteryNumberBallList(lotteryType));
        layoutLotteryNumber.setLotteryValue(mLotteryValue, mLotteryType.type);
    }

    private void change11x5Type() {
        mLotteryValue.clear();
        String type11x5 = (String) spinnerType11x5.getSelectedItem();
        layoutLotteryNumber.set11x5Size(mPresenter.set11x5Type(type11x5));
    }
}
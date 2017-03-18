package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lzy.okgo.OkGo;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.models.CommonJson;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.view.customdialog.LodingDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.COMMON_CHECK_IN;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_ENTER_MEETING;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO1;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO2;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_check_in_type;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_password;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_url;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_token;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_user_email;
import static cn.edu.hfut.lilei.shareboard.utils.StringUtil.isValidMeetingPassword;
import static cn.edu.hfut.lilei.shareboard.utils.StringUtil.isValidMeetingUrl;


public class JoinMeetingActivity extends SwipeBackActivity {
    //控件
    private EditText mEtMeetingUrl, mEtMeetingPassword;
    private Button mBtnJoinMeeting;
    private LodingDialog.Builder mlodingDialog;
    //数据
    private Boolean mJoinByNumber = true;
    //上下文参数
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_meeting);
        init();
    }

    private void init() {
        mContext = this;
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setShadow(getResources().getDrawable(R.drawable.shadow),
                SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {

            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
            }

            @Override
            public void onScrollOverThreshold() {
            }
        });

        mBtnJoinMeeting = (Button) findViewById(R.id.btn_join_meeting);
        mEtMeetingUrl = (EditText) findViewById(R.id.et_meeting_number);
        mEtMeetingPassword = (EditText) findViewById(R.id.et_meeting_password);
        mBtnJoinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlodingDialog = loding(mContext, R.string.entering);
                final String pwd = mEtMeetingPassword.getText()
                        .toString()
                        .trim();
                final String meetingUrl = mEtMeetingUrl.getText()
                        .toString()
                        .trim();

                new AsyncTask<Void, Void, Integer>() {

                    @Override
                    protected Integer doInBackground(Void... voids) {
                        /**
                         * 1.检查网络状态并提醒
                         */
                        if (!NetworkUtil.isNetworkConnected(mContext)) {
                            //网络连接不可用
                            return NET_DISCONNECT;
                        }
                        /**
                         * 2.检查页面参数合法性
                         */
                        String token = (String) SharedPrefUtil.getInstance()
                                .getData(share_token, "空");

                        //如果没有token,跳转到登录界面
                        if (token.equals("空")) {
                            return -2;
                        }
                        String email = (String) SharedPrefUtil.getInstance()
                                .getData(share_user_email, "空");

                        //如果没有email
                        if (token.equals("空")) {
                            return -2;
                        }


                        if (!isValidMeetingUrl(meetingUrl)) {

                            return WRONG_FORMAT_INPUT_NO1;
                        }
                        if (!isValidMeetingPassword(pwd)) {

                            return WRONG_FORMAT_INPUT_NO2;
                        }

                        /**
                         * 3.发送
                         */

                        OkGo.post(URL_ENTER_MEETING)
                                .tag(this)
                                .params(post_meeting_check_in_type, COMMON_CHECK_IN)
                                .params(post_token, token)
                                .params(post_user_email, email)
                                .params(post_meeting_url, meetingUrl)
                                .params(post_meeting_password, StringUtil.getMD5(pwd))

                                .execute(new JsonCallback<CommonJson>() {
                                             @Override
                                             public void onSuccess(CommonJson o, Call call,
                                                                   Response response) {
                                                 if (o.getCode() == SUCCESS) {


                                                     /**
                                                      * 跳到会议界面
                                                      */
                                                     mlodingDialog.cancle();
//                                                     showToast(mContext, o.getMsg());
                                                     Intent intent = new Intent();
                                                     intent.setClass(JoinMeetingActivity.this,
                                                             MeetingActivity.class);
                                                     Bundle b = new Bundle();
                                                     b.putInt(post_meeting_check_in_type, COMMON_CHECK_IN);
                                                     b.putLong(post_meeting_url, Long.parseLong
                                                             (meetingUrl));
                                                     intent.putExtras(b);
                                                     startActivity(intent);
                                                     finish();

                                                 } else {
                                                     //提示所有错误
                                                     mlodingDialog.cancle();
                                                     showToast(mContext, o.getMsg());
                                                 }

                                             }

                                             @Override
                                             public void onError(Call call, Response response,
                                                                 Exception e) {
                                                 super.onError(call, response, e);
                                                 mlodingDialog.cancle();
                                                 showToast(mContext, R.string.system_error);
                                             }
                                         }
                                );


                        return -1;

                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        mlodingDialog.cancle();
                        switch (integer) {
                            case NET_DISCONNECT:
                                //弹出对话框，让用户开启网络
                                NetworkUtil.setNetworkMethod(mContext);
                                break;
                            case WRONG_FORMAT_INPUT_NO1:
                                showToast(mContext, R.string.can_not_recognize_meeting_url);
                                break;
                            case WRONG_FORMAT_INPUT_NO2:
                                showToast(mContext, R.string.can_not_recognize_meeting_password2);
                                break;
                            case -1:
                                break;
                            case -2:
                                showToast(mContext, R.string.please_relogin);
                                break;
                            default:
//                                    showToast(mContext, R.string.system_error);
                                break;
                        }
                    }
                }.execute();

            }
        });

    }
}

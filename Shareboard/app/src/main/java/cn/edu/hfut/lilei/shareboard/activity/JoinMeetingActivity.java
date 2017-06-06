package cn.edu.hfut.lilei.shareboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lzy.okgo.OkGo;

import cn.edu.hfut.lilei.shareboard.JsonEnity.CommonJson;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.callback.JsonCallback;
import cn.edu.hfut.lilei.shareboard.utils.MyAppUtil;
import cn.edu.hfut.lilei.shareboard.utils.NetworkUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.widget.customdialog.LodingDialog;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Response;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.loding;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;
import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showToast;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.COMMON_CHECK_IN;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.NET_DISCONNECT;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.SUCCESS;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.URL_ENTER_MEETING;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO1;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.WRONG_FORMAT_INPUT_NO2;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_check_in_type;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_host_email;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_is_drawable;
import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.post_meeting_is_talkable;
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
    private String pwd, meetingUrl;
    //上下文参数
    private Context mContext;
    private ImageView mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_meeting);
        init();
    }

    private void init() {
        mContext = this;


        Intent i = getIntent();
        if (i != null && i.getExtras() != null) {
            LinearLayout root = (LinearLayout) findViewById(R.id.rootView);
            root.setVisibility(View.INVISIBLE);
            meetingUrl = i.getExtras()
                    .getString(post_meeting_url);

            showLog("################joinmeetingactivity " + meetingUrl);
            pwd = i.getExtras()
                    .getString(post_meeting_password);
            joinMeeting(1);

        } else {
            mBtnBack = (ImageView) findViewById(R.id.img_join_goback);
            mBtnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.my_deepyellow));
            }
            SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
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
            //监听输入字符,格式化输出

            mEtMeetingUrl.addTextChangedListener(new TextWatcher() {
                                                     private int isAdd;

                                                     @Override
                                                     public void beforeTextChanged(
                                                             CharSequence s, int start,
                                                             int count, int after) {
                                                         if (after == 1) {//增加
                                                             isAdd = 1;
                                                             showLog("增加,,,");
                                                         } else
                                                             if (after == 12) {
                                                                 isAdd = 2;
                                                                 showLog("复制粘贴,,,");
                                                             } else {
                                                                 isAdd = 0;
                                                                 showLog("减少,,,");
                                                             }
                                                     }

                                                     @Override
                                                     public void onTextChanged(
                                                             CharSequence s, int start,
                                                             int before, int count) {
                                                     }

                                                     @Override
                                                     public void afterTextChanged(
                                                             Editable s) {
                                                         if (isAdd == 1) {
                                                             if (null != mEtMeetingUrl) {
                                                                 String str = s.toString();
                                                                 if (!str.endsWith(" ")) {
                                                                     int length = s.length();
                                                                     if (length == 4 || length == 9) {
                                                                         String str1 = str + "-";
                                                                         //手动添加-
                                                                         mEtMeetingUrl.setText(str1);
                                                                         mEtMeetingUrl.setSelection(str1.length());//光标移到最右边
                                                                     }
                                                                 }
                                                             }
                                                         } else
                                                             if (isAdd == 2) {
                                                                 String str = s.toString();
                                                                 if (str.length() == 12) {
                                                                     //分成3串数字
                                                                     String part1 = str.substring(0, 4);
                                                                     String part2 = str.substring(4, 8);
                                                                     String part3 = str.substring(8,
                                                                             12);
                                                                     String finalStr = part1 + "-" + part2 + "-" + part3;
                                                                     //手动添加-
                                                                     mEtMeetingUrl.setText(finalStr);
                                                                     mEtMeetingUrl.setSelection(finalStr.length());//光标移到最右边
                                                                 }
                                                             }
                                                     }
                                                 }

            );
            mEtMeetingPassword = (EditText) findViewById(R.id.et_meeting_password);
            mBtnJoinMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    joinMeeting(0);

                }
            });
        }


    }

    public void joinMeeting(final int type) {

        mlodingDialog = loding(mContext, R.string.entering);
        if (type == 0) {
            pwd = mEtMeetingPassword.getText()
                    .toString()
                    .trim();
            meetingUrl = mEtMeetingUrl.getText()
                    .toString()
                    .trim()
                    .replaceAll("-", "");
        }

        if (type == 0) {

            MyAppUtil.changeBtnDisable(mBtnJoinMeeting);
        }
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
                String encryptingCode;
                try {
                    String masterPassword = "L1x#tvh_";
                    encryptingCode =
                            StringUtil.encrypt_security(masterPassword,
                                    pwd);
                    showLog("encrypt_security(masterPassword,mpassword) error");
                } catch (Exception e) {
                    e.printStackTrace();
                    return -2;
                }
                showLog("加密后" + encryptingCode);


                /**
                 * 3.发送
                 */

                OkGo.post(URL_ENTER_MEETING)
                        .tag(this)
                        .params(post_meeting_check_in_type, COMMON_CHECK_IN)
                        .params(post_token, token)
                        .params(post_user_email, email)
                        .params(post_meeting_url, meetingUrl)
                        .params(post_meeting_password, encryptingCode)
//                        .params(post_meeting_password, StringUtil.getMD5(pwd))

                        .execute(new JsonCallback<CommonJson>() {
                                     @Override
                                     public void onSuccess(CommonJson o, Call call,
                                                           Response response) {
                                         if (o.getCode() == SUCCESS) {
                                             int data = o.getData();
                                             boolean isDrawable, isTalkable;
                                             switch (data) {
                                                 case 0:
                                                     isDrawable = false;
                                                     isTalkable = false;
                                                     break;
                                                 case 1:
                                                     isDrawable = false;
                                                     isTalkable = true;
                                                     break;
                                                 case 10:
                                                     isDrawable = true;
                                                     isTalkable = false;
                                                     break;
                                                 case 11:
                                                     isDrawable = true;
                                                     isTalkable = true;
                                                     break;
                                                 default:
                                                     isDrawable = true;
                                                     isTalkable = true;
                                                     break;
                                             }


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
                                             b.putString(post_meeting_host_email, o.getMsg());
                                             b.putBoolean(post_meeting_is_drawable, isDrawable);
                                             b.putBoolean(post_meeting_is_talkable, isTalkable);
                                             //
                                             b.putString(post_meeting_url,
                                                     meetingUrl);
                                             b.putString(post_meeting_password, pwd);

                                             intent.putExtras(b);
                                             startActivity(intent);
                                             finish();

                                         } else {
                                             //提示所有错误

                                             if (type == 1) {
                                                 Intent intent = new Intent();
                                                 intent.setClass(JoinMeetingActivity.this,
                                                         MainActivity.class);
                                                 startActivity(intent);
                                                 finish();
                                             } else {
                                                 mlodingDialog.cancle();
                                                 if (type == 0) {
                                                     MyAppUtil.changeBtnClickable(mBtnJoinMeeting,
                                                             R.drawable.btn_yellow);
                                                 }
                                             }
                                             showToast(mContext, o.getMsg());
                                         }

                                     }

                                     @Override
                                     public void onError(Call call, Response response,
                                                         Exception e) {
                                         super.onError(call, response, e);
                                         mlodingDialog.cancle();
                                         if (type == 0) {
                                             MyAppUtil.changeBtnClickable(mBtnJoinMeeting,
                                                     R.drawable.btn_yellow);
                                         }
//                                                 showToast(mContext, R.string.system_error);
                                     }
                                 }
                        );


                return -1;

            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                mlodingDialog.cancle();
                if (type == 0) {
                    MyAppUtil.changeBtnClickable(mBtnJoinMeeting, R.drawable.btn_yellow);
                }
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
}

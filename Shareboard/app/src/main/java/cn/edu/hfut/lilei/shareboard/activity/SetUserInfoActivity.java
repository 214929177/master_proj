package cn.edu.hfut.lilei.shareboard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.edu.hfut.lilei.shareboard.R;


public class SetUserInfoActivity extends Activity {
    private EditText mEtEmail;
    private Button mBtnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_userinfo);
        mBtnComplete= (Button) findViewById(R.id.btn_setuserinfo_complete);
        mBtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(SetUserInfoActivity.this, ResetPasswordActivity.class);
//                startActivity(intent);
            }
        });



    }
}

package cn.edu.hfut.lilei.shareboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.SharedPrefUtil;

import static cn.edu.hfut.lilei.shareboard.utils.SettingUtil.share_new_msg_num;

public class MeetingIndexAdapter extends BaseAdapter {
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public MeetingIndexAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 组件集合，对应list.xml中的控件
     */
    public final class Zujian {
        public ImageView image;
        public TextView title;
        public Button view;
        public TextView msg_num;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Zujian zujian = null;
        if (convertView == null) {
            zujian = new Zujian();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.listitem_meeting_index, null);
            zujian.image = (ImageView) convertView.findViewById(R.id.image);
            zujian.title = (TextView) convertView.findViewById(R.id.tv_title);
            zujian.msg_num = (TextView) convertView.findViewById(R.id.tv_msg_num);

            convertView.setTag(zujian);
        } else {
            zujian = (Zujian) convertView.getTag();
        }
        //绑定数据
        ImageUtil.load(context, (Integer) data.get(position)
                .get("image"), (Integer) data.get(position)
                .get("image"), zujian.image);
        zujian.title.setText((String) data.get(position)
                .get("tvMeetingTheme"));
        if (position == 2) {//系统消息栏
//            SharedPrefUtil.getInstance()
//                    .deleteData(share_new_msg_num);
            int currenUnreadNews = (int) SharedPrefUtil.getInstance()
                    .getData(share_new_msg_num, 0);
            if (currenUnreadNews == 0) {
                zujian.msg_num.setVisibility(View.GONE);
            } else {
                zujian.msg_num.setVisibility(View.VISIBLE);
                if (currenUnreadNews < 100) {
                    zujian.msg_num.setText(currenUnreadNews + "");
                } else {
                    zujian.msg_num.setText("99+");
                }
            }
        }


        return convertView;
    }

}

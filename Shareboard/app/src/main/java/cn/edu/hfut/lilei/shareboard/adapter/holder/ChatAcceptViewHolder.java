package cn.edu.hfut.lilei.shareboard.adapter.holder;

import android.os.Handler;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.edu.hfut.lilei.shareboard.R;
import cn.edu.hfut.lilei.shareboard.adapter.ChatAdapter;
import cn.edu.hfut.lilei.shareboard.base.MyApplication;
import cn.edu.hfut.lilei.shareboard.model.MessageInfo;
import cn.edu.hfut.lilei.shareboard.utils.DateTimeUtil;
import cn.edu.hfut.lilei.shareboard.utils.ImageUtil;
import cn.edu.hfut.lilei.shareboard.utils.StringUtil;
import cn.edu.hfut.lilei.shareboard.utils.Utils;
import cn.edu.hfut.lilei.shareboard.widget.BubbleImageView;
import cn.edu.hfut.lilei.shareboard.widget.BubbleLinearLayout;
import cn.edu.hfut.lilei.shareboard.widget.GifTextView;


public class ChatAcceptViewHolder extends BaseViewHolder<MessageInfo> {

    @Bind(R.id.chat_item_date)
    TextView chatItemDate;
    @Bind(R.id.chat_item_name)
    TextView chatItemName;
    @Bind(R.id.chat_item_header)
    AvatarImageView chatItemHeader;
    @Bind(R.id.chat_item_content_text)
    GifTextView chatItemContentText;
    @Bind(R.id.chat_item_content_image)
    BubbleImageView chatItemContentImage;
    @Bind(R.id.chat_item_voice)
    ImageView chatItemVoice;
    @Bind(R.id.chat_item_layout_content)
    BubbleLinearLayout chatItemLayoutContent;
    @Bind(R.id.chat_item_voice_time)
    TextView chatItemVoiceTime;
    private ChatAdapter.onItemClickListener onItemClickListener;
    private Handler handler;
    private RelativeLayout.LayoutParams layoutParams;

    public ChatAcceptViewHolder(ViewGroup parent,
                                ChatAdapter.onItemClickListener onItemClickListener,
                                Handler handler) {
        super(parent, R.layout.item_chat_accept);
        ButterKnife.bind(this, itemView);
        this.onItemClickListener = onItemClickListener;
        this.handler = handler;
        layoutParams = (RelativeLayout.LayoutParams) chatItemLayoutContent.getLayoutParams();
    }

    @Override
    public void setData(MessageInfo data) {

        chatItemName.setText(data.getFamilyName() + " " + data.getGivenyName());

        chatItemDate.setText(data.getTime() != null ?
                DateTimeUtil.getChatDateTime(Long.valueOf(data.getTime())) : "");
        chatItemHeader.setTextAndColor(data.getGivenyName(), R.color.lightgreen);
        Glide.with(getContext())
                .load(data.getHeader())
                .into(chatItemHeader);
        chatItemHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onHeaderClick(getDataPosition());
            }
        });
        if (data.getContent() != null) {
            try {
                String encryptingCode = new String(Base64.decode(data.getContent()
                        .getBytes(), Base64.NO_WRAP));

                String masterPassword = "L1x#tvh_";
                String decryptingCode =
                        StringUtil.decrypt_security(masterPassword, encryptingCode);
                Log.i("解密结果", decryptingCode);
                chatItemContentText.setSpanText(handler, decryptingCode, true);

                chatItemVoice.setVisibility(View.GONE);
                chatItemContentText.setVisibility(View.VISIBLE);
                chatItemLayoutContent.setVisibility(View.VISIBLE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemContentImage.setVisibility(View.GONE);
                TextPaint paint = chatItemContentText.getPaint();
                // 计算最长的一行的字符串长度

                int maxlLen = 0;
                String[] lens = chatItemContentText.getText()
                        .toString()
                        .trim()
                        .split("\\r?\\n");
                for (int i = 0; i < lens.length; i++) {
                    if (lens[i].length() > maxlLen) {
                        maxlLen = lens[i].length();
                    }
                }

                if (maxlLen < MyApplication.screenWidth - Utils.dp2px(getContext(), 80)) {
                    layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    layoutParams.width = len + Utils.dp2px(getContext(), 20);
                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                } else {
                    layoutParams.width = MyApplication.screenWidth - Utils.dp2px(getContext(), 150);
                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                }
                chatItemLayoutContent.setLayoutParams(layoutParams);

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else
            if (data.getImageUrl() != null) {
                chatItemVoice.setVisibility(View.GONE);
                chatItemLayoutContent.setVisibility(View.GONE);
                chatItemVoiceTime.setVisibility(View.GONE);
                chatItemContentText.setVisibility(View.GONE);
                chatItemContentImage.setVisibility(View.VISIBLE);
//                Glide.with(getContext())
//                        .load(data.getImageUrl())
//                        .into(chatItemContentImage);

                ImageUtil.loadIntoUseFitWidth(getContext(), data.getImageUrl(),
                        R.drawable.bg_rectangle_white,
                        chatItemContentImage);

                chatItemContentImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onImageClick(chatItemContentImage, getDataPosition());
                    }
                });
                layoutParams.width = Utils.dp2px(getContext(), 120);
                layoutParams.height = Utils.dp2px(getContext(), 48);
                chatItemLayoutContent.setLayoutParams(layoutParams);
            } else
                if (data.getFilepath() != null) {
                    chatItemVoice.setVisibility(View.VISIBLE);
                    chatItemLayoutContent.setVisibility(View.VISIBLE);
                    chatItemContentText.setVisibility(View.GONE);
                    chatItemVoiceTime.setVisibility(View.VISIBLE);
                    chatItemContentImage.setVisibility(View.GONE);
                    chatItemVoiceTime.setText(Utils.formatTime(data.getVoiceTime()));
                    chatItemLayoutContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClickListener.onVoiceClick(chatItemVoice, getDataPosition());
                        }
                    });
                    layoutParams.width = Utils.dp2px(getContext(), 120);
                    layoutParams.height = Utils.dp2px(getContext(), 48);
                    chatItemLayoutContent.setLayoutParams(layoutParams);
                }
    }
}

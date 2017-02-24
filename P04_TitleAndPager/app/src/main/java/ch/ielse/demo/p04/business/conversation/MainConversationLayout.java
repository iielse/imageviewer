package ch.ielse.demo.p04.business.conversation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import ch.ielse.demo.p04.R;

public class MainConversationLayout extends LinearLayout implements View.OnClickListener {

    public MainConversationLayout(Context context) {
        this(context, null);
    }

    public MainConversationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_main_conversation, this);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}

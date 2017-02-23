package ch.ielse.demo.p04.business.contact;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Stack;

import ch.ielse.demo.p04.R;
import ch.ielse.demo.p04.utils.Logger;

public class StickyGroupLayout extends LinearLayout {

    private ViewPropertyAnimator animExpansionToggled;

    private TextView tTitle;
    private ImageView iGroup;

    private Stack<Group> groupStack = new Stack<>();

    public StickyGroupLayout(Context context) {
        this(context, null);
    }

    public StickyGroupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.recycler_main_contact_group, this);
        tTitle = (TextView) findViewById(R.id.t_title);
        tTitle.setTextColor(Color.RED);
        iGroup = (ImageView) findViewById(R.id.i_group);
    }

    private void refresh(Group group) {
        tTitle.setText(group.getTitle());
    }

    public void push(Group group) {
        Logger.e("AAA push " + group.getTitle());
        if (!groupStack.contains(group)) {
            groupStack.push(group);
        }
        setVisibility(View.VISIBLE);
        refresh(group);
    }

    public void pop(Group group) {
        Logger.e("AAA pop " + group.getTitle());
        if (!groupStack.isEmpty() && groupStack.peek() == group) {
            groupStack.pop();
        }

        if (!groupStack.isEmpty()) {
            setVisibility(View.VISIBLE);
            refresh(groupStack.peek());
        } else {
            setVisibility(View.GONE);
        }
    }
}

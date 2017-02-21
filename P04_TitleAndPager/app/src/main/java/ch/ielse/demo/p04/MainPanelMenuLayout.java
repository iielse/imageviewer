package ch.ielse.demo.p04;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainPanelMenuLayout extends FrameLayout implements View.OnClickListener {

    public static final String CONVERSATION = "conversation";
    public static final String CONTACT = "contact";
    public static final String PLUGIN = "plugin";

    private View lConversation, lContact, lPlugin;
    private ImageView iConversation, iContact, iPlugin;
    private final List<Callback> mCallbacks = new ArrayList<>();

    public MainPanelMenuLayout(Context context) {
        this(context, null);
    }

    public MainPanelMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_main_panel_menu, this);
        (lConversation = findViewById(R.id.l_conversation)).setOnClickListener(this);
        (lContact = findViewById(R.id.l_contact)).setOnClickListener(this);
        (lPlugin = findViewById(R.id.l_plugin)).setOnClickListener(this);
        iConversation = (ImageView) findViewById(R.id.i_conversation);
        iContact = (ImageView) findViewById(R.id.i_contact);
        iPlugin = (ImageView) findViewById(R.id.i_plugin);
    }

    @Override
    public void onClick(View v) {
        String clicked = "";
        if (v == lConversation) {
            clicked = CONVERSATION;
        } else if (v == lContact) {
            clicked = CONTACT;
        } else if (v == lPlugin) {
            clicked = PLUGIN;
        }

        refresh(clicked);
        notifyCallback(clicked);
    }

    public interface Callback {
        void onPanelMenuClick(String which);
    }

    public void addCallback(Callback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    private void notifyCallback(String value) {
        if (!TextUtils.isEmpty(value)) {
            for (Callback cb : mCallbacks) cb.onPanelMenuClick(value);
        }
    }

    private void refresh(String value) {
        if (CONVERSATION.equals(value)) {
            iConversation.setImageResource(R.mipmap.skin_tab_icon_conversation_selected);
            iContact.setImageResource(R.mipmap.skin_tab_icon_contact_normal);
            iPlugin.setImageResource(R.mipmap.skin_tab_icon_plugin_normal);
        } else if (CONTACT.equals(value)) {
            iConversation.setImageResource(R.mipmap.skin_tab_icon_conversation_normal);
            iContact.setImageResource(R.mipmap.skin_tab_icon_contact_selected);
            iPlugin.setImageResource(R.mipmap.skin_tab_icon_plugin_normal);
        } else if (PLUGIN.equals(value)) {
            iConversation.setImageResource(R.mipmap.skin_tab_icon_conversation_normal);
            iContact.setImageResource(R.mipmap.skin_tab_icon_contact_normal);
            iPlugin.setImageResource(R.mipmap.skin_tab_icon_plugin_selected);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCallbacks.clear();
    }
}

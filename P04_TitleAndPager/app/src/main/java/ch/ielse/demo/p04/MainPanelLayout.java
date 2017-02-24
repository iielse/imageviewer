package ch.ielse.demo.p04;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ch.ielse.demo.p04.business.contact.ContactTitleLayout;
import ch.ielse.demo.p04.business.contact.MainContactLayout;
import ch.ielse.demo.p04.business.conversation.ConversationTitleLayout;
import ch.ielse.demo.p04.business.conversation.MainConversationLayout;
import ch.ielse.demo.p04.business.plugin.MainPluginLayout;
import ch.ielse.demo.p04.business.plugin.PluginTitleLayout;
import ch.ielse.view.stack.TitleLayout;

public class MainPanelLayout extends LinearLayout implements MainPanelMenuLayout.Callback {
    private MainPanelMenuLayout lMainPanelMenu;

    private TitleLayout lTitle;
    private ContactTitleLayout lContactTitle;
    private PluginTitleLayout lPluginTitle;
    private ConversationTitleLayout lConversationTitle;

    private FrameLayout lMainContent;
    private MainContactLayout lMainContact;
    private MainPluginLayout lMainPlugin;
    private MainConversationLayout lMainConversation;


    public MainPanelLayout(Context context) {
        this(context, null);
    }

    public MainPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_main_panel, this);


        (lMainPanelMenu = (MainPanelMenuLayout) findViewById(R.id.l_main_panel_menu)).addCallback(this);
        lMainContent = (FrameLayout) findViewById(R.id.l_main_content);

        lMainContent.addView(lMainPlugin = new MainPluginLayout(context));
        lMainPlugin.setVisibility(View.GONE);
        lMainContent.addView(lMainContact = new MainContactLayout(context));
        lMainContact.setVisibility(View.GONE);
        lMainContent.addView(lMainConversation = new MainConversationLayout(context));

        lContactTitle = new ContactTitleLayout(context);
        lPluginTitle = new PluginTitleLayout(context);
        lConversationTitle = new ConversationTitleLayout(context);

    }

    @Override
    public void onPanelMenuClick(String which) {
        if (MainPanelMenuLayout.CONVERSATION.equals(which)) {
            lMainContact.setVisibility(View.GONE);
            lMainPlugin.setVisibility(View.GONE);
            lMainConversation.setVisibility(View.VISIBLE);
            lTitle.put(lConversationTitle);
        } else if (MainPanelMenuLayout.CONTACT.equals(which)) {
            lMainConversation.setVisibility(View.GONE);
            lMainPlugin.setVisibility(View.GONE);
            lMainContact.setVisibility(View.VISIBLE);
            lTitle.put(lContactTitle);
        } else if (MainPanelMenuLayout.PLUGIN.equals(which)) {
            lMainConversation.setVisibility(View.GONE);
            lMainContact.setVisibility(View.GONE);
            lMainPlugin.setVisibility(View.VISIBLE);
            lTitle.put(lPluginTitle);
        }
    }

    public MainPanelLayout attach(TitleLayout v) {
        lTitle = v;
        lTitle.push(lConversationTitle);
        return this;
    }

}

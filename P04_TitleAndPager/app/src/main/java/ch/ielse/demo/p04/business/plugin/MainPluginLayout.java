package ch.ielse.demo.p04.business.plugin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import ch.ielse.demo.p04.R;

public class MainPluginLayout extends LinearLayout implements View.OnClickListener{

    public MainPluginLayout(Context context) {
        this(context, null);
    }

    public MainPluginLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_main_plugin, this);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}

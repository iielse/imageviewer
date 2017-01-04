package ch.ielse.demo.p01;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ch.ielse.demo.p01.utils.FallDownToast;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button bShowMessage, bShowError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bShowMessage = (Button) findViewById(R.id.b_show_message);
        bShowMessage.setOnClickListener(this);

        bShowError = (Button) findViewById(R.id.b_show_error);
        bShowError.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == bShowMessage) {
            new FallDownToast.Builder(this).setMessage("Test Toast").create().show();
        } else if (v == bShowError) {
            new FallDownToast.Builder(this).setIcon(R.mipmap.toast_error).setMessage("some thing error")
                    .setBackgroundColor(0xFFE3E3E3).setMessageColor(0xFFFF3366).create().show();
        }
    }
}

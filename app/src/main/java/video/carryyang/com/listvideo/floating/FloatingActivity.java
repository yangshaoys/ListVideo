package video.carryyang.com.listvideo.floating;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import video.carryyang.com.listvideo.R;

/**
 * 主类
 * @author Frank
 * @version 1.0
 * Create by 2015.11.17
 */
public class FloatingActivity extends ActionBarActivity implements View.OnClickListener{
	private Context context;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_main);
        context = this;
    }
    
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.player:
			Toast.makeText(context, "click", Toast.LENGTH_LONG).show();
			Intent playerIntent = new Intent(context, PlayerVideoService.class);
			playerIntent.setAction("play");
			startService(playerIntent);
			break;
		case R.id.close:
			Toast.makeText(context, "click", Toast.LENGTH_LONG).show();
			Intent closeIntent = new Intent(context, PlayerVideoService.class);
			closeIntent.setAction("close");
			startService(closeIntent);
			break;
		case R.id.hiden:
			Toast.makeText(context, "click", Toast.LENGTH_LONG).show();
			Intent hidenIntent = new Intent(context, PlayerVideoService.class);
			hidenIntent.setAction("removeWindow");
			startService(hidenIntent);
			break;
		case R.id.show:
			Toast.makeText(context, "click", Toast.LENGTH_LONG).show();
			Intent addIntent = new Intent(context, PlayerVideoService.class);
			addIntent.setAction("adddWindow");
			startService(addIntent);
			break;
		default:
			break;
		}
	}

}

package com.airtops.carbon.plane.base;

import com.airtops.carbon.plane.view.HttpDialogFragment;
import com.airtops.carbon.plane.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by wangning;
 * Complemented by dingjie.
 * */
public abstract class BaseActivity extends Activity {
	private HttpDialogFragment httpDialog;
	public long START_TIME, END_TIME, TIME_LAST, CURRENT_TOTAL_TIME;
	public SharedPreferences sharedPreferences;
	public long waitTime = 2000;
	public long touchTime = 0;
	public SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * @Title showHttpDialog
	 * @Description 显示一个只有进度条的Http请求对话框。
	 */
	public void showHttpDialog() {
		httpDialog = HttpDialogFragment.newInstance();
		httpDialog.show(getFragmentManager(), "HttpDialogFragment");
	}
	
	/**
	 * @Title showHttpDialog
	 * @Description 显示一个带文字的Http请求对话框。
	 * @param String
	 *            对话框中的文字。
	 */
	public void showHttpDialog(String text) {
		httpDialog = HttpDialogFragment.newInstance(text);
		httpDialog.show(getFragmentManager(), "HttpDialogFragment");
	}
	
	/**
	 * @Title dismissHttpDialog
	 * @Description 不显示Http请求对话框。
	 */
	public void dismissHttpDialog() {
		if (httpDialog != null) {
			httpDialog.dismiss();
			httpDialog = null;
		}
	}

	public SharedPreferences getSharedPreferences() {
		sharedPreferences = getSharedPreferences("TIME", Activity.MODE_PRIVATE);
		return sharedPreferences;
	}

	public void showToast(String str) {
		Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_base, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		} else if (id == android.R.id.home) {
			this.finish();
		}

		return super.onOptionsItemSelected(item);
	}
}

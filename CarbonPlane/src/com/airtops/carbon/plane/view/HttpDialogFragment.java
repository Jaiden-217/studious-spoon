/**   
 * @Title HttpDialogFragment.java
 * @Package com.airtops.rotor.jingjing.core.network
 * @Description Http请求等待对话框。
 * @author WuFuchang_Airtops
 * @date 2015-9-3 09:03:00
 * @version V1.0   
 */

package com.airtops.carbon.plane.view;

import com.airtops.carbon.plane.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HttpDialogFragment extends DialogFragment {

	private String message = "";
	private int messageId = 0;

	public static HttpDialogFragment newInstance(String text) {
		HttpDialogFragment f = new HttpDialogFragment();
		f.setMessage(text);
		return f;
	}

	public static HttpDialogFragment newInstance(int text) {
		HttpDialogFragment f = new HttpDialogFragment();
		f.setMessage(text);
		return f;
	}

	public static HttpDialogFragment newInstance() {
		return new HttpDialogFragment();
	}

	private void setMessage(String text) {
		message = text;
	}

	private void setMessage(int text_id) {
		messageId = text_id;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, R.style.HttpDialogStyle);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_http_waitting,
				container, false);
		TextView messageTv = (TextView) rootView.findViewById(R.id.messageTv);
		if (messageId > 0) {
			messageTv.setText(messageId);
		} else {
			messageTv.setText(message);
		}
		return rootView;
	}
}

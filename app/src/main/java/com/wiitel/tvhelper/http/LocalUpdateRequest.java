package com.wiitel.tvhelper.http;


import android.content.Context;

public class LocalUpdateRequest extends LocalAbstractRequest {

	public static String EPG_SERVER_ADDRESS = "http://service.cdn.wiitelott.com";
	public static String UPDATE_URL = EPG_SERVER_ADDRESS + "/epg/web/update.json";

	public LocalUpdateRequest(Context context) {
		super(context,false,false);
		url = UPDATE_URL;
	}

	@Override
	public RespData localRequestData(Context context) {
		return null;
	}


	@Override
	public void notifyData(RespData data) {
		if (null != listener) {
			((UpdateListListener) listener).success((UpdateData) data);
		}
	}

	@Override
	public RespData getData(String msg) {
		UpdateData data = new UpdateData();
		data.parse(msg);
		return data;
	}

	public interface UpdateListListener extends Listener {
		public void success(UpdateData data);
	}
}

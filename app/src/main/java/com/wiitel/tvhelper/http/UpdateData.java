package com.wiitel.tvhelper.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class UpdateData extends RespData {

	private Update data;

	public Update getData() {
		return data;
	}

	public void setData(Update data) {
		this.data = data;
	}

	@Override
	public void parse(String json) {
		Gson g = new Gson();
		Type lt = new TypeToken<Update>() {
		}.getType();
		data = g.fromJson(json, lt);
	}

	@Override
	public String toString() {
		return data.toString();
	}

}

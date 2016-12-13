package com.android.volley.manager;

import com.android.volley.toolbox.NetworkImageView;

import android.content.Context;
import android.util.AttributeSet;

/**
 * WebImageView extends from NetworkImageView
 * 
 * @author panxw
 * 
 */
public class WebImageView extends NetworkImageView {

	public WebImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WebImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebImageView(Context context) {
		super(context);
	}
}

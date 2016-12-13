/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.InternalUtils;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Cache.Entry;
import com.android.volley.http.HttpEntity;
import com.android.volley.http.HttpResponse;

import android.os.SystemClock;

/**
 * A network performing Volley requests over an {@link HttpStack}.
 */
public class BasicNetwork implements Network {
	protected static final boolean DEBUG = VolleyLog.DEBUG;

	private static int SLOW_REQUEST_THRESHOLD_MS = 3000;

	private static int DEFAULT_POOL_SIZE = 4096;

	protected final HttpStack mHttpStack;

	protected final ByteArrayPool mPool;

	/**
	 * @param httpStack
	 *            HTTP stack to be used
	 */
	public BasicNetwork(HttpStack httpStack) {
		// If a pool isn't passed in, then build a small default pool that will
		// give us a lot of
		// benefit and not use too much memory.
		this(httpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
	}

	/**
	 * @param httpStack
	 *            HTTP stack to be used
	 * @param pool
	 *            a buffer pool that improves GC performance in copy operations
	 */
	public BasicNetwork(HttpStack httpStack, ByteArrayPool pool) {
		mHttpStack = httpStack;
		mPool = pool;
	}

	@Override
	public NetworkResponse performRequest(Request<?> request)
			throws VolleyError {
		long requestStart = SystemClock.elapsedRealtime();
		while (true) {
			HttpResponse httpResponse = null;
			byte[] responseContents = null;
			Map<String, String> responseHeaders = new HashMap<String, String>();
			try {
				// Gather headers.
				Map<String, String> headers = new HashMap<String, String>();
				addCacheHeaders(headers, request.getCacheEntry());

				httpResponse = mHttpStack.performRequest(request, headers);
				responseHeaders.putAll(httpResponse.getAllHeaders());
				int statusCode = httpResponse.getResponseCode();
				// Handle cache validation.
				if (statusCode == HttpResponse.SC_NOT_MODIFIED) {
					Entry entry = request.getCacheEntry();
					if (entry == null) {
						return new NetworkResponse(
								HttpResponse.SC_NOT_MODIFIED, null,
								responseHeaders, true,
								SystemClock.elapsedRealtime() - requestStart);
					}

					// A HTTP 304 response does not have all header fields. We
					// have to use the header fields from the cache entry plus
					// the new ones from the response.
					// http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
					entry.responseHeaders.putAll(responseHeaders);
					return new NetworkResponse(HttpResponse.SC_NOT_MODIFIED,
							entry.data, entry.responseHeaders, true,
							SystemClock.elapsedRealtime() - requestStart);
				}

				// Handle moved resources
				if (statusCode == HttpResponse.SC_MOVED_PERMANENTLY
						|| statusCode == HttpResponse.SC_MOVED_TEMPORARILY) {
					String newUrl = responseHeaders.get("Location");
					request.setRedirectUrl(newUrl);
				}

				// Some responses such as 204s do not have content. We must
				// check.
				if (httpResponse.getEntity() != null) {
					responseContents = entityToBytes(httpResponse.getEntity());
				} else {
					// Add 0 byte response as a way of honestly representing a
					// no-content request.
					responseContents = new byte[0];
				}

				// if the request is slow, log it.
				long requestLifetime = SystemClock.elapsedRealtime()
						- requestStart;
				logSlowRequests(requestLifetime, request, responseContents,
						httpResponse);

				if (statusCode < 200 || statusCode > 299) {
					throw new IOException();
				}
				return new NetworkResponse(statusCode, responseContents,
						responseHeaders, false, SystemClock.elapsedRealtime()
								- requestStart);
			} catch (SocketTimeoutException e) {
				attemptRetryOnException("socket", request, new TimeoutError());
			} catch (MalformedURLException e) {
				throw new RuntimeException("Bad URL " + request.getUrl(), e);
			} catch (IOException e) {
				int statusCode = 0;
				NetworkResponse networkResponse = null;
				if (httpResponse != null) {
					statusCode = httpResponse.getResponseCode();
				} else {
					throw new NoConnectionError(e);
				}
				if (statusCode == HttpResponse.SC_MOVED_PERMANENTLY
						|| statusCode == HttpResponse.SC_MOVED_TEMPORARILY) {
					VolleyLog.e("Request at %s has been redirected to %s",
							request.getOriginUrl(), request.getUrl());
				} else {
					VolleyLog.e("Unexpected response code %d for %s",
							statusCode, request.getUrl());
				}
				if (responseContents != null) {
					networkResponse = new NetworkResponse(statusCode,
							responseContents, responseHeaders, false,
							SystemClock.elapsedRealtime() - requestStart);
					if (statusCode == HttpResponse.SC_UNAUTHORIZED
							|| statusCode == HttpResponse.SC_FORBIDDEN) {
						attemptRetryOnException("auth", request,
								new AuthFailureError(networkResponse));
					} else if (statusCode == HttpResponse.SC_MOVED_PERMANENTLY
							|| statusCode == HttpResponse.SC_MOVED_TEMPORARILY) {
						attemptRetryOnException("redirect", request,
								new AuthFailureError(networkResponse));
					} else {
						// TODO: Only throw ServerError for 5xx status codes.
						throw new ServerError(networkResponse);
					}
				} else {
					throw new NetworkError(networkResponse);
				}
			}
		}
	}

	/**
	 * Logs requests that took over SLOW_REQUEST_THRESHOLD_MS to complete.
	 */
	private void logSlowRequests(long requestLifetime, Request<?> request,
			byte[] responseContents, HttpResponse httpResponse) {
		if (DEBUG || requestLifetime > SLOW_REQUEST_THRESHOLD_MS) {
			VolleyLog
					.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], "
							+ "[rc=%d], [retryCount=%s]", request,
							requestLifetime,
							responseContents != null ? responseContents.length
									: "null", httpResponse.getResponseCode(),
							request.getRetryPolicy().getCurrentRetryCount());
		}
	}

	/**
	 * Attempts to prepare the request for a retry. If there are no more
	 * attempts remaining in the request's retry policy, a timeout exception is
	 * thrown.
	 * 
	 * @param request
	 *            The request to use.
	 */
	private static void attemptRetryOnException(String logPrefix,
			Request<?> request, VolleyError exception) throws VolleyError {
		RetryPolicy retryPolicy = request.getRetryPolicy();
		int oldTimeout = request.getTimeoutMs();

		try {
			retryPolicy.retry(exception);
		} catch (VolleyError e) {
			request.addMarker(String.format("%s-timeout-giveup [timeout=%s]",
					logPrefix, oldTimeout));
			throw e;
		}
		request.addMarker(String.format("%s-retry [timeout=%s]", logPrefix,
				oldTimeout));
	}

	private void addCacheHeaders(Map<String, String> headers, Entry entry) {
		// If there's no cache entry, we're done.
		if (entry == null) {
			return;
		}

		if (entry.etag != null) {
			headers.put("If-None-Match", entry.etag);
		}

		if (entry.lastModified > 0) {
			Date refTime = new Date(entry.lastModified);
			headers.put("If-Modified-Since", InternalUtils.formatDate(refTime));
		}
	}

	protected void logError(String what, String url, long start) {
		long now = SystemClock.elapsedRealtime();
		VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", what, (now - start),
				url);
	}

	/** Reads the contents of HttpEntity into a byte[]. */
	private byte[] entityToBytes(HttpEntity entity) throws IOException,
			ServerError {
		PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(
				mPool, (int) entity.getContentLength());
		byte[] buffer = null;
		try {
			InputStream in = entity.getContent();
			if (in == null) {
				throw new ServerError();
			}
			buffer = mPool.getBuf(1024);
			int count;
			while ((count = in.read(buffer)) != -1) {
				bytes.write(buffer, 0, count);
			}
			return bytes.toByteArray();
		} finally {
			try {
				// Close the InputStream and release the resources by
				// "consuming the content".
				entity.consumeContent();
			} catch (Exception e) {
				// This can happen if there was an exception above that left the
				// entity in
				// an invalid state.
				VolleyLog.v("Error occured when calling consumingContent");
			}
			mPool.returnBuf(buffer);
			bytes.close();
		}
	}

}
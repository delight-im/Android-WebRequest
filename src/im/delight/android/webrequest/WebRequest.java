package im.delight.android.webrequest;

/**
 * Copyright 2014 www.delight.im <info@delight.im>
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import android.util.Base64;

/** Fluent interface for easy HTTP(S) GET/POST/PUT/DELETE requests to web servers */
public class WebRequest {
	
	protected static final int METHOD_GET = 1;
	protected static final int METHOD_POST = 2;
	protected static final int METHOD_PUT = 3;
	protected static final int METHOD_DELETE = 4;
	protected static final String HTTP_HEADER_USER_AGENT = "User-Agent";
	protected static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
	protected static final String DEFAULT_USER_AGENT = "Android";
	protected static final int DEFAULT_CONNECTION_TIMEOUT = 6000;
	protected static final int DEFAULT_SOCKET_TIMEOUT = 8000;
	protected static final String DEFAULT_CHARSET = "utf-8";
	protected int mRequestMethod;
	protected String mUrl;
	protected String mUserAgent;
	protected int mConnectionTimeout;
	protected int mSocketTimeout;
	protected String mCharset;
	protected List<NameValuePair> mParams;
	protected String mUsername;
	protected String mPassword;
	protected DefaultHttpClient mClient;
	protected HttpRequestBase mHttpRequest;
	
	public interface Callback {
		public void onSuccess(String responseText);
		public void onError();
	}

	/** Creates a new WebRequest instance which you can then call get(), post(), put() or delete() on */
	public WebRequest() {
		super();
		mUserAgent = DEFAULT_USER_AGENT;
		mConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
		mSocketTimeout = DEFAULT_SOCKET_TIMEOUT;
		mCharset = DEFAULT_CHARSET;
		mParams = new ArrayList<NameValuePair>();
		mUsername = null;
		mPassword = null;
	}
	
	/**
	 * Make this request a GET request so that you can call to(...)
	 * @return this instance for chaining
	 */
	public WebRequest get() {
		mRequestMethod = METHOD_GET;
		return this;
	}
	
	/**
	 * Make this request a POST request so that you can call to(...)
	 * @return this instance for chaining
	 */
	public WebRequest post() {
		mRequestMethod = METHOD_POST;
		return this;
	}
	
	/**
	 * Make this request a PUT request so that you can call to(...)
	 * @return this instance for chaining
	 */
	public WebRequest put() {
		mRequestMethod = METHOD_PUT;
		return this;
	}
	
	/**
	 * Make this request a DELETE request so that you can call to(...)
	 * @return this instance for chaining
	 */
	public WebRequest delete() {
		mRequestMethod = METHOD_DELETE;
		return this;
	}
	
	/**
	 * Sets the URL for this web request
	 * @param url the URL to send the request to
	 * @return this instance for chaining
	 */
	public WebRequest to(String url) {
		if (url == null || url.length() < 1) {
			throw new RuntimeException("You must provide a valid target URL");
		}
		else {
			mUrl = url;
			return this;
		}
	}
	
	/**
	 * Sets the username and password for HTTP Basic Auth
	 * @param username the username to authenticate as
	 * @param password the password to authenticate with
	 * @return this instance for chaining
	 */
	public WebRequest auth(String username, String password) {
		mUsername = username;
		mPassword = password;
		return this;
	}
	
	/**
	 * Sets the User-Agent header for the current request
	 * @param userAgent User-Agent header to send
	 * @param this instance for chaining
	 */
	public WebRequest asUserAgent(String userAgent) {
		if (userAgent == null) {
			throw new RuntimeException("You must pass a valid User-Agent header");
		}
		else {
			mUserAgent = userAgent;
			return this;
		}
	}
	
	/**
	 * Sets the connection timeout and socket timeout for the current request in milliseconds
	 * @param connectionTimeout timeout in milliseconds
	 * @param socketTimeout timeout in milliseconds
	 * @return this instance for chaining
	 */
	public WebRequest withTimeout(int connectionTimeout, int socketTimeout) {
		mConnectionTimeout = connectionTimeout;
		mSocketTimeout = socketTimeout;
		return this;
	}
	
	/**
	 * Sets the charset for the current request
	 * @param charset the character encoding to use for the current request
	 * @return this instance for chaining
	 */
	public WebRequest withCharset(String charset) {
		if (charset == null) {
			throw new RuntimeException("You must pass a valid charset name");
		}
		else {
			mCharset = charset;
			return this;
		}
	}
	
	/**
	 * Adds a new key-value pair to the parameters of this request
	 * @param key the key of this pair
	 * @param value the corresponding value for this pair
	 * @return this instance for chaining
	 */
	public WebRequest addParam(String key, String value) {
		if (key == null) {
			throw new RuntimeException("You must pass a valid key when adding a param");
		}
		else {
			mParams.add(new BasicNameValuePair(key, value));
			return this;
		}
	}
	
	/**
	 * Adds a new key-value pair to the parameters of this request
	 * @param key the key of this pair
	 * @param value the corresponding value for this pair
	 * @return this instance for chaining
	 */
	public WebRequest addParam(String key, int value) {
		if (key == null) {
			throw new RuntimeException("You must pass a valid key when adding a param");
		}
		else {
			mParams.add(new BasicNameValuePair(key, String.valueOf(value)));
			return this;
		}
	}
	
	/**
	 * Adds a new key-value pair to the parameters of this request
	 * @param key the key of this pair
	 * @param value the corresponding value for this pair
	 * @return this instance for chaining
	 */
	public WebRequest addParam(String key, long value) {
		if (key == null) {
			throw new RuntimeException("You must pass a valid key when adding a param");
		}
		else {
			mParams.add(new BasicNameValuePair(key, String.valueOf(value)));
			return this;
		}
	}
	
	/**
	 * Adds a new key-value pair to the parameters of this request
	 * @param key the key of this pair
	 * @param value the corresponding value for this pair
	 * @return this instance for chaining
	 */
	public WebRequest addParam(String key, float value) {
		if (key == null) {
			throw new RuntimeException("You must pass a valid key when adding a param");
		}
		else {
			mParams.add(new BasicNameValuePair(key, String.valueOf(value)));
			return this;
		}
	}
	
	/**
	 * Adds a new key-value pair to the parameters of this request
	 * @param key the key of this pair
	 * @param value the corresponding value for this pair
	 * @return this instance for chaining
	 */
	public WebRequest addParam(String key, double value) {
		if (key == null) {
			throw new RuntimeException("You must pass a valid key when adding a param");
		}
		else {
			mParams.add(new BasicNameValuePair(key, String.valueOf(value)));
			return this;
		}
	}
	
	/**
	 * Method to be overwritten in subclasses if you want to add custom HTTP headers
	 * @param request the request object to add the HTTP headers to
	 */
	protected void addCustomHTTPHeaders(HttpRequestBase request) { }

	protected void prepare() {
		if (mRequestMethod == 0) {
			throw new RuntimeException("You must call one of get(), post(), put() or delete() before executing the request");
		}
		else if (mUrl == null || mUrl.length() < 1) {
			throw new RuntimeException("You must provide a valid target URL before executing the request");
		}
		
		final BasicHttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, mConnectionTimeout);
		HttpConnectionParams.setSoTimeout(httpParameters, mSocketTimeout);

		mClient = new DefaultHttpClient(httpParameters);
		if (mRequestMethod == METHOD_GET) {
			if (mParams.size() > 0) {
				mUrl = mUrl+"?"+httpBuildQuery(mParams, mCharset);
			}
			mHttpRequest = new HttpGet(mUrl);
		}
		else if (mRequestMethod == METHOD_POST) {
			mHttpRequest = new HttpPost(mUrl);
			if (mParams.size() > 0) {
				try {
					((HttpPost) mHttpRequest).setEntity(new UrlEncodedFormEntity(mParams, mCharset));
				}
				catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
		}
		else {
			throw new RuntimeException("Unknown request method: "+mRequestMethod);
		}

		addCustomHTTPHeaders(mHttpRequest);
		mHttpRequest.setHeader(HTTP_HEADER_USER_AGENT, mUserAgent);
		if (mUsername != null && mPassword != null) {
			mHttpRequest.setHeader(HTTP_HEADER_AUTHORIZATION, getAuthDigest());
		}
	}
	
	/** Runs the current request asynchronously and executes the given callback afterwards */
	public void executeAsync(final Callback callback) {
		prepare();

		new Thread() {
			public void run() {
				String responseStr;
				try {
					final HttpResponse responseData = mClient.execute(mHttpRequest);
					responseStr = EntityUtils.toString(responseData.getEntity(), mCharset);
					if (responseStr == null) {
						if (callback != null) {
							callback.onError();
						}
					}
					else {
						if (callback != null) {
							callback.onSuccess(responseStr);
						}
					}
				}
				catch (Exception e) {
					if (callback != null) {
						callback.onError();
					}
					return;
				}
			}
		}.start();
	}
	
	/** Runs the current request asynchronously and executes the given callback afterwards */
	public String executeSync() {
		prepare();

		String responseStr;
		try {
			final HttpResponse responseData = mClient.execute(mHttpRequest);
			responseStr = EntityUtils.toString(responseData.getEntity(), mCharset);
			if (responseStr == null) {
				return null;
			}
			else {
				return responseStr;
			}
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/** Builds the HTTP query string from the list of parameters (analogous to PHP's http_build_query(...) function) */
	protected static String httpBuildQuery(List<? extends NameValuePair> parameters, String encoding) {
		return URLEncodedUtils.format(parameters, encoding).replace("*", "%2A");
	}
	
	protected String getAuthDigest() {
		if (mUsername != null && mPassword != null) {
			return "Basic "+Base64.encodeToString((mUsername+":"+mPassword).getBytes(), Base64.NO_WRAP);
		}
		else {
			return "";
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WebRequest [mUrl=");
		builder.append(mUrl);
		builder.append(", mRequestMethod=");
		builder.append(mRequestMethod);
		builder.append(", mUserAgent=");
		builder.append(mUserAgent);
		builder.append(", mConnectionTimeout=");
		builder.append(mConnectionTimeout);
		builder.append(", mSocketTimeout=");
		builder.append(mSocketTimeout);
		builder.append(", mCharset=");
		builder.append(mCharset);
		builder.append(", mParams=");
		builder.append(mParams);
		builder.append(", mUsername=");
		builder.append(mUsername);
		builder.append(", mPassword=");
		builder.append(mPassword);
		builder.append("]");
		return builder.toString();
	}

}
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

import org.apache.http.util.CharArrayBuffer;
import java.io.Reader;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import org.apache.http.Header;
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
	protected boolean mGzip;
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
		mGzip = false;
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
	 * Whether to ask for GZIP response compression or not
	 * @param gzip whether to ask for GZIP or not
	 * @return this instance for chaining
	 */
	public WebRequest askForGzip(boolean gzip) {
		mGzip = gzip;
		return this;
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
		// connections break all the time anyway so disable stale checking and get slightly improved performance
		HttpConnectionParams.setStaleCheckingEnabled(httpParameters, false);
		// use the consumer-supplied connection timeout (or the default)
		HttpConnectionParams.setConnectionTimeout(httpParameters, mConnectionTimeout);
		// use the consumer-supplied socket timeout (or the default)
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
		mHttpRequest.setHeader("User-Agent", mUserAgent);
		if (mUsername != null && mPassword != null) {
			mHttpRequest.setHeader("Authorization", getAuthDigest());
		}
		if (mGzip) {
			mHttpRequest.addHeader("Accept-Encoding", "gzip");
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
					responseStr = parseResponse(responseData);
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
			responseStr = parseResponse(responseData);
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
	
	protected String parseResponse(HttpResponse response) throws Exception {
		final Header contentEncoding = response.getFirstHeader("Content-Encoding");
		// if we have a compressed response (GZIP)
		if (contentEncoding != null && contentEncoding.getValue() != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
			// get the entity and the content length (if any) from the response
			final HttpEntity entity = response.getEntity();
			long contentLength = entity.getContentLength();
			
			// handle too large or undefined content lengths
			if (contentLength > Integer.MAX_VALUE) {
				throw new Exception("Response too large");
			}
			else if (contentLength < 0) {
				// use an arbitrary buffer size
				contentLength = 4096;
			}

			// construct a GZIP input stream from the response
			InputStream responseStream = entity.getContent();
			if (responseStream == null) {
				return null;
			}
			responseStream = new GZIPInputStream(responseStream);
			
			// read from the stream
			Reader reader = new InputStreamReader(responseStream, mCharset);
			CharArrayBuffer buffer = new CharArrayBuffer((int) contentLength);
			try {
				char[] tmp = new char[1024];
				int l;
				while ((l = reader.read(tmp)) != -1) {
					buffer.append(tmp, 0, l);
				}
			}
			finally {
				reader.close();
			}

			// return the decompressed response text as a string
			return buffer.toString();
		}
		// if we have an uncompressed response
		else {
			// return the response text as a string
			return EntityUtils.toString(response.getEntity(), mCharset);
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
		builder.append(", mGzip=");
		builder.append(mGzip);
		builder.append("]");
		return builder.toString();
	}

}
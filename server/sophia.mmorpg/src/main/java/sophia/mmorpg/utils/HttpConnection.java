package sophia.mmorpg.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public final class HttpConnection {

	private static final int HttpConnnectTimeout = 10 * 1000;
	private static final int HttpSoTimeout = 10 * 1000;

	private String _url;
	private String _method;
	private List<NameValuePair> _params = new ArrayList<NameValuePair>();
	private CallbackListener _callbackListener;
	private HttpClient _httpClient;

	private HttpConnection() {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, HttpConnnectTimeout);
		HttpConnectionParams.setSoTimeout(httpParams, HttpSoTimeout);
		_httpClient = new DefaultHttpClient(httpParams);
	}

	/** 创建一个GET方式的http连接 */
	public static HttpConnection create(String url, CallbackListener callbackListener) {
		HttpConnection httpConnection = new HttpConnection();
		httpConnection._method = "GET";
		httpConnection._url = url;
		// 补一个时间参数. 避免http的缓存问题..
		if (httpConnection._url.indexOf("?") > 0) {
			httpConnection._url += "&_t=" + System.currentTimeMillis();
		} else {
			httpConnection._url += "?_t=" + System.currentTimeMillis();
		}
		httpConnection._callbackListener = callbackListener;
		return httpConnection;
	}

	/** 创建一个POST方式的http连接 */
	public static HttpConnection create(String url, List<NameValuePair> params, CallbackListener callbackListener) {
		HttpConnection httpConnection = new HttpConnection();
		httpConnection._url = url;
		httpConnection._method = "POST";
		httpConnection._params.addAll(params);
		httpConnection._callbackListener = callbackListener;
		return httpConnection;
	}

	/**
	 * 开始执行Http请求
	 * 
	 * @param async
	 *            为true时将会在另一个线程中执行http请求和回调,为false时在当前线程中执行
	 */
	public void exec(boolean async) {
		if (async) {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					_connect();
				}
			});

			t.start();
		} else {
			_connect();
		}
	}

	private void _connect() {
		HttpResponse httpResponse = null;
		try {
			if ("GET".equals(_method)) {
				httpResponse = _httpClient.execute(new HttpGet(_url));
			} else {
				HttpPost httpPost = new HttpPost(_url);
				httpPost.setEntity(new UrlEncodedFormEntity(this._params, "UTF-8"));
				httpResponse = _httpClient.execute(httpPost);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_callbackListener.callBack(-1, "");
			return;
		}

		int responseCode = httpResponse.getStatusLine().getStatusCode();

		String result = "";
		try {
			result = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
			_callbackListener.callBack(responseCode, "");
			return;
		}

		_callbackListener.callBack(responseCode, result);
	}

	public interface CallbackListener {
		/**
		 * Http请求的返回时被回调
		 * 
		 * @param responseCode
		 *            成功时返回 HttpStatus.SC_OK, 其他状态参考{@link HttpStatus }
		 * @param result
		 */
		public void callBack(final int responseCode, final String result);
	}

}

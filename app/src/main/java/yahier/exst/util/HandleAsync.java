package yahier.exst.util;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;

/**
 * 封装异步任务类的操作
 * @author 子旺
 *
 */
public class HandleAsync {

	public void excute(Listener dealListener) {
		new MyAsync(dealListener).execute();
	}

	class MyAsync extends AsyncTask<Void, Void, String> {
		Listener listener;

		public MyAsync(Listener listener) {
			this.listener = listener;
		}

		@Override
		protected String doInBackground(Void... parse) {
			String result = null;
			try {
				result = listener.getResult();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			listener.parse(result);

		}

	}

	public interface Listener {
		/**
		 * 获取接口的数�?
		 * @return
		 * @throws IOException 
		 * @throws ClientProtocolException 
		 */
		String getResult() throws ClientProtocolException, IOException;

		/**
		 * 解析得到的数�?
		 * @param result
		 */
		void parse(String result);
	}
}

package higa.sharedcalendars.view.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by higashiyamamasahiro on 西暦17/11/16.
 */

public class AddPlanActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
	GoogleAccountCredential mCredential;
	private TextView mOutputText;
	private Button mCallApiButton;
	ProgressDialog mProgress;

	static final int REQUEST_ACCOUNT_PICKER = 1000;
	static final int REQUEST_AUTHORIZATION = 1001;
	static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
	static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

	private static final String BUTTON_TEXT = "Call Google Calendar API";
	private static final String PREF_ACCOUNT_NAME = "accountName";
	private static final String[] SCOPES = {CalendarScopes.CALENDAR};

	/**
	 * Activityを作成する。
	 *
	 * @param savedInstanceState 以前に保存されたインスタンスのデータ
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Activity のレイアウトを準備する
		LinearLayout activityLayout = new LinearLayout(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		activityLayout.setLayoutParams(lp);
		activityLayout.setOrientation(LinearLayout.VERTICAL);
		activityLayout.setPadding(16, 16, 16, 16);

		ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		// Google Calendar API を呼び出す　Button を準備する
		mCallApiButton = new Button(this);
		mCallApiButton.setText(BUTTON_TEXT);
		mCallApiButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallApiButton.setEnabled(false);
				mOutputText.setText("");
				getResultsFromApi();
				mCallApiButton.setEnabled(true);
			}
		});
		activityLayout.addView(mCallApiButton);

		// Google Calendar API の呼び出し結果を表示する　TextView を準備する
		mOutputText = new TextView(this);
		mOutputText.setLayoutParams(tlp);
		mOutputText.setPadding(16, 16, 16, 16);
		mOutputText.setVerticalScrollBarEnabled(true);
		mOutputText.setMovementMethod(new ScrollingMovementMethod());
		mOutputText.setText("Click the \'" + BUTTON_TEXT + "\' button to test the API.");
		activityLayout.addView(mOutputText);

		// Google Calendar API の呼び出し中を表す PrgressDialog を準備する
		mProgress = new ProgressDialog(this);
		mProgress.setMessage("Calling Google Calendar API ...");

		// ActivityにViewを設定する
		setContentView(activityLayout);

		// Google Calendar API の呼び出しのための認証情報を初期化する
		mCredential = GoogleAccountCredential.usingOAuth2(
				getApplicationContext(),
				Arrays.asList(SCOPES)
		).setBackOff(new ExponentialBackOff());
	}

	/**
	 * Google Calendar API の呼び出しの事前条件を確認し、条件を満たしていればAPIを呼び出す。
	 *
	 * 事前条件：
	 * - 有効な Google Play Services がインストールされていること
	 * - 有効な Google アカウントが選択されていること
	 * - 端末がインターネット接続可能であること
	 *
	 * 事前条件を満たしていない場合には、ユーザーに説明を表示する。
	 */
	private void getResultsFromApi() {
		if (!isGooglePlayServicesAvailable()) {
			// Google Play Services が無効な場合
			acquireGooglePlayServices();
		}
		else if (mCredential.getSelectedAccountName() == null) {
			// 有効な Google アカウントが選択されていない場合
			chooseAccount();
		}
		else if (!isDeviceOnline()) {
			// 端末がインターネットに接続されていない場合
			mOutputText.setText("No network connection available.");
		}
		else {
			new MakeRequestTask(mCredential).execute();
		}
	}

	/**
	 * 端末に Google Play Services がインストールされ、アップデートされているか否かを確認する。
	 *
	 * @return 利用可能な Google Play Services がインストールされ、アップデートされている場合にはtrueを、
	 * そうでない場合にはfalseを返す。
	 */
	private boolean isGooglePlayServicesAvailable() {
		GoogleApiAvailability apiAvailability =
				GoogleApiAvailability.getInstance();
		final int connectionStatusCode =
				apiAvailability.isGooglePlayServicesAvailable(this);
		return connectionStatusCode == ConnectionResult.SUCCESS;
	}

	/**
	 * ユーザーにダイアログを表示して、Google Play Services を利用可能な状態に設定するように促す。
	 * ただし、ユーザーが解決できないようなエラーの場合には、ダイアログを表示しない。
	 */
	private void acquireGooglePlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
		}
	}

	/**
	 * 有効な Google Play Services が見つからないことをエラーダイアログで表示する。
	 *
	 * @param connectionStatusCode Google Play Services が無効であることを示すコード
	 */
	void showGooglePlayServicesAvailabilityErrorDialog(
			final int connectionStatusCode
	) {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		Dialog dialog = apiAvailability.getErrorDialog(
				AddPlanActivity.this,
				connectionStatusCode,
				REQUEST_GOOGLE_PLAY_SERVICES
		);
		dialog.show();
	}

	/**
	 * Google　Calendar API の認証情報を使用するGoogleアカウントを設定する。
	 *
	 * 既にGoogleアカウント名が保存されていればそれを使用し、保存されていなければ、
	 * Googleアカウントの選択ダイアログを表示する。
	 *
	 * 認証情報を用いたGoogleアカウントの設定には、"GET_ACCOUNTS"パーミッションを
	 * 必要とするため、必要に応じてユーザーに"GET_ACCOUNTS"パーミッションを要求する
	 * ダイアログが表示する。
	 */
	@AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
	private void chooseAccount() {
		// "GET_ACCOUNTS"パーミッションを取得済みか確認する
		if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
			// SharedPreferencesから保存済みGoogleアカウントを取得する
			String accountName = getPreferences(Context.MODE_PRIVATE)
					.getString(PREF_ACCOUNT_NAME, null);
			if (accountName != null) {
				mCredential.setSelectedAccountName(accountName);
				getResultsFromApi();
			} else {
				// Googleアカウントの選択を表示する
				// GoogleAccountCredentialのアカウント選択画面を使用する
				startActivityForResult(
						mCredential.newChooseAccountIntent(),
						REQUEST_ACCOUNT_PICKER);
			}
		}
		else {
			// ダイアログを表示して、ユーザーに"GET_ACCOUNTS"パーミッションを要求する
			EasyPermissions.requestPermissions(
					this,
					"This app needs to access your Google account (via Contacts).",
					REQUEST_PERMISSION_GET_ACCOUNTS,
					Manifest.permission.GET_ACCOUNTS);
		}
	}

	/**
	 * アカウント選択や認証など、呼び出し先のActivityから戻ってきた際に呼び出される。
	 *
	 * @param requestCode Activityの呼び出し時に指定したコード
	 * @param resultCode  呼び出し先のActivityでの処理結果を表すコード
	 * @param data        呼び出し先のActivityでの処理結果のデータ
	 */
	@Override
	protected void onActivityResult(
			int requestCode,
			int resultCode,
			Intent data
	) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_GOOGLE_PLAY_SERVICES:
				if (resultCode != RESULT_OK) {
					mOutputText.setText(
							"This app requires Google Play Services. Please install " +
									"Google Play Services on your device and relaunch this app.");
				} else {
					getResultsFromApi();
				}
				break;

			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
					String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null) {
						SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString(PREF_ACCOUNT_NAME, accountName);
						editor.apply();
						mCredential.setSelectedAccountName(accountName);
						getResultsFromApi();
					}
				}
				break;

			case REQUEST_AUTHORIZATION:
				if (resultCode == RESULT_OK) {
					getResultsFromApi();
				}
				break;
		}
	}

	/**
	 * Android 6.0 (API 23) 以降にて、実行時にパーミッションを要求した際の結果を受け取る。
	 *
	 * @param requestCode  requestPermissions(android.app.Activity, String, int, String[])
	 *                     を呼び出した際に渡した　request code
	 * @param permissions  要求したパーミッションの一覧
	 * @param grantResults 要求したパーミッションに対する承諾結果の配列
	 *                     PERMISSION_GRANTED または PERMISSION_DENIED　が格納される。
	 */
	@Override
	public void onRequestPermissionsResult(
			int requestCode,
			@NonNull String[] permissions,
			@NonNull int[] grantResults
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	/**
	 * 要求したパーミッションがユーザーに承諾された際に、EasyPermissionsライブラリから呼び出される。
	 *
	 * @param requestCode 要求したパーミッションに関連した request code
	 * @param list        要求したパーミッションのリスト
	 */
	@Override
	public void onPermissionsGranted(int requestCode, List<String> list) {
		// 何もしない
	}

	/**
	 * 要求したパーミッションがユーザーに拒否された際に、EasyPermissionsライブラリから呼び出される。
	 *
	 * @param requestCode 要求したパーミッションに関連した request code
	 * @param list        要求したパーミッションのリスト
	 */
	@Override
	public void onPermissionsDenied(int requestCode, List<String> list) {
		// 何もしない
	}

	/**
	 * 現在、端末がネットワークに接続されているかを確認する。
	 *
	 * @return ネットワークに接続されている場合にはtrueを、そうでない場合にはfalseを返す。
	 */
	private boolean isDeviceOnline() {
		ConnectivityManager connMgr =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	/**
	 * 非同期で　Google Calendar API の呼び出しを行うクラス。
	 */
	private class MakeRequestTask extends AsyncTask<Void, Void, String> {

		private com.google.api.services.calendar.Calendar mService = null;
		private Exception mLastError = null;

		public MakeRequestTask(GoogleAccountCredential credential) {
			HttpTransport transport = AndroidHttp.newCompatibleTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			mService = new com.google.api.services.calendar.Calendar
					.Builder(transport, jsonFactory, credential)
					.setApplicationName("Google Calendar API Android Quickstart")
					.build();
		}

		/**
		 * Google Calendar API を呼び出すバックグラウンド処理。
		 *
		 * @param params 引数は不要
		 */
		@Override
		protected String doInBackground(Void... params) {
			try {
				return createCalendar();
			} catch (Exception e) {
				mLastError = e;
				cancel(true);
				return null;
			}
		}

		/**
		 * 選択されたGoogleアカウントに対して、新規にカレンダーを追加する。
		 *
		 * @return 作成したカレンダーのID
		 * @throws IOException
		 */
		private String createCalendar() throws IOException {
			// 新規にカレンダーを作成する
			com.google.api.services.calendar.model.Calendar calendar = new Calendar();
			// カレンダーにタイトルを設定する
			calendar.setSummary("CalendarTitle");
			// カレンダーにタイムゾーンを設定する
			calendar.setTimeZone("Asia/Tokyo");

			// 作成したカレンダーをGoogleカレンダーに追加する
			Calendar createdCalendar = mService.calendars().insert(calendar).execute();
			String calendarId = createdCalendar.getId();

			// カレンダー一覧から新規に作成したカレンダーのエントリを取得する
			CalendarListEntry calendarListEntry = mService.calendarList().get(calendarId).execute();

			// カレンダーのデフォルトの背景色を設定する
			calendarListEntry.setBackgroundColor("#ff0000");

			// カレンダーのデフォルトの背景色をGoogleカレンダーに反映させる
			CalendarListEntry updatedCalendarListEntry =
					mService.calendarList()
							.update(calendarListEntry.getId(), calendarListEntry)
							.setColorRgbFormat(true)
							.execute();

			// 新規に作成したカレンダーのIDを返却する
			return calendarId;
		}

		@Override
		protected void onPreExecute() {
			mOutputText.setText("");
			mProgress.show();
		}

		@Override
		protected void onPostExecute(String output) {
			mProgress.hide();
			if (output == null || output.isEmpty()) {
				mOutputText.setText("No results returned.");
			} else {
				mOutputText.setText("Calendar created using the Google Calendar API: " + output);
			}
		}

		@Override
		protected void onCancelled() {
			mProgress.hide();
			if (mLastError != null) {
				if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
					showGooglePlayServicesAvailabilityErrorDialog(
							((GooglePlayServicesAvailabilityIOException) mLastError)
									.getConnectionStatusCode());
				} else if (mLastError instanceof UserRecoverableAuthIOException) {
					startActivityForResult(
							((UserRecoverableAuthIOException) mLastError).getIntent(),
							REQUEST_AUTHORIZATION);
				} else {
					mOutputText.setText("The following error occurred:\n" + mLastError.getMessage());
				}
			} else {
				mOutputText.setText("Request cancelled.");
			}
		}
	}
}

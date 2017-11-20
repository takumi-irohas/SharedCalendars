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
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import higa.sharedcalendars.R;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by higashiyamamasahiro on 西暦17/11/19.
 */

public class CalendarActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
	private Toolbar toolbar;
	GoogleAccountCredential mCredential;
	private TextView mOutputText;
	private Button mCallApiButton;
	ProgressDialog mProgress;

	static final int REQUEST_ACCOUNT_PICKER = 1000;
	static final int REQUEST_AUTHORIZATION = 1001;
	static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
	static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

	private static final String BUTTON_TEXT = "今後の予定";
	private static final String PREF_ACCOUNT_NAME = "accountName";
	private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mCallApiButton = (Button)findViewById(R.id.api_call_button);
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

		mOutputText = (TextView)findViewById(R.id.api_call_text);
		mOutputText.setPadding(16, 16, 16, 16);
		mOutputText.setVerticalScrollBarEnabled(true);
		mOutputText.setMovementMethod(new ScrollingMovementMethod());
		mOutputText.setText(
				"ボタンをクリックすると今後10日間予定されている予定が出力されます。");

		mProgress = new ProgressDialog(this);
		mProgress.setMessage("Calling My Google Calendar ...");

		// Initialize credentials and service object.
		mCredential = GoogleAccountCredential.usingOAuth2(
				getApplicationContext(), Arrays.asList(SCOPES))
				.setBackOff(new ExponentialBackOff());

		WebView webView = (WebView)findViewById(R.id.web_view);
		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl("https://google.com/");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_activity_calendars, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(this, MemoListActivity.class));
		return true;
	}

	private void getResultsFromApi() {
		if (! isGooglePlayServicesAvailable()) {
			acquireGooglePlayServices();
		} else if (mCredential.getSelectedAccountName() == null) {
			chooseAccount();
		} else if (! isDeviceOnline()) {
			mOutputText.setText("No network connection available.");
		} else {
			new MakeRequestTask(mCredential).execute();
		}
	}

	@AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
	private void chooseAccount() {
		if (EasyPermissions.hasPermissions(
				this, Manifest.permission.GET_ACCOUNTS)) {
			String accountName = getPreferences(Context.MODE_PRIVATE)
					.getString(PREF_ACCOUNT_NAME, null);
			if (accountName != null) {
				mCredential.setSelectedAccountName(accountName);
				getResultsFromApi();
			} else {
				// Start a dialog from which the user can choose an account
				startActivityForResult(
						mCredential.newChooseAccountIntent(),
						REQUEST_ACCOUNT_PICKER);
			}
		} else {
			// Request the GET_ACCOUNTS permission via a user dialog
			EasyPermissions.requestPermissions(
					this,
					"This app needs to access your Google account (via Contacts).",
					REQUEST_PERMISSION_GET_ACCOUNTS,
					Manifest.permission.GET_ACCOUNTS);
		}
	}

	@Override
	protected void onActivityResult(
			int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
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
				if (resultCode == RESULT_OK && data != null &&
						data.getExtras() != null) {
					String accountName =
							data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null) {
						SharedPreferences settings =
								getPreferences(Context.MODE_PRIVATE);
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

	@Override
	public void onRequestPermissionsResult(int requestCode,
																				 @NonNull String[] permissions,
																				 @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(
				requestCode, permissions, grantResults, this);
	}

	@Override
	public void onPermissionsGranted(int requestCode, List<String> list) {
		// Do nothing.
	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> list) {
		// Do nothing.
	}

	private boolean isDeviceOnline() {
		ConnectivityManager connMgr =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	private boolean isGooglePlayServicesAvailable() {
		GoogleApiAvailability apiAvailability =
				GoogleApiAvailability.getInstance();
		final int connectionStatusCode =
				apiAvailability.isGooglePlayServicesAvailable(this);
		return connectionStatusCode == ConnectionResult.SUCCESS;
	}

	private void acquireGooglePlayServices() {
		GoogleApiAvailability apiAvailability =
				GoogleApiAvailability.getInstance();
		final int connectionStatusCode =
				apiAvailability.isGooglePlayServicesAvailable(this);
		if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
		}
	}

	void showGooglePlayServicesAvailabilityErrorDialog(
			final int connectionStatusCode) {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		Dialog dialog = apiAvailability.getErrorDialog(
				CalendarActivity.this,
				connectionStatusCode,
				REQUEST_GOOGLE_PLAY_SERVICES);
		dialog.show();
	}

	private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
		private com.google.api.services.calendar.Calendar mService = null;
		private Exception mLastError = null;

		MakeRequestTask(GoogleAccountCredential credential) {
			HttpTransport transport = AndroidHttp.newCompatibleTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			mService = new com.google.api.services.calendar.Calendar.Builder(
					transport, jsonFactory, credential)
					.setApplicationName("Google Calendar API Android Quickstart")
					.build();
		}

		@Override
		protected List<String> doInBackground(Void... params) {
			try {
				return getDataFromApi();
			} catch (Exception e) {
				mLastError = e;
				cancel(true);
				return null;
			}
		}

		private List<String> getDataFromApi() throws IOException {
			// List the next 10 events from the primary calendar.
			DateTime now = new DateTime(System.currentTimeMillis());
			List<String> eventStrings = new ArrayList<String>();
			Events events = mService.events().list("primary")
					.setMaxResults(10)
					.setTimeMin(now)
					.setOrderBy("startTime")
					.setSingleEvents(true)
					.execute();
			List<Event> items = events.getItems();

			for (Event event : items) {
				DateTime start = event.getStart().getDateTime();
				if (start == null) {
					// All-day events don't have start times, so just use
					// the start date.
					start = event.getStart().getDate();
				}
				eventStrings.add(
						String.format("%s (%s)", event.getSummary(), start));
			}
			return eventStrings;
		}

		@Override
		protected void onPreExecute() {
			mOutputText.setText("");
			mProgress.show();
		}

		@Override
		protected void onPostExecute(List<String> output) {
			mProgress.hide();
			if (output == null || output.size() == 0) {
				mOutputText.setText("No results returned.");
			} else {
				output.add(0, "今後10日間の予定:");
				mOutputText.setText(TextUtils.join("\n", output));
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
							CalendarActivity.REQUEST_AUTHORIZATION);
				} else {
					mOutputText.setText("The following error occurred:\n"
							+ mLastError.getMessage());
				}
			} else {
				mOutputText.setText("Request cancelled.");
			}
		}
	}
}

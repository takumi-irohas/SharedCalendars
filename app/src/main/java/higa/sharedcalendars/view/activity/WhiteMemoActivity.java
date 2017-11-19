package higa.sharedcalendars.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import higa.sharedcalendars.R;
import higa.sharedcalendars.dao.DatabaseHelper;
import higa.sharedcalendars.model.Memo;
import higa.sharedcalendars.model.OrmaDatabase;

/**
 * Created by higashiyamamasahiro on 西暦17/11/08.
 */

public class WhiteMemoActivity extends AppCompatActivity {
		private OrmaDatabase db;

		// view
		private Toolbar toolbar;
		private TextInputLayout memoTitleLayout;
		private EditText memoTitle;
		private TextInputLayout memoContentLayout;
		private EditText memoContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_white_memo);

		db = DatabaseHelper.db(this);

		initView();
		initToolbar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.white_memo_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.white_memo:
				if (!validation()) {
					saveMemo();
					finish();
					return true;
				} else {
					return false;
				}
		}
		return super.onOptionsItemSelected(item);
	}

	private void initView() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		memoTitleLayout = (TextInputLayout) findViewById(R.id.memo_title_layout);
		memoTitle = (EditText) findViewById(R.id.memo_title_text);
		memoContentLayout = (TextInputLayout) findViewById(R.id.memo_content_layout);
		memoContent = (EditText) findViewById(R.id.memo_content_text);
	}

	private void initToolbar() {
		setSupportActionBar(toolbar);
		ActionBar bar = getSupportActionBar();
		if (bar != null) {
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setDisplayShowHomeEnabled(true);
			bar.setDisplayShowTitleEnabled(true);
			bar.setHomeButtonEnabled(true);
		}
	}

	private boolean validation() {
		Boolean isError = false;

		if (memoTitle.getText().toString().isEmpty()) {
			memoTitleLayout.setError("タイトルを入力してください");
			isError = true;
		} else {
			memoTitleLayout.setErrorEnabled(false);
		}

		if (memoContent.getText().toString().isEmpty()) {
			memoContentLayout.setError("内容を入力してください");
			isError = true;
		} else {
			memoContentLayout.setErrorEnabled(false);
		}

		return isError;
	}

	private void saveMemo() {
		Memo memo = new Memo();
		memo.memoTitle = memoTitle.getText().toString();
		memo.memoContent = memoContent.getText().toString();

		db.relationOfMemo().inserter().execute(memo);
	}

	public static Intent createIntent(Context context) {
		return new Intent(context, MemoListActivity.class);
	}
}

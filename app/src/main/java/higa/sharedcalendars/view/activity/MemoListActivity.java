package higa.sharedcalendars.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import java.util.List;

import higa.sharedcalendars.R;
import higa.sharedcalendars.dao.DatabaseHelper;
import higa.sharedcalendars.model.Memo;
import higa.sharedcalendars.model.OrmaDatabase;
import higa.sharedcalendars.view.adapter.MemoRecyclerAdapter;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by higashiyamamasahiro on 西暦17/11/07.
 */

public class MemoListActivity extends AppCompatActivity {
	private Toolbar toolbar;
	private static String TAG = MemoListActivity.class.getSimpleName();
	private OrmaDatabase db;
	private RecyclerView memoList;
	private MemoRecyclerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo_list);
		toolbar = (Toolbar)findViewById(R.id.memo_list_toolbar);
		setSupportActionBar(toolbar);
		db = DatabaseHelper.db(this);
		setView();
		recycler();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_memo_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(this, WhiteMemoActivity.class));
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		db.selectFromMemo()
				.executeAsObservable()
				.toList()
				.subscribeOn(Schedulers.io())
				.subscribe(new SingleObserver<List<Memo>>() {
					@Override
					public void onSubscribe(Disposable d) {
					}
					@Override
					public void onSuccess(List<Memo> memos) {
						adapter.setMemos(memos);
					}
					@Override
					public void onError(Throwable e) {
						Log.e(TAG, "onResume: " + e.toString());
					}
				});
	}

	private void setView() {
		memoList = (RecyclerView) findViewById(R.id.job_list);
	}

	private void recycler() {
		adapter = new MemoRecyclerAdapter(this);
		DividerItemDecoration divider = new DividerItemDecoration(this, 1);
		divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));

		memoList.setAdapter(adapter);
		memoList.setHasFixedSize(true);
		memoList.addItemDecoration(divider);
		memoList.setLayoutManager(new LinearLayoutManager(this));
	}

	public static Intent createIntent(Context context) {
		return new Intent(context, MemoListActivity.class);
	}
}

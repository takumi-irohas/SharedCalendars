package higa.sharedcalendars.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toolbar;

import higa.sharedcalendars.R;

/**
 * Created by higashiyamamasahiro on 西暦17/11/07.
 */

public class MemoListActivity extends AppCompatActivity {
	private Toolbar toolbar;
	private ListView memoListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo_list);
		initToolbar();
	}

	public void initToolbar(){
		toolbar = (Toolbar)findViewById(R.id.memo_list_toolbar);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
 			public void onClick(View v) {
				Intent intent = new Intent(MemoListActivity.this, CalendarActivity.class);
				startActivity(intent);
			}
 		});
		
		memoListView = (ListView)findViewById(R.id.memo_list);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_activity_calendars, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(this, AddMemoActivity.class));
		return true;
	}

}

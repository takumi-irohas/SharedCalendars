package higa.sharedcalendars.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import higa.sharedcalendars.R;
import higa.sharedcalendars.dao.MemoDAO;
import higa.sharedcalendars.dao.MemoDTO;

/**
 * Created by higashiyamamasahiro on 西暦17/11/08.
 */

public class AddMemoActivity extends AppCompatActivity{
//	private String db = "memo.db";
	private EditText memoTitleEditText;
	private EditText memoContentEditText;
	private Button memoInsertButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_memo);
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_memo_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		startActivity(new Intent(this, MemoListActivity.class));
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initView(){
		memoTitleEditText = (EditText) findViewById(R.id.title);
		memoContentEditText = (EditText) findViewById(R.id.content);
		memoInsertButton = (Button) findViewById(R.id.insert);

		memoInsertButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MemoDTO memoDTO = new MemoDTO();
				memoDTO.setTitle(memoTitleEditText.getText().toString());
				memoDTO.setContent(memoContentEditText.getText().toString());
				insertMemo(memoDTO);
				updateBookList();
			}
		});
	}

	private void updateBookList() {
		// ここにクエリを実装します
		List<MemoDTO> bookDTOList = new ArrayList();
//		showBookList(bookDTOList);
	}

	private void insertMemo(MemoDTO memo) {
		long rowId = MemoDAO.insert(this , memo);
		Toast.makeText(this, "rowId:" + rowId , Toast.LENGTH_SHORT).show();
	}

	private void insertOrThrowMemo(MemoDTO memo) {
		long rowId = MemoDAO.insertOrThrow(this , memo);
		Toast.makeText(this, "rowId:" + rowId , Toast.LENGTH_SHORT).show();
	}

	private void insertWithOnConflictMemo(MemoDTO memo) {
		long rowId = MemoDAO.insertWithOnConflict(this , memo);
		Toast.makeText(this, "rowId:" + rowId , Toast.LENGTH_SHORT).show();
	}

	/*
	private void showBookList(List<MemoDTO> memoDTOList) {
		final List<Map<String, String>> list = new ArrayList<>();
		for (MemoDTO memoDTO : memoDTOList) {
			Map<String, String> map = new HashMap<>();
			map.put("title", memoDTO.getTitle());
			map.put("subtitle", memoDTO.getContent());
			list.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(
				this,
				list,
				android.R.layout.simple_list_item_2,
				new String[] {"title", "subtitle"},
				new int[] {android.R.id.text1, android.R.id.text2}
		);
		mListView.setAdapter(adapter);
	}*/
}

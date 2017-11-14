package higa.sharedcalendars.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static higa.sharedcalendars.dao.LoadToSQLiteMasterContract.Memo.COLUMN_NAME_CONTENT;
import static higa.sharedcalendars.dao.LoadToSQLiteMasterContract.Memo.COLUMN_NAME_TITLE;

/**
 * Created by higashiyamamasahiro on 西暦17/11/08.
 */

public class MemoDAO {
	/**
	 * SQLiteDatabaseインスタンスを取得する
	 * @param context コンテキスト
	 * @param isWritable 書き込み可能
	 * @return SQLiteDatabaseインスタンス
	 */
	public static SQLiteDatabase getSqLiteDatabase(Context context , boolean isWritable) {
		LoadToSQLiteMasterSQLiteOpenHelper loadToSQLiteMasterSQLiteOpenHelper = new LoadToSQLiteMasterSQLiteOpenHelper(context);
		SQLiteDatabase sqLiteDatabase = null;
		if(isWritable) {
			sqLiteDatabase = loadToSQLiteMasterSQLiteOpenHelper.getWritableDatabase();
		} else {
			sqLiteDatabase = loadToSQLiteMasterSQLiteOpenHelper.getReadableDatabase();
		}
		return sqLiteDatabase;
	}

	public static ContentValues convertContentValues(MemoDTO memoDTO) {
		ContentValues contentValues = new ContentValues();
		if(memoDTO.getId() != 0) {
			contentValues.put(LoadToSQLiteMasterContract.Memo._ID, memoDTO.getId());
		}
		if(memoDTO.getTitle() != null) {
			contentValues.put(COLUMN_NAME_TITLE, memoDTO.getTitle());
		}
		if(memoDTO.getContent() != null) {
			contentValues.put(COLUMN_NAME_CONTENT, memoDTO.getContent());
		}
		return contentValues;
	}

	public static long insert(Context context , MemoDTO memoDTO){
		SQLiteDatabase sqLiteDatabase = getSqLiteDatabase(context,true);
		long rowId = sqLiteDatabase.insert(
				LoadToSQLiteMasterContract.Memo.TABLE_NAME,
				COLUMN_NAME_CONTENT,
				convertContentValues(memoDTO)
		);
		return rowId;
	}

	public static long insertOrThrow(Context context , MemoDTO memoDTO){
		SQLiteDatabase sqLiteDatabase = getSqLiteDatabase(context,true);
		long rowId = sqLiteDatabase.insertOrThrow(
				LoadToSQLiteMasterContract.Memo.TABLE_NAME ,
				COLUMN_NAME_CONTENT,
				convertContentValues(memoDTO)
		);
		return rowId;
	}

	public static long insertWithOnConflict(Context context , MemoDTO memoDTO){
		SQLiteDatabase sqLiteDatabase = getSqLiteDatabase(context,true);
		long rowId = sqLiteDatabase.insertWithOnConflict(
				LoadToSQLiteMasterContract.Memo.TABLE_NAME ,
				COLUMN_NAME_CONTENT,
				convertContentValues(memoDTO),
				SQLiteDatabase.CONFLICT_REPLACE
		);
		return rowId;
	}

	private static List<MemoDTO> convertCursorToDTO(Cursor cursor) {
		List<MemoDTO> ret = new ArrayList<>();
		while(cursor.moveToNext()){
			MemoDTO memoDTO = new MemoDTO();
			int columnIndex = cursor.getColumnIndex(LoadToSQLiteMasterContract.Memo._ID);
			if(columnIndex != -1) {
				memoDTO.setId(cursor.getInt(columnIndex));
			}
			columnIndex = cursor.getColumnIndex(COLUMN_NAME_TITLE);
			if(columnIndex != -1) {
				memoDTO.setTitle(cursor.getString(columnIndex));
			}
			columnIndex = cursor.getColumnIndex(COLUMN_NAME_CONTENT);
			if(columnIndex != -1) {
				memoDTO.setContent(cursor.getString(columnIndex));
			}
			ret.add(memoDTO);
		}
		return ret;
	}
}

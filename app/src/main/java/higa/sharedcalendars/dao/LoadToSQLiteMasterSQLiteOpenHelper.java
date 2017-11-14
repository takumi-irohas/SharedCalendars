package higa.sharedcalendars.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by higashiyamamasahiro on 西暦17/11/08.
 */

public class LoadToSQLiteMasterSQLiteOpenHelper extends SQLiteOpenHelper {

	/**
	 * SQLiteOpenHelperはデータベースを作成したり、開いたりするためのヘルパークラスです。
	 * @param context
	 */
	public LoadToSQLiteMasterSQLiteOpenHelper(Context context) {
		/**
		 * Context context : コンテキスト
		 * String name : データベースファイル名を指定します、nullを渡した場合はメモリ上にデータベースが作成されます。
		 * SQLiteDatabase.CursorFactory factory : Cursorオブジェクトを作成するファクトリークラスを指定します、nullを指定するとデフォルトのものが使用されます。
		 * int version : データベースのバージョンを指定します、作成済みのデータベースがある場合は「作成済みバージョン」と「引数バージョン」が比較されます。
		 *               作成済みバージョン = 引数バージョン : インスタンスが作成されます。
		 *               作成済みバージョン < 引数バージョン : SQLiteOpenHelper#onUpgradeが呼ばれます。
		 *               作成済みバージョン > 引数バージョン : SQLiteOpenHelper#onDowngradeが呼ばれます。
		 * DatabaseErrorHandler errorHandler : データベースになんらかの破損を検知した時に呼ばれるコールバックインターフェースを設定する。
		 */
		super(context, LoadToSQLiteMasterContract.DataBase.DATABASE_NAME, null, LoadToSQLiteMasterContract.DataBase.DATABASE_VERSION,null);
		Log.d(TAG, "LoadToSQLiteMasterSQLiteOpenHelper: ");
	}

	@Override
	public void onConfigure(SQLiteDatabase db) {
		Log.d(TAG, "onConfigure: ");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate: ");
		// Memoテーブルを作成します。
		db.execSQL(LoadToSQLiteMasterContract.Memo.SQL_CREATE_ENTRIES);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		Log.d(TAG, "onOpen: ");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade: ");
		// Memoテーブルを削除します。
		db.execSQL(LoadToSQLiteMasterContract.Memo.SQL_DROP_ENTRIES);
		onCreate(db);
	}
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onDowngrade: ");
	}
}

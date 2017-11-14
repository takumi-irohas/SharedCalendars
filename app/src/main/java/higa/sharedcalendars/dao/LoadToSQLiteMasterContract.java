package higa.sharedcalendars.dao;

import android.provider.BaseColumns;

/**
 * Created by higashiyamamasahiro on 西暦17/11/08.
 */

public class LoadToSQLiteMasterContract {
	/** TEXT型 */
	public static final String TYPE_TEXT = " TEXT ";
	/** INTEGER型 */
	public static final String TYPE_INTEGER = " INTEGER ";
	/** プライマリーキー */
	public static final String PRIMARY_KEY = " PRIMARY KEY ";
	/** カンマ */
	public static final String COMMA_SEP = " , ";

	/**
	 * このクラスはインスタンス化することを目的としていないため、
	 * privateなコンストラクタを定義する。
	 */
	private LoadToSQLiteMasterContract(){}

	/**
	 * データベースを定義するクラス
	 */
	public static class DataBase {
		/** 現在のバージョン */
		public static final int DATABASE_VERSION = 1;
		/** データベース名 */
		public static final String DATABASE_NAME = "MemoDataBase.db";
	}

	/**
	 * Bookテーブルを定義するクラス
	 */
	public static class Memo implements BaseColumns {

		/** テーブル名 */
		public static final String TABLE_NAME = "memo";

		/** カラム名(タイトル) */
		public static final String COLUMN_NAME_TITLE = "title";

		/** カラム名(内容) */
		public static final String COLUMN_NAME_CONTENT = "content";

		/** クリエイト文 */
		public static final String SQL_CREATE_ENTRIES =
				"CREATE TABLE " + TABLE_NAME + " (" +
						_ID + TYPE_INTEGER + PRIMARY_KEY  + COMMA_SEP +
						COLUMN_NAME_TITLE + TYPE_TEXT + COMMA_SEP +
						COLUMN_NAME_CONTENT + TYPE_TEXT +
						" )";

		/** ドロップ文 */
		public static final String SQL_DROP_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

		/**
		 * このクラスはインスタンス化することを目的としていないため、
		 * privateなコンストラクタを定義する。
		 */
		private Memo(){}
	}
}

package higa.sharedcalendars.dao;

/**
 * Created by higashiyamamasahiro on 西暦17/11/08.
 */

public class MemoDTO {
	private int mId;
	private String mTitle;
	private String mContent;

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String content) {
		this.mContent = content;
	}
}

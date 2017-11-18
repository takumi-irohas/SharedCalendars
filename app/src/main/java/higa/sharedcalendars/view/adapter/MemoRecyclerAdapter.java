package higa.sharedcalendars.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import higa.sharedcalendars.R;
import higa.sharedcalendars.model.Memo;

/**
 * Created by higashiyamamasahiro on 西暦17/11/18.
 */

public class MemoRecyclerAdapter extends RecyclerView.Adapter<MemoRecyclerAdapter.ViewHolder> {
	private Context context;
	private List<Memo> memos;

	public MemoRecyclerAdapter(Context context) {
		this.context = context;
	}

	@Override
	public MemoRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_memo_list_item, parent, false);
		return new ViewHolder(inflate);
	}

	@Override
	public void onBindViewHolder(MemoRecyclerAdapter.ViewHolder holder, int position) {
		holder.memoTitle.setText(memos.get(position).memoTitle);
		holder.memoContent.setText(memos.get(position).memoContent);
	}

	@Override
	public int getItemCount() {
		if (memos != null) {
			return memos.size();
		} else {
			return 0;
		}
	}

	public void setMemos(List<Memo> memos) {
		this.memos = memos;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		// each data item is just a string in this case
		public TextView memoTitle;
		public TextView memoContent;

		public ViewHolder(View v) {
			super(v);
			memoTitle = (TextView) v.findViewById(R.id.memo_title);
			memoContent = (TextView) v.findViewById(R.id.memo_content);
		}
	}
}

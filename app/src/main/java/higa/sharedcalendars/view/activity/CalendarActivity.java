package higa.sharedcalendars.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import higa.sharedcalendars.R;

public class CalendarActivity extends AppCompatActivity {
	private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
	private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("yyyy年M月dd日 ahh:m:ss", Locale.getDefault());
	private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy - M月", Locale.getDefault());
	private TextView monthTextView;
	private CompactCalendarView compactCalendarView;
	//ダイアログを表示するボタン
	private Button mDispDialog;
	//ダイアログで選択で反映するテキストを表示する。
	private TextView mSelectedText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_activity_calendars, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.set_plans:
				startActivity(new Intent(this, AddPlanActivity.class));
				return true;
			case R.id.set_memo:
				startActivity(new Intent(this, MemoListActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initView(){
		monthTextView = (TextView)findViewById(R.id.text_month);
		compactCalendarView = (CompactCalendarView)findViewById(R.id.compact_calendar_view);
	}

	private void initCalendar(){
		compactCalendarView.setUseThreeLetterAbbreviation(false);
		compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
		loadEvents();
		loadEventsForYear(Calendar.YEAR);
		compactCalendarView.invalidate();
		//set title on calendar scroll
		compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
			@Override
			public void onDayClick(Date dateClicked) {

			}
			@Override
			public void onMonthScroll(Date firstDayOfNewMonth) {
				monthTextView.setText(dateFormatForMonth.format(firstDayOfNewMonth));
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		monthTextView.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
		// Set to current day on resume to set calendar to latest day
		monthTextView.setText(dateFormatForMonth.format(new Date()));
	}

	private void loadEvents() {
		addEvents(-1, -1);
		addEvents(Calendar.DECEMBER, -1);
		addEvents(Calendar.AUGUST, -1);
	}

	private void loadEventsForYear(int year) {
		addEvents(Calendar.DECEMBER, year);
		addEvents(Calendar.AUGUST, year);
	}

	private void logEventsByMonth(CompactCalendarView compactCalendarView) {
		currentCalender.setTime(new Date());
		currentCalender.set(Calendar.DAY_OF_MONTH, 1);
		currentCalender.set(Calendar.MONTH, Calendar.AUGUST);
		List<String> dates = new ArrayList<>();
		for (Event e : compactCalendarView.getEventsForMonth(new Date())) {
			dates.add(dateFormatForDisplaying.format(e.getTimeInMillis()));
		}
//		Log.d(TAG, "Events for Aug with simple date formatter: " + dates);
//		Log.d(TAG, "Events for Aug month using default local and timezone: " + compactCalendarView.getEventsForMonth(currentCalender.getTime()));
	}

	private void addEvents(int month, int year) {
		currentCalender.setTime(new Date());
		currentCalender.set(Calendar.DAY_OF_MONTH, 1);
		Date firstDayOfMonth = currentCalender.getTime();
		for (int i = 0; i < 6; i++) {
			currentCalender.setTime(firstDayOfMonth);
			if (month > -1) {
				currentCalender.set(Calendar.MONTH, month);
			}
			if (year > -1) {
				currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
				currentCalender.set(Calendar.YEAR, year);
			}
			currentCalender.add(Calendar.DATE, i);
			setToMidnight(currentCalender);
			long timeInMillis = currentCalender.getTimeInMillis();
			List<Event> events = getEvents(timeInMillis, i);
			compactCalendarView.addEvents(events);
		}
	}

	// TODO: 2017/10/11 このメソッドを拡張して勤務先と勤務時間を設定する
	private List<Event> getEvents(long timeInMillis, int day) {
		if (day < 2) {
			return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
		} else if ( day > 2 && day <= 4) {
			return Arrays.asList(
					new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)),
					new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)));
		} else {
			return Arrays.asList(
					new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis) ),
					new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)),
					new Event(Color.argb(255, 70, 68, 65), timeInMillis, "Event 3 at " + new Date(timeInMillis)));
		}
	}

	private void setToMidnight(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}


	//ダイアログの表示を行う
//	private View mDaialogSet(LayoutInflater inflater, ViewGroup container){
//		View v = inflater.inflate(R.layout.dailog_calendar, container, false);
//		mSelectedText = (TextView) getView().findViewById(R.id.dailog_text);
//		//ナビゲーションボタンのIDを書き込む
//		mDispDialog = (Button) getView().findViewById(R.id.shift_card);
//		mDispDialog.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				FragmentManager manager = getActivity().getSupportFragmentManager();
//				CalendarDialog dialog = CalendarDialog.newInstance();
//				dialog.setTargetFragment(CalendarFragment.this, 0); // ★★★
//				//dialog.show(manager);
//			}
//		});
//		return v;
//	}
}

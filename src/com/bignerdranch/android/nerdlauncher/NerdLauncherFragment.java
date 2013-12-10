package com.bignerdranch.android.nerdlauncher;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NerdLauncherFragment extends ListFragment {
	private static final String TAG = "NerdLauncherFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent startupIntent = new Intent(Intent.ACTION_MAIN);
		startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		//Final modifier recommended by Eclipse, not in book
		final PackageManager pm = getActivity().getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
		
		Log.i(TAG, "I've found " + activities.size() + " activities.");
		
		//Add the following code to sort ResolveInfo objects returned from PM alphabetically.
		Collections.sort(activities, new Comparator<ResolveInfo>() {
			public int compare(ResolveInfo a, ResolveInfo b) {
				PackageManager pm = getActivity().getPackageManager();
				return String.CASE_INSENSITIVE_ORDER.compare(
						a.loadLabel(pm).toString(),
						b.loadLabel(pm).toString());
			}
		});
		
		//Create ArrayAdapter that will create simple list item views that display the label of an activity
		ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo> ( 
				getActivity(), android.R.layout.simple_list_item_1, activities) {
			public View getView(int pos, View convertView, ViewGroup parent) {
				View v = super.getView(pos, convertView, parent);
				//Documentation says that simple list item 1 is a textview, 
				//so cast it so that you can set its text value
				TextView tv = (TextView)v;
				ResolveInfo ri = getItem(pos);
				tv.setText(ri.loadLabel(pm));
				return v;
			}
		};
		
		setListAdapter(adapter);
				}
	//get ActivityInfo for the list item, then use its data to create an explicit intent that will start the activity.
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ResolveInfo resolveInfo = (ResolveInfo)l.getAdapter().getItem(position);
		ActivityInfo activityInfo = resolveInfo.activityInfo;
		
		if (activityInfo == null) return;
		
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
		//We want nerdlauncher activity to start activities in new tasks, so add a flag to the intent
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		startActivity(i);
		//Notice that in this intent you are sending an action as part of an explicit intent.
	}

}

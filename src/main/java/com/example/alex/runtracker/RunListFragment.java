package com.example.alex.runtracker;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 轨迹列表Fragment
 * Created by Alex on 2015/7/21.
 */
public class RunListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
    //private RunDataBaseHelper.RunCursor mRunCursor;
    private static final int REQUEST_NEW_RUN = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //得到轨迹游标
        //mRunCursor = RunManager.get(getActivity()).queryRuns();
        //根据游标创建Adapter
        //RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), mRunCursor);
        //setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

//    @Override
//    public void onDestroy() {
//        mRunCursor.close();
//        super.onDestroy();
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_run:
                Intent i = new Intent(getActivity(), RunActivity.class);
                startActivityForResult(i, REQUEST_NEW_RUN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //处理结果：更新游标，并通知数据变化
        if(REQUEST_NEW_RUN == requestCode){
            //mRunCursor.requery();
            //((RunCursorAdapter)getListAdapter()).notifyDataSetChanged();
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //RunDataBaseHelper中指定了run表中的ID字段,CursorAdapter检测到该字段
        //并将其作为id参数传递给了onListItemClick(...)方法
        //这里其作为附加信息传递给了RunActivity
        Intent intent = new Intent(getActivity(), RunActivity.class);
        intent.putExtra(RunActivity.EXTRA_RUN_ID, id);
        startActivity(intent);
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new RunListCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        RunCursorAdapter adapter = new RunCursorAdapter(getActivity(),
                (RunDataBaseHelper.RunCursor)data);
        setListAdapter(adapter);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        setListAdapter(null);
    }



    //将游标变为ListView的Adapter
    private static class RunCursorAdapter extends CursorAdapter {
        private RunDataBaseHelper.RunCursor mRunCursor;

        public RunCursorAdapter(Context context, RunDataBaseHelper.RunCursor cursor) {
            super(context, cursor, 0);
            mRunCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Run run = mRunCursor.getRun();
            TextView startDateTextView = (TextView)view;
            String cellText = context.getString(R.string.cell_text, run.getStartDate());
            startDateTextView.setText(cellText);
        }
    }

    private static class RunListCursorLoader extends SQLiteCursorLoader {

        public RunListCursorLoader(Context context) {
            super(context);
        }

        @Override
        protected Cursor loadCursor() {
            return RunManager.get(getContext()).queryRuns();
        }
    }
}

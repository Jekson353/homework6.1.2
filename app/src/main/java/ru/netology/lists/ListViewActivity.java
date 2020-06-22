package ru.netology.lists;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListViewActivity extends AppCompatActivity {
    final String NAME_TITTLE = "tittle";
    final String NAME_SUBTITTLE = "subtittle";
    final String KEY_DEL_ITEMS = "delItemSharePrefs";
    ArrayList<Integer> deleteItems = new ArrayList<>();
    static BaseAdapter listContentAdapter;

    List<Map<String, String>> simpleAdapterContent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView list = findViewById(R.id.list);
        List<Map<String, String>> values = prepareContent();

        listContentAdapter = createAdapter(values);
        list.setAdapter(listContentAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                simpleAdapterContent.remove(i);
                deleteItems.add(i);
                listContentAdapter.notifyDataSetChanged();
            }
        });

        final SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                simpleAdapterContent.clear();
                prepareContent();
                listContentAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String, String>> values) {
        String[] from = {NAME_TITTLE, NAME_SUBTITTLE};
        int[] to = {R.id.id_tittle, R.id.id_subtitle};
        return new SimpleAdapter(this, values, R.layout.activity_list_item, from, to);
    }


    @NonNull
    private List<Map<String, String>> prepareContent() {

        SharedPreferences pref = getSharedPreferences("value", MODE_PRIVATE);
        String saveTxt = pref.getString("value", "");

        String[] arrayContent;
        assert saveTxt != null;

        if (!saveTxt.isEmpty()) {
            arrayContent = saveTxt.split("\n\n");
            Toast.makeText(ListViewActivity.this, R.string.get_string_from_share
                    , Toast.LENGTH_LONG)
                    .show();
        } else {
            arrayContent = getString(R.string.large_text).split("\n\n");
            pref.edit().putString("value", getString(R.string.large_text)).apply();
            Toast.makeText(ListViewActivity.this, R.string.get_string_from_string
                    , Toast.LENGTH_LONG)
                    .show();
        }

        Map<String, String> m;

        for (String s : arrayContent) {
            m = new HashMap<>();
            m.put(NAME_TITTLE, s);
            m.put(NAME_SUBTITTLE, s.length() + "");
            simpleAdapterContent.add(m);
        }
        return simpleAdapterContent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putIntegerArrayList(KEY_DEL_ITEMS, deleteItems);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        ArrayList<Integer> das = savedInstanceState.getIntegerArrayList(KEY_DEL_ITEMS);

        if (das != null) {
            for (Integer s : das) {
                simpleAdapterContent.remove(s.intValue());
            }
            listContentAdapter.notifyDataSetChanged();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

}

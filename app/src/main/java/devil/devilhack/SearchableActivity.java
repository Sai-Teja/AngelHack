package devil.devilhack;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

///**
// * Created by Teja on 29-May-15.
// */
public class SearchableActivity extends ActionBarActivity {

    String username;
    ListView listView;
    boolean calledOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        getSupportActionBar().setTitle("Availability");
        getSupportActionBar().setHomeButtonEnabled(true);

        listView = (ListView) findViewById(R.id.list);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            username = intent.getStringExtra(SearchManager.QUERY);
        }
    }
}

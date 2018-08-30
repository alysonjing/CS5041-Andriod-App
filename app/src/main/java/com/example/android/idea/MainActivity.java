//I referenced multiple online tutorials and Udacity.com
//https://aj83.host.cs.st-andrews.ac.uk

package com.example.android.idea;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {


    private ListView mListIdeas;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListIdeas = (ListView) findViewById(R.id.main_listview);


    }

    public void shareText(View view){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBodyText = "Your shearing message goes here";
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject/Title");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(intent, "Choose sharing method"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_create:
                startActivity(new Intent(this, IdeaActivity.class));
                break;

            case R.id.action_createImg:
                startActivity(new Intent(this, PaintActivity.class));
                break;

            case R.id.action_createMap:
                String addressString = "St Andrews, United Kingdom";
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("geo")
                        .path("0,0")
                        .query(addressString);
                Uri addressUri = builder.build();
                showMap(addressUri);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mListIdeas.setAdapter(null);
        ArrayList<Idea> ideas = Utilities.getAllSavedIdeas(getApplicationContext());

        Collections.sort(ideas, new Comparator<Idea>() {
            @Override
            public int compare(Idea lhs, Idea rhs) {
                if(lhs.getDateTime() > rhs.getDateTime()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        if(ideas != null && ideas.size() > 0) {
            final IdeaAdapter na = new IdeaAdapter(this, R.layout.item_idea, ideas);
            mListIdeas.setAdapter(na);

            mListIdeas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String fileName = ((Idea) mListIdeas.getItemAtPosition(position)).getDateTime()
                            + Utilities.FILE_EXTENSION;
                    Intent viewNoteIntent = new Intent(getApplicationContext(), IdeaActivity.class);
                    viewNoteIntent.putExtra(Utilities.EXTRAS_IDEA_FILENAME, fileName);
                    startActivity(viewNoteIntent);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "you have not saved anything!\ncreate some new ideas :)"
                    , Toast.LENGTH_SHORT).show();
        }
    }
    private void showMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}


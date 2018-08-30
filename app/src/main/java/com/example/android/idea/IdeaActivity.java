package com.example.android.idea;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class IdeaActivity extends AppCompatActivity {

    private boolean mIsViewingOrUpdating;
    private long mIdeaCreationTime;
    private String mFileName;
    private Idea mLoadedIdea = null;

    private EditText mEtTitle;
    private EditText mEtContent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea);

        mEtTitle = (EditText) findViewById(R.id.idea_edit_title);
        mEtContent = (EditText) findViewById(R.id.idea_edit_content);

        mFileName = getIntent().getStringExtra(Utilities.EXTRAS_IDEA_FILENAME);
        if(mFileName != null && !mFileName.isEmpty() && mFileName.endsWith(Utilities.FILE_EXTENSION)) {
            mLoadedIdea = Utilities.getIdeaByFileName(getApplicationContext(), mFileName);
            if (mLoadedIdea != null) {
                mEtTitle.setText(mLoadedIdea.getTitle());
                mEtContent.setText(mLoadedIdea.getContent());
                mIdeaCreationTime = mLoadedIdea.getDateTime();
                mIsViewingOrUpdating = true;
            }
        } else {
            mIdeaCreationTime = System.currentTimeMillis();
            mIsViewingOrUpdating = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mIsViewingOrUpdating) {
            getMenuInflater().inflate(R.menu.menu_view, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_idea_new, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save_idea:
            case R.id.action_update:
                validateAndSaveIdea();
                break;

            case R.id.action_share:
                shareIdea();
                break;

            case R.id.action_delete:
                deleteIdea();
                break;

            case R.id.action_cancel:
                cancelIdea();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        cancelIdea();
    }

    private void deleteIdea() {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(this)
                .setTitle("Delete idea")
                .setMessage("Do you really want to delete this idea?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mLoadedIdea != null && Utilities.deleteFile(getApplicationContext(), mFileName)) {
                            Toast.makeText(IdeaActivity.this, mLoadedIdea.getTitle() + " Deleted"
                                    , Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(IdeaActivity.this, "can not delete the note '" + mLoadedIdea.getTitle() + "'"
                                    , Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                })
                .setNegativeButton("NO", null);

        dialogDelete.show();
    }

    private void shareIdea(){
        Toast.makeText(this, "TODO: Share your ideas", Toast.LENGTH_LONG).show();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void cancelIdea() {

        if(!checkIdeaAltred()) {
            finish();
        } else {
            AlertDialog.Builder dialogCancel = new AlertDialog.Builder(this)
                    .setTitle("discard changes...")
                    .setMessage("are you sure you do not want to save changes?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("NO", null);
            dialogCancel.show();
        }
    }


    private boolean checkIdeaAltred() {
        if(mIsViewingOrUpdating) {
            return mLoadedIdea != null && (!mEtTitle.getText().toString().equalsIgnoreCase(mLoadedIdea.getTitle())
                    || !mEtContent.getText().toString().equalsIgnoreCase(mLoadedIdea.getContent()));
        } else {
            return !mEtTitle.getText().toString().isEmpty() || !mEtContent.getText().toString().isEmpty();
        }
    }


    private void validateAndSaveIdea() {

        String title = mEtTitle.getText().toString();
        String content = mEtContent.getText().toString();

        if(title.isEmpty()) {
            Toast.makeText(IdeaActivity.this, "please enter a title!"
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        if(content.isEmpty()) {
            Toast.makeText(IdeaActivity.this, "please enter your idea!"
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        if(mLoadedIdea != null) {
            mIdeaCreationTime = mLoadedIdea.getDateTime();
        } else {
            mIdeaCreationTime = System.currentTimeMillis();
        }

        if(Utilities.saveIdea(this, new Idea(mIdeaCreationTime, title, content))) {
            Toast.makeText(this, "Idea has been saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "can not save the note. make sure you have enough space " +
                    "on your device", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}

package com.example.android.idea;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PaintActivity extends AppCompatActivity {

    private CanvasView canvasView;
    private EditText mEtTitle;

    private boolean mIsViewingOrUpdating;
    private long mIdeaCreationTime;
    private String mFileName;
    private Idea mLoadedIdea = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);

        mEtTitle = (EditText) findViewById(R.id.title);
        canvasView = (CanvasView) findViewById(R.id.canvas);

        mFileName = getIntent().getStringExtra(Utilities.EXTRAS_IDEA_FILENAME);
        if(mFileName != null && !mFileName.isEmpty() && mFileName.endsWith(Utilities.FILE_EXTENSION)) {
            mLoadedIdea = Utilities.getIdeaByFileName(getApplicationContext(), mFileName);
            if (mLoadedIdea != null) {
                mEtTitle.setText(mLoadedIdea.getTitle());
                canvasView.setDrawingCacheEnabled(true);
                mIdeaCreationTime = mLoadedIdea.getDateTime();
                mIsViewingOrUpdating = true;
            }
        } else {
            mIdeaCreationTime = System.currentTimeMillis();
            mIsViewingOrUpdating = false;
        }
    }

    public void clearCanvas(View v){
        canvasView.clearCanvas();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        if(mIsViewingOrUpdating) {
            getMenuInflater().inflate(R.menu.menu_view, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_idea_new, menu);
        }
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save_idea:
            case R.id.action_update:
                validateAndSaveIdea();
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
                            Toast.makeText(PaintActivity.this, mLoadedIdea.getTitle() + " Deleted"
                                    , Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(PaintActivity.this, "can not delete the note '" + mLoadedIdea.getTitle() + "'"
                                    , Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                })
                .setNegativeButton("NO", null);

        dialogDelete.show();
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
                    || !canvasView.getDrawingCache().toString().equalsIgnoreCase(mLoadedIdea.getContent()));
        } else {
            return !mEtTitle.getText().toString().isEmpty() || !canvasView.getDrawingCache().toString().isEmpty();
        }
    }


    private void validateAndSaveIdea() {

        String title = mEtTitle.getText().toString();
        String content = canvasView.getDrawingCache().toString();

        if(title.isEmpty()) {
            Toast.makeText(PaintActivity.this, "please enter a title!"
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        if(content.isEmpty()) {
            Toast.makeText(PaintActivity.this, "please enter your idea!"
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

package com.example.yangjingqi.assignment3;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yangjingqi on 7/21/17.
 */

@TargetApi(Build.VERSION_CODES.N)
public class NoteDetail extends AppCompatActivity {

    private EditText userTitle;
    private EditText userNotes;
    private NotesEdit currentNote;
    private boolean isAdd = true;
    private String preTitle;
    private String preNote;
    private ArrayList<NotesEdit> NL = new ArrayList<NotesEdit>();

    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userTitle = (EditText) findViewById(R.id.editTitle);
        userNotes = (EditText) findViewById(R.id.editNotes);

        // Check to see if a Country object was provided in the activity's intent
        // Set up the textviews if so.
        Intent intent = getIntent();
        if (intent.hasExtra("NOTES")) {
            currentNote = (NotesEdit) intent.getSerializableExtra("NOTES");
            userTitle.setText(currentNote.getNotes_title());
            userNotes.setText(currentNote.getContent());
            userTitle.setFocusable(true);
            isAdd = false;
            preTitle = currentNote.getNotes_title();
            preNote = currentNote.getContent();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:
                String titleData = userTitle.getText().toString();
                String contentData = userNotes.getText().toString();
                String timeData = sdf.format(new Date());
                NotesEdit newNotes = new NotesEdit(titleData, timeData, contentData);
                if (titleData.length() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Title ");
                    builder.setIcon(R.drawable.icon4);
                    builder.setMessage("Your note has no title! \nAdd title or Delete note? ");
                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(NoteDetail.this, "Continue Editting Note", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(NoteDetail.this, "Notes Not Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    if (isAdd)
                        DatabaseHandler.getInstance(this).addNotes(newNotes);
                    else
                        DatabaseHandler.getInstance(this).updateNotes(newNotes);
                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final String titleData = userTitle.getText().toString();
        final String contentData = userNotes.getText().toString();
        final String timeData = sdf.format(new Date());
        if (preTitle.equals(titleData )&& preNote.equals(contentData)) {
            finish();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.icon1);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(NoteDetail.this, "Notes Saved", Toast.LENGTH_SHORT).show();
                    NotesEdit newNotes = new NotesEdit(titleData, timeData, contentData);
                    if (isAdd)
                        DatabaseHandler.getInstance(NoteDetail.this).addNotes(newNotes);
                    else
                        DatabaseHandler.getInstance(NoteDetail.this).updateNotes(newNotes);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Toast.makeText(NoteDetail.this, "Notes Not Saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            builder.setMessage("Your note is not saved! \nSave note " + titleData + " ? ");
            builder.setTitle("Save Note ");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}

package com.example.yangjingqi.assignment3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private List<NotesEdit> notesList = new ArrayList<>();  // Main content is here
    private ArrayList<NotesEdit> noteLIST = new ArrayList<>();
    private RecyclerView recyclerView; // Layout's recyclerview
    private NotesEditAdapter nAdapter; // Data to recyclerview adapter
    private static final int ADD_CODE = 1;
    private static final int UPDATE_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        nAdapter = new NotesEditAdapter(notesList, this);
        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseHandler.getInstance(this).setupDb();

        Intent newData = getIntent();
        if (getIntent().getExtras() != null){
            NotesEdit n = (NotesEdit)newData.getSerializableExtra("newData");
            noteLIST.add(0, n);
            nAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {
        new JSONTask(MainActivity.this).execute();
        DatabaseHandler.getInstance(this).dumpLog();
        ArrayList<NotesEdit> list = DatabaseHandler.getInstance(this).loadNotes();
        notesList.clear();
        notesList.addAll(list);
        Log.d(TAG, "onResume: " + list);
        nAdapter.notifyDataSetChanged();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        DatabaseHandler.getInstance(this).shutDown();
        super.onDestroy();
    }

    @Override
    protected  void  onPause() {
        try {
            saveProduct();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        try {
            saveProduct();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    /*
    Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.about_menu:
                Toast.makeText(this, "Info options", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, About_activity.class);
                startActivity(intent1);
                return true;
            case R.id.Notes_menu:
                Toast.makeText(this, "You want to create a new note", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, NoteDetail.class);
                startActivityForResult(intent2, ADD_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        NotesEdit c = notesList.get(pos);
        Intent intent = new Intent(this, NoteDetail.class);
        intent.putExtra("NOTES", c);
        startActivityForResult(intent, UPDATE_CODE);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        NotesEdit c = notesList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseHandler.getInstance(MainActivity.this).deleteNotes(notesList.get(pos).getNotes_title());
                notesList.remove(pos);
                nAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Delete Note " + notesList.get(pos).getNotes_title() + "?");
        builder.setTitle("Delete Note");

        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    /*
    Json File
     */

    private void saveProduct() throws JSONException{
        Log.d(TAG, "saveProduct: Saving JSON File***********************************");
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginObject();
            createGroupInServer();
            writer.endObject();
            writer.close();
            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
    public JSONObject createGroupInServer()throws JSONException {
        ArrayList<NotesEdit> groups = new ArrayList<>();
        JSONObject jResult = new JSONObject();
        JSONArray jArray = new JSONArray();
        for (int i = 0; i < groups.size(); i++) {
            JSONObject jGroup = new JSONObject();
            jGroup.put("MULTINOTES_TITLE", groups.get(i).getNotes_title());
            jGroup.put("MULTINOTES_TIME", groups.get(i).getTime());
            jGroup.put("MULTINOTES_CONTENT", groups.get(i).getContent());
            JSONObject jOuter = new JSONObject();
            jOuter.put("NOTES_group", jGroup);
            jArray.put(jOuter);
        }

        jResult.put("recordset", jArray);
        return jResult;
        }

    public void updateData(ArrayList<NotesEdit> cList) {
        notesList.addAll(cList);
        nAdapter.notifyDataSetChanged();
    }

    public String getFile(){
        String filename = getString(R.string.file_name);
        return filename;
    }
    public String getCode(){
        String code = getString(R.string.encoding);
        return code;
    }

    // save note data to json file
    public void saveNotes(ArrayList<NotesEdit> notes) throws JSONException {
        Log.d(TAG, "saveNotes: ==================================================");
        JSONArray jsonArray = new JSONArray();
        for (NotesEdit n : notes) {
            //toJSON, save data to json object
            jsonArray.put(n.toJSON());
        }
        Writer writer = null;
        try {
            //open file and write into
            OutputStream out = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(jsonArray.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
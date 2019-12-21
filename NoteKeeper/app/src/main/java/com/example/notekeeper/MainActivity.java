package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String NOTE_INFO = "com.example.notekeeper.NOTE_INFO";
    public static final String NOTE_POSITION = "com.example.notekeeper.NOTE_POSITION";
    private NoteInfo selectedNoteInfo;
    boolean isNewNote;
    private Spinner spinnerCourses;
    private EditText textNoteTitle;
    private EditText textNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);

        List<CourseInfo> listCourses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses = new ArrayAdapter<CourseInfo>(this,android.R.layout.simple_spinner_item,listCourses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();

        textNoteTitle = (EditText)findViewById(R.id.text_note_title);
        textNoteText = (EditText) findViewById(R.id.text_note_text);

        if(!isNewNote){
            displayNote(spinnerCourses, textNoteText, textNoteTitle);
        }

    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteText, EditText textNoteTitle) {

        textNoteTitle.setText(selectedNoteInfo.getTitle());
        textNoteText.setText(selectedNoteInfo.getText());

        List<CourseInfo> courseList = DataManager.getInstance().getCourses();
        int courseIndex = courseList.indexOf(selectedNoteInfo.getCourse());
        spinnerCourses.setSelection(courseIndex);
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        //selectedNoteInfo = intent.getParcelableExtra(NOTE_INFO);
        //isNewNote=selectedNoteInfo==null;

        int selectedNotePosition = intent.getIntExtra(NOTE_POSITION,-1);
        isNewNote = selectedNotePosition==-1;

        if(!isNewNote)
            selectedNoteInfo=DataManager.getInstance().getNotes().get(selectedNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_email) {
            sendMail();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMail() {
        CourseInfo selectedCourse = (CourseInfo)spinnerCourses.getSelectedItem();
        String subject = textNoteTitle.getText().toString();
        String text = "Your note title is"+selectedCourse.getTitle()+
                        "\n\n your note is "+textNoteText.getText().toString();
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.setType("message/rfc2822");
        intentEmail.putExtra(Intent.EXTRA_SUBJECT,subject);
        intentEmail.putExtra(Intent.EXTRA_TEXT,text);
        startActivity(intentEmail);
    }
}

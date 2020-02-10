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

    public static final String ORIGINAL_COURSE_ID = "com.example.notekeeper.ORIGINAL_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.notekeeper.ORIGINAL_NOTE_TEXT";

    public static final String NOTE_INFO = "com.example.notekeeper.NOTE_INFO";
    public static final String NOTE_POSITION = "com.example.notekeeper.NOTE_POSITION";
    private NoteInfo selectedNoteInfo;
    boolean isNewNote;
    private Spinner spinnerCourses;
    private EditText textNoteTitle;
    private EditText textNoteText;
    private boolean isCancelling;
    private int newNotePosition;
    private String originalNoteCourseId;
    private String originalNoteCourseTitle;
    private String originalNoteCourseText;
    private int selectedNotePosition;

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

        if (savedInstanceState == null)
        {
            saveOriginalNoteValues();
        }
        else
        {
            restoreOriginalStateValues(savedInstanceState);
        }
        saveOriginalNoteValues();

        textNoteTitle = (EditText)findViewById(R.id.text_note_title);
        textNoteText = (EditText) findViewById(R.id.text_note_text);

        if(!isNewNote){
            displayNote(spinnerCourses, textNoteText, textNoteTitle);
        }
    }

    private void restoreOriginalStateValues(Bundle savedInstanceState) {
        originalNoteCourseId = savedInstanceState.getString(ORIGINAL_COURSE_ID);
        originalNoteCourseTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        originalNoteCourseText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_COURSE_ID,originalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE,originalNoteCourseTitle);
        outState.putString(originalNoteCourseText,originalNoteCourseText);
    }

    private void saveOriginalNoteValues() {
        if(isNewNote)
            return;
        originalNoteCourseId = selectedNoteInfo.getCourse().getCourseId();
        originalNoteCourseTitle = selectedNoteInfo.getTitle();
        originalNoteCourseText = selectedNoteInfo.getText();
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

        selectedNotePosition = intent.getIntExtra(NOTE_POSITION,-1);
        isNewNote = selectedNotePosition ==-1;

        if(!isNewNote)
            selectedNoteInfo=DataManager.getInstance().getNotes().get(selectedNotePosition);
        else
            createNewNote();
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        newNotePosition = dm.createNewNote();
        selectedNoteInfo=dm.getNotes().get(newNotePosition);

    }

    //if user press back button or call finish() method then onPause method will execute
    @Override
    protected void onPause() {
        super.onPause();

        if(isCancelling){
            if(isNewNote){
                DataManager.getInstance().removeNote(newNotePosition);
            }
            else{
                storePreviousNoteValues();
            }
        }
        else{
            saveNote();
        }


    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(originalNoteCourseId);
        selectedNoteInfo.setCourse(course);
        selectedNoteInfo.setTitle(originalNoteCourseTitle);
        selectedNoteInfo.setText(originalNoteCourseText);
    }

    private void saveNote() {
        selectedNoteInfo.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        selectedNoteInfo.setTitle(textNoteTitle.getText().toString());
        selectedNoteInfo.setText(textNoteText.getText().toString());
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
        else if(id==R.id.action_cancel){
            isCancelling = true;
            finish();//calling the onPause() method
        }
        else if(id==R.id.action_next){
            moveNext();
        }


        return super.onOptionsItemSelected(item);
    }

    private void moveNext() {
        saveNote();
        ++selectedNotePosition;
        selectedNoteInfo = DataManager.getInstance().getNotes().get(selectedNotePosition);
        saveOriginalNoteValues();
        displayNote(spinnerCourses,textNoteText,textNoteTitle);
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

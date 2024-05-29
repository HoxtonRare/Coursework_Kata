package dao;

import model.Note;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public interface NoteDao {
    public String printHelp();

    public void createNewNote(Note note);

    public ArrayList<Note> getNotesByLabels(Set<String> labels);

    public boolean removeNote(long id);

    public boolean exportNote();

    public void exitNoteEditor();

    public ArrayList<Note> getAllNotes();
}

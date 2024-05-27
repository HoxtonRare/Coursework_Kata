package dao;

import model.Note;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public interface NoteDao {
    public void help();

    public boolean newNote(Scanner scanner);

    public ArrayList<Note> getNotes(Scanner scanner);

    public boolean removeNote(Scanner scanner);

    public boolean exportNote();

    public void exitNoteEditor();
}

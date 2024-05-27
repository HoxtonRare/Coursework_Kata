package service;

import model.Note;

import java.util.ArrayList;
import java.util.Scanner;

public interface NoteService {
    public void help();

    public boolean newNote(Scanner scanner);

    public ArrayList<Note> getNotes(Scanner scanner);

    public boolean removeNote(Scanner scanner);

    public boolean exportNote();

    public void exitNoteEditor();
}

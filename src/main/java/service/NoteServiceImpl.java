package service;

import dao.NoteDaoImpl;
import model.Note;

import java.util.ArrayList;
import java.util.Scanner;

public class NoteServiceImpl implements NoteService {
    private final NoteDaoImpl noteDao = new NoteDaoImpl();

    @Override
    public void help() {
        noteDao.help();
    }

    @Override
    public boolean newNote(Scanner scanner) {
        return noteDao.newNote(scanner);
    }

    @Override
    public ArrayList<Note> getNotes(Scanner scanner) {
        return noteDao.getNotes(scanner);
    }

    @Override
    public boolean removeNote(Scanner scanner) {
        return noteDao.removeNote(scanner);
    }

    @Override
    public boolean exportNote() {
        return noteDao.exportNote();
    }

    @Override
    public void exitNoteEditor() {
        noteDao.exitNoteEditor();
    }
}

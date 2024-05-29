package dao;

import model.Note;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.*;


public class NoteDaoImpl implements NoteDao {
    private final Logger logger = Logger.getLogger(NoteDao.class.getName());
    private ArrayList<Note> notes = new ArrayList<>();

    public NoteDaoImpl() {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.INFO);
        logger.setUseParentHandlers(false);

        try {
            FileHandler fileHandler = new FileHandler("NoteApp.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка инициализации файла для записи логов", e);
        }
    }


    @Override
    public String printHelp() {
        logger.log(Level.FINE, "Вызвана команда help");
        return "Список доступных команд: \n" +
                "help - выводит на экран список доступных команд с их описанием\n" +
                "note-new  - создать новую заметку\n" +
                "note-list - выводит все заметки на экран\n" +
                "note-remove - удаляет заметку\n" +
                "note-export - сохраняет все заметки в текстовый файл и выводит имя сохраненного файла\n" +
                "exit - выход из приложения";
    }

    @Override
    public void createNewNote(Note note) {
        notes.add(note);
    }

    @Override
    public ArrayList<Note> getNotesByLabels(Set<String> labels) {
        ArrayList<Note> matchedNotes = new ArrayList<>();
        if(notes.isEmpty()){
            logger.log(Level.INFO, "Список заметок пуст");
            System.out.println("Список заметок пуст");
            return matchedNotes;
        }
        for (Note note : notes) {
            for (String label : note.getLabels()) {
                if (labels.contains(label)) {
                    matchedNotes.add(note);
                    logger.log(Level.FINE, "Вывод заметок, которые были найдены по меткам");
                }
            }
        }
        if (matchedNotes.isEmpty()) {
            logger.log(Level.INFO, "По поиску меток ничего не было найдено");
            System.out.println("По поиску меток ничего не было найдено");
            return matchedNotes;
        } else {
            return matchedNotes;
        }
    }

    @Override
    public ArrayList<Note> getAllNotes() {
        if(notes.isEmpty()){
            logger.log(Level.INFO, "Список заметок пуст");
            System.out.println("Список заметок пуст");
            return notes;
        }
        logger.log(Level.FINE, "Вывод всех заметок, т.к. не были введены метки");
        return notes;
    }

    @Override
    public boolean removeNote(long id) {
        Iterator<Note> iterator = notes.iterator();
        while (iterator.hasNext()) {
            Note note = iterator.next();
            if (note.getId() == id) {
                iterator.remove();
                logger.log(Level.FINE, "Заметка c id " + id + " была удалена");
                System.out.println("Заметка c id " + id + " была удалена");
                return true;
            }
        }
        logger.log(Level.INFO, "Заметка с id " + id + " не найдена");
        System.out.println("Заметка с id " + id + " не найдена");
        return false;
    }

    @Override
    public boolean exportNote() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_HH-mm-ss");
        logger.log(Level.FINE, "Вызвана команда exportNote");
        String formattedDateTime = now.format(formatter);
        String filename = "notes_" + formattedDateTime + " .txt";
        File file = new File(filename);
        try {
            file.createNewFile();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка создания файла");
            throw new RuntimeException(e);
        }
        try (FileWriter writer = new FileWriter(file)) {
            for (Note note : notes) {
                writer.write(note.toString());
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка записи в файл");
            e.printStackTrace();
            return false;
        }
        System.out.println("Записная книжка успешно экспортирована в файл: " + filename);
        return true;
    }

    @Override
    public void exitNoteEditor() {
        logger.log(Level.FINE, "Вызвана команда exitNoteEditor");
        logger.log(Level.FINE, "Закрытие программы");
        System.exit(0);
    }
}

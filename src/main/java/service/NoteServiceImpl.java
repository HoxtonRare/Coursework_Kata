package service;

import dao.NoteDao;
import dao.NoteDaoImpl;
import model.Note;

import java.io.IOException;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoteServiceImpl implements NoteService {
    private final NoteDaoImpl noteDao = new NoteDaoImpl();
    final Logger logger = Logger.getLogger(NoteService.class.getName());

    public NoteServiceImpl() {
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
        return noteDao.printHelp();
    }

    @Override
    public boolean createNewNote(Scanner scanner) {
        logger.log(Level.FINE, "Вызвана команда newNote");
        System.out.println("Введите текст заметки: ");
        logger.log(Level.FINE, "Вывод сообщения о вводе текста заметки");
        String text = scanner.nextLine();
        if (this.checkTextLength(text)) {
            System.out.println("Добавить метки? " +
                    "Метки состоят из одного слова и могу содержать только буквы. " +
                    "Для добавления нескольких меток разделяйте слова пробелом.");
            logger.log(Level.FINE, "Вывод сообщения о вводе меток");
            String labelsInput = scanner.nextLine().trim();
            Set<String> labels = this.createValidateLabels(labelsInput);
            if (!labels.isEmpty()) {
                Note note = new Note(text, labels);
                noteDao.createNewNote(note);
                logger.log(Level.FINE, "Создана новая заметка: " + note.getId());
                System.out.println("Новая заметка успешно создана");
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<Note> getNotes(Scanner scanner) {
        logger.log(Level.FINE, "Вызвана команда getNotes");
        System.out.println("Введите метки, чтобы отобразить определенные заметки" +
                " или оставьте пустым для отображения всех заметок");
        logger.log(Level.FINE, "Вывод сообщения о вводе меток для поиска");
        String labelsInput = scanner.nextLine().trim();
        if (!labelsInput.isEmpty()) {
            Set<String> labels = this.createValidateLabels(labelsInput);
            if (!labels.isEmpty()) {
                return noteDao.getNotesByLabels(labels);
            } else {
                return null;
            }
        } else {
            return noteDao.getAllNotes();
        }
    }

    @Override
    public boolean removeNote(Scanner scanner) {
        logger.log(Level.FINE, "Вызвана команда removeNote");
        System.out.println("Введите id удаляемой заметки: ");
        logger.log(Level.FINE, "Вывод сообщения для ввода id удаляемой заметки");
        String num = scanner.nextLine();
        long id;
        if (this.checkForCorrectId(num)) {
            id = Long.parseLong(num);
            noteDao.removeNote(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean exportNote() {
        return noteDao.exportNote();
    }

    @Override
    public void exitNoteEditor() {
        noteDao.exitNoteEditor();
    }

    private Set<String> createValidateLabels(String labels) throws IllegalArgumentException {
        Set<String> validLabels = Stream.of(labels.split("\\s+")).map(String::toUpperCase)
                .filter(label -> {
                    if (!checkIsValidateLabel(label)) {
                        logger.log(Level.INFO, "Метка содержит что-то кроме букв");
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toSet());
        try {
            if (validLabels.isEmpty()) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Метки должны состоять только из букв");
        }
        return validLabels;
    }

    private boolean checkIsValidateLabel(String label) {
        if (label == null) {
            return false;
        }
        return label.matches("^[А-Яа-я-A-Za-z]+$");
    }

    private boolean checkTextLength(String text) {
        try {
            if (text.length() < 3) {
                logger.log(Level.INFO, "текст заметки должен быть длиннее 3 символов, введено - " + text);
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("==========================================================\n" +
                    "Текст заметки должен состоять минимум из 3-х букв.\n" +
                    "==========================================================");
            return false;
        }
        return true;
    }

    private boolean checkForCorrectId(String num) {
        try {
            long id = Long.parseLong(num);
        } catch (NumberFormatException ex) {
            System.out.println("==========================================================\n" +
                    "Нужно ввести число. Удаление заметки отменено\n" +
                    "==========================================================");
            logger.log(Level.WARNING, "Некорректный ввод id для удаления заметки");
            return false;
        }
        return true;
    }
}

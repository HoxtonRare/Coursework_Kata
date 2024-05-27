package dao;

import model.Note;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoteDaoImpl implements NoteDao{
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
    public void help() {
        System.out.println("Cписок доступных команд: ");
        System.out.println("help - Выводит на экран список доступных команд с их описанием");
        System.out.println("note-new - Создать новую заметку");
        System.out.println("note-list - Вывести все заметки на экран");
        System.out.println("note-remove - Удалить заметку");
        System.out.println("note-export - Сохранить все заметки в текстовый файл и вывести имя сохраненного файла");
        System.out.println("exit - Выход из приложения");
        logger.log(Level.FINE, "Вызвана команда help");
    }

    @Override
    public boolean newNote(Scanner scanner){
        logger.log(Level.FINE, "Вызвана команда newNote");
        System.out.println("Введите текст заметки: ");
        logger.log(Level.FINE, "Вывод сообщения о вводе текста заметки");
        String text = scanner.nextLine();
        try {
            if (text.length() < 3) {
                logger.log(Level.INFO, "текст заметки должен быть длиннее 3 символов, введено - " + text);
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("==========================================================\n" +
                    "Текст заметки должен состоять минимум из 3-х букв. Создание заметки отменено\n" +
                    "==========================================================");
            return false;
        }

        System.out.println("Добавить метки? " +
                "Метки состоят из одного слова и могу содержать только буквы. " +
                "Для добавления нескольких меток разделяйте слова пробелом.");
        logger.log(Level.FINE, "Вывод сообщения о вводе меток");
        String labelsInput = scanner.nextLine().trim();
        Set<String>labels = new HashSet<>();
        try {
        labels = this.validateLabels(labelsInput);
        if(labels.isEmpty()){
            throw new IllegalArgumentException();
        }
        } catch (IllegalArgumentException ex) {
            System.out.println("==========================================================\n" +
                    "Метки должны состоять из букв. Создание заметки отменено\n" +
                    "==========================================================");
            return false;
        }
        Note note = new Note(text, labels);
        notes.add(note);
        logger.log(Level.FINE, "Создана новая заметка: " + note.getId());
        System.out.println("Новая заметка успешно создана");
        return true;
    }

    @Override
    public ArrayList<Note> getNotes(Scanner scanner) {
        logger.log(Level.FINE, "Вызвана команда printNoteList");
        System.out.println("Введите метки, чтобы отобразить определенные заметки" +
                " или оставьте пустым для отображения всех заметок");
        logger.log(Level.FINE, "Вывод сообщения о ввод меток для поиска");
        String labelsInput = scanner.nextLine().trim();
        Set<String>labels;
        ArrayList<Note> matchedNotes = new ArrayList<>();
        try {
            if (!labelsInput.isEmpty()) {
                labels = this.validateLabels(labelsInput);
                if (labels.isEmpty()) {
                    throw new IllegalArgumentException();
                }
                for (Note note : notes) {
                    for (String label : note.getLabels()) {
                        if (labels.contains(label)) {
                            matchedNotes.add(note);
                            logger.log(Level.FINE, "Вывод заметок, которые были найдены по меткам");
                        }
                    }
                }
            } else {
                logger.log(Level.FINE, "Вывод всех заметок, т.к. по поиску меток ничего не было найдено");
                return notes;
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("==========================================================\n" +
                    "Метки должны состоять из букв. Поиск заметок отменён\n" +
                    "==========================================================");
        }
        return matchedNotes;
    }

    @Override
    public boolean removeNote(Scanner scanner) {
        logger.log(Level.FINE, "Вызвана команда removeNote");
        System.out.println("Введите id удаляемой заметки: ");
        logger.log(Level.FINE, "Вывод сообщения для ввода id удаляемой заметки");
        String num = scanner.nextLine();
        try {
            Long id = Long.parseLong(num);
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
        } catch (IllegalArgumentException e) {
            System.out.println("==========================================================\n" +
                    "Нужно ввести число. Удаление заметки отменено\n" +
                    "==========================================================");
            logger.log(Level.WARNING, "Некорректный ввод id для удаления заметки");
            return false;
        }
    }

    @Override
    public boolean exportNote() {
        logger.log(Level.FINE, "Вызвана команда exportNote");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_HH-mm-ss");
        String formattedDateTime = now.format(formatter);
        String filename = "notes_" + formattedDateTime + " .txt";
        File file = new File(filename);
        try {
            file.createNewFile();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка создания файла");
            throw new RuntimeException(e);
        }
        try(FileWriter writer = new FileWriter(file)){
            for(Note note : notes) {
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

    private Set<String> validateLabels(String labels) throws IllegalArgumentException {
            Set<String> validLabels = Stream.of(labels.split("\\s+")).map(String::toUpperCase)
                    .filter(label -> {
                        if (!isValidateLabel(label)) {
                            logger.log(Level.INFO, "Некорректный ввод текста метки");
                            throw new IllegalArgumentException();
                        }
                        return true;
                    })
                    .collect(Collectors.toSet());
        return validLabels;
    }

    private boolean isValidateLabel(String label) {
        if (label == null) {
            return false;
        }
        return label.matches("^[А-Яа-я-A-Za-z]+$");
    }
}

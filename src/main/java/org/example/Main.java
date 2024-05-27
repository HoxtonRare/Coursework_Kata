package org.example;
import dao.NoteDao;
import service.NoteServiceImpl;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        System.out.println("Это Ваша записная книжка. Вот список доступных команд: \nhelp \nnote-new " +
                "\nnote-list \nnote-remove \nnote-export \n exit");
        final Logger logger = Logger.getLogger(NoteDao.class.getName());
        logger.log(Level.FINE, "Программа запущена");
        Scanner scanner = new Scanner(System.in);
        String answer;
        NoteServiceImpl noteService = new NoteServiceImpl();
        while (true) {
            answer = scanner.nextLine();
            switch (answer) {
                case ("help"):
                    noteService.help();
                    break;
                case("note-new"):
                    noteService.newNote(scanner);
                    break;
                case("note-list"):
                    System.out.println(noteService.getNotes(scanner));;
                    break;
                case("note-remove"):
                    noteService.removeNote(scanner);
                    break;
                case("note-export"):
                    noteService.exportNote();
                    break;
                case("exit"):
                    noteService.exitNoteEditor();
                    break;
                default:
                    System.out.println("Команда не найдена");
                    logger.log(Level.WARNING, "Введена несуществующая команда");
                    break;
            }
        }
    }
}
import model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import service.NoteService;
import service.NoteServiceImpl;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class NoteServiceImplTest {
    @Mock
    private ArrayList<Note> notes = new ArrayList<>();

    @InjectMocks
    private final NoteService noteService = new NoteServiceImpl();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Создание новой заметки из введённых данных")
    void testCreateNewNote() {
        String space = " ";
        String in = String.join(System.lineSeparator(),
                "Заметка 1", "метка метка"
        ) + System.lineSeparator();

        ByteArrayInputStream testIn = new ByteArrayInputStream(in.getBytes());
        System.setIn(testIn);
        Scanner scanner = new Scanner(System.in);

        noteService.createNewNote(scanner);
        testIn = new ByteArrayInputStream(space.getBytes());
        System.setIn(testIn);
        scanner = new Scanner(System.in);
        notes = noteService.getNotes(scanner);
        assertFalse(notes.isEmpty());
    }

    @Test
    @DisplayName("Генерирование уникальных id")
    void testGenerateUniqueId() {
        int numberOfNotes = 50;
        int result = numberOfNotes * 2;
        Set<String> labels = new HashSet<>();
        Set<Long> ids = new HashSet<>();
        ArrayList<Note> notes = new ArrayList<>();
        labels.add("метка");
        labels.add("ещёметка");
        for (int i = 0; i < numberOfNotes; i++) {
            notes.add(new Note("текст", labels));
        }
        for (Note note : notes) {
            ids.add(note.getId());
        }
        assertEquals(numberOfNotes, ids.size());

        notes.clear();
        for (int i = 0; i < numberOfNotes; i++) {
            notes.add(new Note("текст", labels));
        }
        for (Note note : notes) {
            ids.add(note.getId());
        }
        assertEquals(result, ids.size());
    }

    @Test
    @DisplayName("Валидация тела заметки")
    void testValidatorTextOfNote() {
        String in = String.join(System.lineSeparator(),
                "За", "метка метка"
        ) + System.lineSeparator();
        ByteArrayInputStream testIn = new ByteArrayInputStream(in.getBytes());
        System.setIn(testIn);
        Scanner scanner = new Scanner(System.in);
        assertFalse(noteService.createNewNote(scanner));
    }

    @Test
    @DisplayName("Валидация меток")
    void testValidatorLabelOfNote() {
        String in = String.join(System.lineSeparator(),
                "Заметка", "метка21"
        ) + System.lineSeparator();
        ByteArrayInputStream testIn = new ByteArrayInputStream(in.getBytes());
        System.setIn(testIn);
        Scanner scanner = new Scanner(System.in);
        assertFalse(noteService.createNewNote(scanner));
    }

    @Test
    @DisplayName("Валидация id")
    void testValidatorIdOfNote() {
        String in = "Проверка";
        ByteArrayInputStream testIn = new ByteArrayInputStream(in.getBytes());
        System.setIn(testIn);
        Scanner scanner = new Scanner(System.in);
        assertFalse(noteService.removeNote(scanner));
    }

    @Test
    @DisplayName("Удаление заметок")
    void testRemoveNotes() {
        String space = " ";
        String id = "1";
        String in = String.join(System.lineSeparator(),
                "Заметка 1", "метка метка"
        ) + System.lineSeparator();

        ByteArrayInputStream testIn = new ByteArrayInputStream(in.getBytes());
        System.setIn(testIn);
        Scanner scanner = new Scanner(System.in);

        noteService.createNewNote(scanner);

        testIn = new ByteArrayInputStream(id.getBytes());
        System.setIn(testIn);
        scanner = new Scanner(System.in);

        noteService.removeNote(scanner);

        testIn = new ByteArrayInputStream(space.getBytes());
        System.setIn(testIn);
        scanner = new Scanner(System.in);

        notes = noteService.getNotes(scanner);
        assertTrue(notes.isEmpty());
    }

    @Test
    @DisplayName("Отображение help")
    void testPrintHelp() {
        String expected = "Список доступных команд: \n" +
                "help - выводит на экран список доступных команд с их описанием\n" +
                "note-new  - создать новую заметку\n" +
                "note-list - выводит все заметки на экран\n" +
                "note-remove - удаляет заметку\n" +
                "note-export - сохраняет все заметки в текстовый файл и выводит имя сохраненного файла\n" +
                "exit - выход из приложения";

        assertEquals(expected, noteService.printHelp());
    }
}

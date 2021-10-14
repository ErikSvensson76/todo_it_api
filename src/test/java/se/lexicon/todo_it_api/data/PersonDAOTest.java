package se.lexicon.todo_it_api.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import se.lexicon.todo_it_api.model.entity.Person;
import se.lexicon.todo_it_api.model.entity.TodoItem;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PersonDAOTest {

    public List<Person> people(){
        return Arrays.asList(
                new Person(null, "Test1", "Testsson1", LocalDate.parse("1950-01-10"), null),
                new Person(null, "Test2", "Testsson2",LocalDate.parse("1955-02-20"), null),
                new Person(null, "Test3", "Testsson3", LocalDate.parse("1960-03-10"), null),
                new Person(null, "Test4", "Testsson4",LocalDate.parse("1965-03-30"), null),
                new Person(null, "Test5", "Testsson5", LocalDate.parse("1970-04-21"), null)
        );
    }

    public List<TodoItem> todoItems(){
        return Arrays.asList(
                new TodoItem(null, "Title1", "Description1", LocalDate.parse("2021-09-11"), false, null),
                new TodoItem(null, "Title2", "Description2", LocalDate.parse("2021-09-12"), false, null),
                new TodoItem(null, "Title3", "Description3", LocalDate.parse("2021-09-13"), false, null)
        );
    }

    @Autowired
    private PersonDAO testObject;

    @Autowired
    private TestEntityManager testEntityManager;

    private List<Person> persistedPeople;
    private List<TodoItem> persistedTodoItems;

    @BeforeEach
    void setUp() {
        persistedPeople = (List<Person>) testObject.saveAll(people());
        persistedTodoItems = todoItems().stream()
                .map(testEntityManager::persist)
                .collect(Collectors.toList());

        persistedPeople.get(0).addTodoItem(persistedTodoItems.get(0));
        persistedPeople.get(1).addTodoItem(persistedTodoItems.get(1));
        persistedPeople.get(2).addTodoItem(persistedTodoItems.get(2));
        persistedPeople = (List<Person>) testObject.saveAll(persistedPeople);
        testEntityManager.flush();
    }

    @Test
    void findIdlePeople() {
        int expectedSize = 2;
        List<Person> result = testObject.findIdlePeople();
        assertEquals(expectedSize, result.size());
        assertTrue(result.stream().allMatch(person -> person.getTodoItems().isEmpty()));
    }
}
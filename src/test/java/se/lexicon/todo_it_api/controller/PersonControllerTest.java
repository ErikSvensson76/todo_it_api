package se.lexicon.todo_it_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import se.lexicon.todo_it_api.model.entity.Person;
import se.lexicon.todo_it_api.model.entity.TodoItem;
import se.lexicon.todo_it_api.model.forms.PersonForm;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
@DirtiesContext
class PersonControllerTest {

    public static final String APPLICATION_HAL_JSON = "application/hal+json";
    public static final String TODO_API_V_1_PEOPLE = "/todo/api/v1/people";
    public static final String ID = "/{id}";
    public static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain;charset=UTF-8";
    public static final String TODOS = "/todos";
    public static final String ADD = "/add";
    public static final String REMOVE = "/remove";
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    @Autowired private TestEntityManager em;
    @Autowired private WebApplicationContext webApplicationContext;

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

    private List<Person> persistedPeople;
    private List<TodoItem> persistedTodoItems;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        persistedPeople = people().stream()
                .map(em::persist)
                .collect(Collectors.toList());
        persistedTodoItems = todoItems().stream()
                .map(em::persist)
                .collect(Collectors.toList());
        persistedPeople.get(0).addTodoItem(persistedTodoItems.get(0));
        persistedPeople.get(1).addTodoItem(persistedTodoItems.get(1));
        persistedPeople.get(2).addTodoItem(persistedTodoItems.get(2));


    }

    @Test
    void contextTest() {
        assertNotNull(mockMvc);
        assertNotNull(objectMapper);
        assertNotNull(em);
        assertNotNull(webApplicationContext);
    }

    @Test
    @DisplayName("Given valid PersonForm create() successfully persist object and return expected and status 201")
    void create() throws Exception{
        PersonForm formInData = new PersonForm();
        formInData.setFirstName("Test");
        formInData.setLastName("Testsson");
        formInData.setBirthDate(LocalDate.now());


        String jsonForm = objectMapper.writeValueAsString(formInData);

        mockMvc.perform(post(TODO_API_V_1_PEOPLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonForm))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$.personId").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(formInData.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(formInData.getLastName()))
                .andExpect(jsonPath("$.todoItems").isEmpty());
    }

    @Test
    @DisplayName("Given form with invalid data create() throws MethodArgumentNotValidException and redirects")
    void create_validation_error() throws Exception{
        PersonForm form = new PersonForm();
        form.setFirstName("E");
        form.setLastName(null);

        String jsonForm = objectMapper.writeValueAsString(form);

        mockMvc.perform(post(TODO_API_V_1_PEOPLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonForm))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Given search param 'all' return status ok with 5 elements")
    void find_all() throws Exception {
        String search = "all";
        mockMvc.perform(get(TODO_API_V_1_PEOPLE).param("search", search))
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.['personDTOList'].length()").value(5));
    }

    @Test
    @DisplayName("Given search param 'idle' return status ok with 2 elements")
    void find_all_idle() throws Exception{
        String search = "idle";
        mockMvc.perform(get(TODO_API_V_1_PEOPLE).param("search", search))
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.personDTOList.length()").value(2));
    }



    @Test
    @DisplayName("Given id as pathVariable findById return status ok and expected json")
    void findById() throws Exception {
        Person person = persistedPeople.get(2);
        Integer pathVariable = person.getPersonId();

        mockMvc.perform(get(TODO_API_V_1_PEOPLE+ID, pathVariable))
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personId").value(person.getPersonId()))
                .andExpect(jsonPath("$.firstName").value(person.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(person.getLastName()))
                .andExpect(jsonPath("$.todoItems.length()").value(1));
    }

    @Test
    @DisplayName("Given id as pathVariable findById return status notFound")
    void findById_not_found() throws Exception {
        Integer id = 345;

        mockMvc.perform(get(TODO_API_V_1_PEOPLE+ID, id))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given PersonForm update() return status ok and return expected json")
    void update_success() throws Exception{
        Integer id = persistedPeople.get(0).getPersonId();
        String firstName = "Arnold";
        String lastName = "Schwarzenegger";
        PersonForm personForm = new PersonForm();
        personForm.setFirstName(firstName);
        personForm.setLastName(lastName);

        String jsonForm = objectMapper.writeValueAsString(personForm);

        mockMvc.perform(put(TODO_API_V_1_PEOPLE+ID, id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonForm))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName));

    }

    @Test
    @DisplayName("Given form with invalid data update() throws MethodArgumentNotValidException and redirects")
    void update_badRequest() throws Exception {
        Integer id = persistedPeople.get(0).getPersonId();
        String firstName = "A";
        String lastName = "S";
        PersonForm personForm = new PersonForm();
        personForm.setFirstName(firstName);
        personForm.setLastName(lastName);

        String jsonForm = objectMapper.writeValueAsString(personForm);

        mockMvc.perform(put(TODO_API_V_1_PEOPLE+ID, id).contentType(MediaType.APPLICATION_JSON).content(jsonForm))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Given id delete() return status ok and expected String")
    void deletePerson() throws Exception {
        Integer id = persistedPeople.get(0).getPersonId();
        String expected = "Person with id " + id + " was deleted";

        mockMvc.perform(delete(TODO_API_V_1_PEOPLE+ID, id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_PLAIN_CHARSET_UTF_8))
                .andExpect(content().string(expected));
    }

    @Test
    @DisplayName("Given id getTodoItems() return status ok and return expected json")
    void getTodoItems() throws Exception {
        Integer id = persistedPeople.get(0).getPersonId();

        mockMvc.perform(get(TODO_API_V_1_PEOPLE+ID+TODOS, id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$._embedded.todoItemDTOList.length()").value(1));
    }

    @Test
    @DisplayName("Given id and todoId assignTodoItem return status ok and return expected json")
    void assignTodoItem() throws Exception {
        TodoItem newTodoItem = new TodoItem(null, "Title4", "Description4", LocalDate.parse("2021-10-12"), false, null);
        newTodoItem = em.persistAndFlush(newTodoItem);
        Integer todoId = newTodoItem.getTodoId();
        Integer id = persistedPeople.get(0).getPersonId();

        mockMvc.perform(put(TODO_API_V_1_PEOPLE+ID+TODOS+ADD, id).param("todoId", todoId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$.personId").value(id))
                .andExpect(jsonPath("$.todoItems.length()").value(2));
    }

    @Test
    @DisplayName("given id and todoId removeTodoItem return status ok and return expected json")
    void removeTodoItem() throws Exception {
        Integer todoId = persistedTodoItems.get(0).getTodoId();
        Integer id = persistedPeople.get(0).getPersonId();

        mockMvc.perform(put(TODO_API_V_1_PEOPLE+ID+TODOS+REMOVE, id).param("todoId", todoId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_HAL_JSON))
                .andExpect(jsonPath("$.personId").value(id))
                .andExpect(jsonPath("$.todoItems.length()").value(0));
    }
}
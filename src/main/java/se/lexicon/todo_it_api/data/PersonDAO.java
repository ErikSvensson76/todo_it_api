package se.lexicon.todo_it_api.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.lexicon.todo_it_api.model.entity.Person;

import java.util.List;

public interface PersonDAO extends CrudRepository<Person, Integer> {
    @Query("SELECT p FROM Person p WHERE p.todoItems.size = 0")
    List<Person> findIdlePeople();

}

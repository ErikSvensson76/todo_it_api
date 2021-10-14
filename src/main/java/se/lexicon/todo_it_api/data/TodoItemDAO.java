package se.lexicon.todo_it_api.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import se.lexicon.todo_it_api.model.entity.TodoItem;

import java.time.LocalDate;
import java.util.List;

public interface TodoItemDAO extends CrudRepository<TodoItem, Integer> {
    @Query("SELECT t FROM TodoItem t WHERE UPPER(t.title) LIKE UPPER(CONCAT('%',:string,'%'))")
    List<TodoItem> findByTitleContains(@Param("string") String string);
    @Query("SELECT t FROM TodoItem t WHERE t.assignee.personId = :personId")
    List<TodoItem> findByPersonId(@Param("personId") Integer personId);
    @Query("SELECT t FROM TodoItem t WHERE t.done = :doneStatus")
    List<TodoItem> findByDoneStatus(@Param("doneStatus") boolean doneStatus);
    @Query("SELECT t FROM TodoItem t WHERE t.deadLine BETWEEN :start AND :ending")
    List<TodoItem> findByDeadlineBetween(@Param("start") LocalDate start, @Param("ending") LocalDate end);
    @Query("SELECT t FROM TodoItem t WHERE t.deadLine < :date")
    List<TodoItem> findByDeadLineBefore(@Param("date") LocalDate localDate);
    @Query("SELECT t FROM TodoItem t WHERE t.deadLine > :date")
    List<TodoItem> findByDeadlineAfter(@Param("date") LocalDate localDate);
    @Query("SELECT t FROM TodoItem t WHERE t.assignee IS null")
    List<TodoItem> findUnassignedTodoItems();
    @Query("SELECT t FROM TodoItem t WHERE t.done = false AND current_date > t.deadLine")
    List<TodoItem> findAllUnfinishedAndOverdue();
}

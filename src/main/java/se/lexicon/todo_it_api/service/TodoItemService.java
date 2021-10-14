package se.lexicon.todo_it_api.service;

import se.lexicon.todo_it_api.model.dto.TodoItemDTO;
import se.lexicon.todo_it_api.model.forms.TodoItemForm;

import java.time.LocalDate;
import java.util.List;

public interface TodoItemService {

    TodoItemDTO create(TodoItemForm form);
    TodoItemDTO findById(Integer id);
    List<TodoItemDTO> findAll();
    List<TodoItemDTO> findAllUnassigned();
    List<TodoItemDTO> findAllByPersonId(Integer personId);
    List<TodoItemDTO> findByDoneStatus(boolean doneStatus);
    List<TodoItemDTO> findByDeadlineBetween(LocalDate start,LocalDate end);
    List<TodoItemDTO> findByDeadlineBefore(LocalDate localDate);
    List<TodoItemDTO> findByDeadlineAfter(LocalDate localDate);
    List<TodoItemDTO> findByTitle(String title);
    List<TodoItemDTO> findAllUnfinishedAndOverdue();
    TodoItemDTO update(Integer id, TodoItemForm form);
    boolean delete(Integer id);

}

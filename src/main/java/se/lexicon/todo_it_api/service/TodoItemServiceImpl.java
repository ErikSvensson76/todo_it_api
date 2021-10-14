package se.lexicon.todo_it_api.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.todo_it_api.data.TodoItemDAO;
import se.lexicon.todo_it_api.exception.AppResourceNotFoundException;
import se.lexicon.todo_it_api.model.dto.TodoItemDTO;
import se.lexicon.todo_it_api.model.entity.TodoItem;
import se.lexicon.todo_it_api.model.forms.TodoItemForm;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoItemServiceImpl  implements TodoItemService{

    private final TodoItemDAO todoItemDAO;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public TodoItemDTO create(TodoItemForm form) {
        TodoItem todoItem = todoItemDAO.save(new TodoItem(null, form.getTitle().trim(), form.getDescription().trim(), form.getDeadLine(), false, null));
        return convert(todoItem);
    }

    @Override
    @Transactional(readOnly = true)
    public TodoItemDTO findById(Integer id) {
        return todoItemDAO.findById(id)
                .map(this::convert)
                .orElseThrow(() -> new AppResourceNotFoundException("Could not find todo item with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoItemDTO> findAll() {
        List<TodoItem> todoItems = (List<TodoItem>) todoItemDAO.findAll();
        return todoItems.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoItemDTO> findAllUnassigned() {
        return todoItemDAO.findUnassignedTodoItems().stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoItemDTO> findAllByPersonId(Integer personId) {
        return todoItemDAO.findByPersonId(personId).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoItemDTO> findByDoneStatus(boolean doneStatus) {
        return todoItemDAO.findByDoneStatus(doneStatus).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoItemDTO> findByDeadlineBetween(LocalDate start, LocalDate end) {
        return todoItemDAO.findByDeadlineBetween(start, end).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoItemDTO> findByDeadlineBefore(LocalDate localDate) {
        return todoItemDAO.findByDeadLineBefore(localDate).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoItemDTO> findByDeadlineAfter(LocalDate localDate) {
        return todoItemDAO.findByDeadlineAfter(localDate).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoItemDTO> findByTitle(String title) {
        return todoItemDAO.findByTitleContains(title).stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoItemDTO> findAllUnfinishedAndOverdue() {
        return todoItemDAO.findAllUnfinishedAndOverdue().stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TodoItemDTO update(Integer id, TodoItemForm form) {
        TodoItem todoItem = todoItemDAO.findById(id)
                .orElseThrow(() -> new AppResourceNotFoundException("Could not find todo item with id " + id));
        todoItem.setTitle(form.getTitle().trim());
        todoItem.setDescription(form.getDescription().trim());
        todoItem.setDeadLine(form.getDeadLine());
        todoItem.setDone(form.isDone());

        todoItem = todoItemDAO.save(todoItem);
        return convert(todoItem);
    }

    @Override
    @Transactional
    public boolean delete(Integer id) {
        TodoItem todoItem = todoItemDAO.findById(id)
                .orElseThrow(() -> new AppResourceNotFoundException("Could not find todo item with id " + id));

        todoItem.setAssignee(null);
        todoItemDAO.delete(todoItem);
        return !todoItemDAO.findById(id).isPresent();
    }


    public TodoItemDTO convert(TodoItem todoItem){
        return modelMapper.map(todoItem, TodoItemDTO.class);
    }
}

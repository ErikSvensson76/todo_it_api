package se.lexicon.todo_it_api.exception;

public class AppResourceNotFoundException extends RuntimeException{
    public AppResourceNotFoundException(String message) {
        super(message);
    }
}

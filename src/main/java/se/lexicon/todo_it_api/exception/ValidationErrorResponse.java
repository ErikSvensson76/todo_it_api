package se.lexicon.todo_it_api.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationErrorResponse extends GeneralExceptionResponse{
    private final List<Violation> violations;

    public ValidationErrorResponse(GeneralExceptionResponse response, List<Violation> violations) {
        super(response.getTimeStamp(), response.getStatus(), response.getError(), response.getMessage(), response.getPath());
        this.violations = violations;
    }
}

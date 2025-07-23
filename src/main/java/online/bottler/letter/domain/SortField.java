package online.bottler.letter.domain;

import lombok.Getter;
import online.bottler.letter.domain.exception.InvalidSortFieldException;

@Getter
public enum SortField {
    CREATED_AT("createdAt");

    private final String field;

    SortField(String field) {
        this.field = field;
    }

    public static void validateSort(String field) {
        boolean isValid = false;
        for (SortField sortField : SortField.values()) {
            if (sortField.field.equalsIgnoreCase(field)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new InvalidSortFieldException();
        }
    }
}

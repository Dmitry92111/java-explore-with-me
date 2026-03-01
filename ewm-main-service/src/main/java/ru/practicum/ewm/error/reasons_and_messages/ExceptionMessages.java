package ru.practicum.ewm.error.reasons_and_messages;

import java.util.Map;

public class ExceptionMessages {
    private ExceptionMessages() {
    }

    public static final String DEFAULT_FIELD_S_ERROR_S_VALUE_S_MESSAGE =
            "Field: %s. Error: %s. Value: %s";

    public static final String START_DATE_AFTER_END_DATE = "Start date cannot be after end date";

    //Incorrect request
    public static final String VALIDATION_FAILED = "Validation failed.";
    public static final String INCORRECT_HTTP_REQUEST_BODY = "Request body is invalid or malformed.";
    public static final String MISMATCH_OF_TYPES_OF_PARAMETER_OF_REQUEST_AND_METHOD_ARGUMENT =
            "Parameter '%s' has invalid value '%s'.";
    public static final String MISSING_REQUIRED_PARAMETER_OF_HTTP_REQUEST = "Required parameter: '%s' is missing.";
    public static final String INCORRECT_REQUEST = "Incorrect request.";

    //Internal server error
    public static final String INTERNAL_SERVER_ERROR = "Something went wrong";


    //DataBase exceptions
    public static final String DATA_INTEGRITY_VIOLATION = "The operation violates data integrity rules.";
    public static final String INTEGRITY_CONSTRAINT_VIOLATED = "Integrity constraint has been violated.";
    public static final Map<String, String> CVE_CONSTRAINT_TO_MESSAGE = Map.of(
            "users_email_uq_active", "Email already exists",
            "uq_category_name", "Category name must be unique",
            "uq_request_event_requester", "Request already exists for this event and requester"
    );

    //Category
    public static final String CATEGORY_NOT_FOUND = "Category with id=%d was not found";
    public static final String CATEGORY_NOT_EMPTY_CANNOT_BE_DELETED = "The category is not empty";

    //User
    public static final String USER_NOT_FOUND = "User with id=%d was not found";

    //Event
    public static final String EVENT_NOT_FOUND = "Event with id=%d was not found";
    public static final String SHOULD_BE_IMPOSSIBLE_TO_UPDATE_PUBLISHED_EVENT =
            "Only pending or canceled events can be changed";
    public static final String SHOULD_BE_IMPOSSIBLE_TO_SEND_PENDING_REQUEST_TO_REVIEW =
            "Your request is already on a review";
    public static final String SHOULD_BE_IMPOSSIBLE_TO_CANCEL_CANCELLED_REQUEST =
            "Your request is already canceled";
    public static final String UNKNOWN_EVENT_STATE = "Unknown state of event: %s";
    public static final String ONLY_PENDING_EVENT_CAN_BE_PUBLISHED = "Only pending events can be published";
    public static final String SHOULD_BE_IMPOSSIBLE_TO_REJECT_PUBLISHED_EVENT =
            "Sorry, but you cannot reject published event";

    public static final String CONFIRMATION_OF_PARTICIPATION_REQUEST_IS_NOT_REQUIRED =
            "Confirmation of requests is not required for this event";
    public static final String YOU_CAN_CHANGE_STATUS_PENDING_PARTICIPATION_REQUESTS_ONLY =
            "Sorry, but you can change status of pending participation requests only";
    public static final String SOME_REQUESTS_DO_NOT_MATCH_WITH_PROVIDED_EVENT_ID =
            "Some participation requests were not found for this event";
    public static final String SOME_PARTICIPATION_REQUEST_NOT_FOUND = "Some participation requests were not found";

    //Participation request
    public static final String PARTICIPATION_REQUEST_NOT_FOUND = "Request with id=%d was not found";
    public static final String INITIATOR_CANNOT_CREATE_REQUEST_TO_HIS_OWN_EVENT =
            "You cannot create request to your event with id = %d";
    public static final String SHOULD_BE_IMPOSSIBLE_TO_CREATE_PARTICIPATION_REQUEST_TO_NOT_PUBLISHED_EVENT =
            "We cannot find this event among published ones";
    public static final String CANNOT_CREATE_OR_CONFIRM_PARTICIPATION_REQUEST_WHEN_REQUEST_LIMIT_HAS_BEEN_REACHED =
            "The participant limit has been reached";

    //Compilation
    public static final String COMPILATION_NOT_FOUND = "Compilation with id=%d was not found";
    public static final String SOME_EVENTS_IN_NEW_COMPILATION_DO_NOT_EXIST =
            "Your compilation contains ids which are not exist";
}

5. They are returning AsyncResult
get(): Waits for the task to complete and returns the result.
status: Returns the current status of the task (e.g., PENDING, STARTED, SUCCESS, FAILURE).
ready(): Returns True if the task has finished processing.
successful(): Returns True if the task completed successfully.
failed(): Returns True if the task raised an exception.
traceback: Returns the traceback of the task if it failed.


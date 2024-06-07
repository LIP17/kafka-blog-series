

4. apply_async vs delay
apply_async has more parameters
countdown: Delay execution for a specified number of seconds. relative time
eta: Execute the task at a specific time (ETA - Estimated Time of Arrival). specific time
expires: Set an expiration time for the task.
retry: Enable or disable automatic retries.
priority: Set the priority of the task.
routing_key: Specify a routing key to control which queue the task is sent to.
queue: Specify the queue to send the task to.

5. They are returning AsyncResult
get(): Waits for the task to complete and returns the result.
status: Returns the current status of the task (e.g., PENDING, STARTED, SUCCESS, FAILURE).
ready(): Returns True if the task has finished processing.
successful(): Returns True if the task completed successfully.
failed(): Returns True if the task raised an exception.
traceback: Returns the traceback of the task if it failed.


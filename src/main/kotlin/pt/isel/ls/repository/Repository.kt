package pt.isel.ls.repository

/**
 * Generic Interface for a repository.
 */
interface Repository<T> {
    /**
     * Function that updates an element.
     * Returns the updated element.
     */
    fun save(element: T): T

    /**
     * Function that finds a specific element.
     * If it's found, returns the element, otherwise returns null.
     */
    fun findByIdentifier(element: T): T?

    /**
     * Function that finds every element.
     * Returns a list with the elements (if no element is on the column yet, returns an empty list).
     */
    fun findAll(): List<T>

    /**
     * Function that deletes an element.
     * Returns the deleted element.
     */
    fun deleteByIdentifier(element: T): T
}

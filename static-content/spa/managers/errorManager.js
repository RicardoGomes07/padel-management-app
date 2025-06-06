const errorManager = (() => {
    const errorContent = document.getElementById("errorContent")
    let error = null

    /**
     * Stores an error view in the error manager.
     * @param errorView
     */
    function store(errorView) {
        if (!errorContent) throw "Missing error content element in the DOM"
        error = errorView
        return this
    }

    /**
     * Clears the stored error view in the error manager.
     */
    function clear() {
        if (!errorContent) throw "Missing error content element in the DOM"
        error = null
        errorContent.replaceChildren()
    }

    /**
     * Checks if there is an error stored in the error manager.
     * @returns {boolean}
     */
    function hasError() {
        return error !== null
    }

    /**
     * Renders the error view if there is an error stored.
     * This function should be called after the error is stored.
     */
    function render() {
        if (errorContent && error) {
            errorContent.replaceChildren(error)
        }
        error = null // Set error to null after rendering to avoid re-rendering the same error after handler execution
    }

    return {
        store,
        clear,
        hasError,
        render
    }
})()

export default errorManager

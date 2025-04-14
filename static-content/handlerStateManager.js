let lastBasePath = null
const routeState = {}

const MAX_SIZE = 40

function resetRouteState() {
    // only delete if it's not already empty
    if(Object.keys(routeState).length !== 0) {
        // should only exist one entry at a time, but use a 'for' for safety
        for (const key in routeState) {
            delete routeState[key]
        }
    }
}

function next() {
    pushCurrToPrev()

    const removedValues = routeState.next.splice(0, routeState.curr.length)
    routeState.curr = [...removedValues]
}

function prev() {
    pushCurrToNext()

    const removedValues = routeState.prev.splice(-routeState.curr.length)
    routeState.curr = [...removedValues]
}

function onLinkClick(action) {
    if (action === "next") {
        next()
    } else {
        prev()
    }
}

function pushCurrToPrev() {
    if(routeState.prev.length > MAX_SIZE){
        routeState.prev.splice(0, routeState.curr.length)
    }
    routeState.prev.push(...routeState.curr)
}

function pushCurrToNext() {
    if(routeState.next.length > MAX_SIZE){
        routeState.next.splice(-routeState.curr.length)
    }
    routeState.next.unshift(...routeState.curr)
}

function setStateValue(values, totalElements){
    const halfLength = Math.ceil(values.length / 2)
    if (Object.keys(routeState).length === 0){
        routeState.path = lastBasePath
        routeState.curr = values.slice(0, halfLength)
        routeState.next = values.slice(halfLength)
        routeState.prev = []
        routeState.totalElements = totalElements
    } else {
        // if next is empty, add first half to curr and second to next
        pushCurrToPrev()
        routeState.curr = values.slice(0, halfLength)
        routeState.next = values.slice(halfLength)
    }
}

function clearIfNeeded(currentPath){
    const basePath = currentPath.split("?")[0]

    if(lastBasePath !== basePath){
        resetRouteState()
    }

    lastBasePath = basePath
}

const routeStateManager = {
    routeState,
    lastBasePath,
    onLinkClick,
    clearIfNeeded,
    setStateValue,
}

export default routeStateManager
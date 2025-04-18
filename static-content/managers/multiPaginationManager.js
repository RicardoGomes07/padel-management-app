import { createPaginationManager } from "./paginationManager.js"

export function createMultiPaginationManager(fetchFunWithId, jsonProp, maxCacheSize = 100) {
    const managers = {}

    const getManager = (id) => {
        if (!managers[id]) {
            const adaptedFetch = (skip, limit) => fetchFunWithId(id, skip, limit)
            managers[id] = createPaginationManager(adaptedFetch, jsonProp, maxCacheSize)
        }
        return managers[id]
    }

    return {
        async getPage(id, skip, limit, onError) {
            return await getManager(id).getPage(skip, limit, onError)
        },
        getTotal(id) {
            return getManager(id).getTotal()
        }
    }
}

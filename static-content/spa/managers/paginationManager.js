import {ELEMS_PER_PAGE} from "../handlers/views/pagination.js";

export const MAX_CACHE_SIZE = 100

export function createPaginationManager(fetchFun, jsonProp, maxCacheSize = MAX_CACHE_SIZE) {
    const cache = []
    let count = 0

    let dynamicParams = []
    let currentFilterProp = null
    let currentFilterValue = null

    const getElements = (skip, limit) => {
        return [cache.slice(skip, skip + limit), count]
    }

    const updateCache = (items) => {
        cache.push(...items)

        if (cache.length > maxCacheSize) {
            const overflow = cache.length - maxCacheSize
            cache.splice(0, overflow)
        }
    }

    const fetchAndCache = async (page, onError) => {
        const cacheSize = cache.length

        const skip = (page - 1) * ELEMS_PER_PAGE

        const needed = (page * ELEMS_PER_PAGE) - cacheSize

        if (needed > 0) {
            try {
                const res = await fetchFun(...dynamicParams, cacheSize, needed)

                if (res.status !== 200) {
                    onError(res.data)
                    return []
                }

                const items = res.data.items[jsonProp] ?? []
                count = res.data.count

                updateCache(items)
            } catch (err) {
                onError(err.message ?? "Unknown error occurred.")
                return []
            }
        }

        return getElements(skip, ELEMS_PER_PAGE)
    }

    return {
        reqParams(...params) {
            dynamicParams = params
            return this
        },

        resetCacheIfNeeded(propName, propValue) {
            if (currentFilterProp !== propName || currentFilterValue !== propValue) {
                cache.length = 0 // reset cache se filtro mudou
                currentFilterProp = propName
                currentFilterValue = propValue
            }
            return this
        },

        async getPage(page, onError) {
            return await fetchAndCache(page, onError)
        },
    }
}


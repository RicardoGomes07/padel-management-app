export function createPaginationManager(fetchFun, jsonProp, maxCacheSize = 100) {
    const cache = []

    let dynamicParams = []
    let currentFilterProp = null
    let currentFilterValue = null
    let hasNext = false

    const getElements = (skip, limit) => {
        hasNext = cache.length > (skip + limit)
        return cache.slice(skip, skip + limit)
    }

    const updateCache = (items) => {
        cache.push(...items)

        if (cache.length > maxCacheSize) {
            const overflow = cache.length - maxCacheSize
            cache.splice(0, overflow)
        }
    }

    const fetchAndCache = async (userSkip, limit, onError) => {
        const cacheSize = cache.length

        const needed = (userSkip + limit + 1) - cacheSize

        if (needed > 0) {
            try {
                const res = await fetchFun(...dynamicParams, cacheSize, needed)

                if (res.status !== 200) {
                    onError(res.data)
                    hasNext = false
                    return []
                }

                const items = res.data[jsonProp] ?? []

                updateCache(items)
            } catch (err) {
                onError(err.message ?? "Unknown error occurred.")
                return []
            }
        }

        return getElements(userSkip, limit)
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

        async getPage(skip, limit, onError) {
            return await fetchAndCache(skip, limit, onError)
        },

        hasNext() {
            return hasNext
        }
    }
}


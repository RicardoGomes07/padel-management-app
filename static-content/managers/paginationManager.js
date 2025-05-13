export function createPaginationManager(fetchFun, jsonProp, maxCacheSize = 100) {
    const cache = []

    let dynamicParams = []
    let currentFilterProp = null
    let currentFilterValue = null
    let hasNext = false

    const filteredElems = () => {
        return currentFilterProp
            ? cache.filter(item => item?.[currentFilterProp] === currentFilterValue)
            : cache
    }

    const getElements = (skip, limit) => {
        const filtered = filteredElems()
        const filteredCount = filtered.length
        hasNext = filteredCount > (skip + limit)
        return filtered.slice(skip, skip + limit)
    }

    const updateCache = (items) => {
        cache.push(...items)

        if (cache.length > maxCacheSize) {
            const overflow = cache.length - maxCacheSize
            cache.splice(0, overflow)
        }
    }

    const fetchAndCache = async (userSkip, limit, onError) => {
        const filtered = filteredElems()
        const filteredCount = filtered.length

        const needed = (userSkip + limit + 1) - filteredCount
        console.log(needed)

        if (needed > 0) {
            try {
                const res = await fetchFun(...dynamicParams, filteredCount, needed)

                if (res.status !== 200) {
                    onError(res.data)
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

        filterBy(propName, propValue) {
            if (currentFilterProp !== propName || currentFilterValue !== propValue) {
                cache.length = 0 // reset cache se filtro mudou
            }
            currentFilterProp = propName
            currentFilterValue = propValue
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


export function createPaginationManager(fetchFun, jsonProp, maxCacheSize = 100) {
    const cache = []
    let total = 0

    const hasElementsCached = (skip, limit) => {
        const cached = cache.slice(skip, skip + limit)
        return cached.length === limit && !cached.includes(undefined)
    }

    const getElements = (skip, limit) => {
        return cache.slice(skip, skip + limit)
    }

    const updateCache = (items, skip) => {
        for (let i = 0; i < items.length; i++) {
            cache[skip + i] = items[i]
        }

        if (cache.length > maxCacheSize) {
            const overflow = cache.length - maxCacheSize
            cache.splice(0, overflow)
        }
    }

    const fetchAndCache = async (skip, limit, onError) => {

        if (total !== 0 && limit >= total) {
            return getElements(skip, limit)
        }

        if (hasElementsCached(skip, limit) ) {
            return getElements(skip, limit)
        }

        try {
            const res = await fetchFun(skip, limit)

            if (res.status !== 200) {
                onError(res.data)
                return
            }
            const items = res.data[jsonProp] ?? []
            total = res.data.paginationInfo.totalElements

            updateCache(items, skip)
            return getElements(skip, limit)

        } catch (err) {
            onError(err.message || "Unknown error occurred.")
        }
    }

    return {
        async getPage(skip, limit, onError) {
            return await fetchAndCache(skip, limit, onError)
        },
        getTotal() {
            return total
        }
    }
}

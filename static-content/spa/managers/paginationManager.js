import {ELEMS_PER_PAGE} from "../handlers/views/pagination.js";

export function createPaginationManager(fetchFun, jsonProp) {
    let count = 0

    let dynamicParams = []

    const fetchElems = async (page, onError) => {
        try {
            const skip = (page - 1) * ELEMS_PER_PAGE
            const res = await fetchFun(...dynamicParams, skip, ELEMS_PER_PAGE)

            if (res.status !== 200) {
                onError(res.data)
                return []
            }

            const items = res.data.items[jsonProp] ?? []
            count = res.data.count
            return items
        } catch (err) {
            onError(err.message ?? "Unknown error occurred.")
            return []
        }
    }

    return {
        reqParams(...params) {
            dynamicParams = params
            return this
        },
        async getPage(page, onError) {
            return [await fetchElems(page, onError), count]
        }
    }
}
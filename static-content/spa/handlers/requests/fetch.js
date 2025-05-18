export function handleResponse(res) {
    const status = res.status
    return res.text().then(text => {
        try {
            const data = text ? JSON.parse(text) : undefined
            return { status, data }
        } catch (e) {
            return { status, data: undefined }
        }
    })
}
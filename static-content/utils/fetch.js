export function handleResponse(res) {
    const status = res.status
    return res.json().then(data => ({ status, data }))
}
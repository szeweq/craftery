const api = (path, ...r) => fetch(`//localhost:3000/api/${path}`, ...r).then(r => r.json())

export default api